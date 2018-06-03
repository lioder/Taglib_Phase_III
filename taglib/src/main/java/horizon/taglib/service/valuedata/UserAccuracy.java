package horizon.taglib.service.valuedata;

import horizon.taglib.dao.TagDao;
import horizon.taglib.dao.TaskPublisherDao;
import horizon.taglib.dao.TaskWorkerDao;
import horizon.taglib.dao.UserDao;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.TagDescType;
import horizon.taglib.enums.TagType;
import horizon.taglib.enums.TaskState;
import horizon.taglib.model.*;
import horizon.taglib.service.impl.TaskServiceImpl;
import horizon.taglib.utils.CenterTag;
import horizon.taglib.utils.SparkUtil;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.feature.Word2VecModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 根据标注的所有数据自动调整用户的准确度
 */

@Service
@Component
public class UserAccuracy {
    @Autowired
    private TaskPublisherDao taskPublisherDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TaskServiceImpl taskServiceImpl;

    @Autowired
    private TaskWorkerDao taskWorkerDao;

    @Autowired
    private TagDao tagDao;

    private SparkUtil sparkUtil;
    JavaSparkContext context;

    @Autowired
    public UserAccuracy(SparkUtil sparkUtil){
        this.sparkUtil = sparkUtil;
        context = sparkUtil.getSparkContext();
    }

    /**
     *
     * @param taskPublisherId 发布者任务编号
     * @return key:图片名；value:该张图片标注的所有标准标签坐标和描述（左上点X,左上点Y,右下点X,右下点Y,描述）
     * Map<String,List<List<Object>>>
     */
    public ResultMessage adjustUserAccuracy(long taskPublisherId){
        List<RecTag> tags = getTags(taskPublisherId);
        TaskPublisher taskPublisher = taskPublisherDao.findOne(taskPublisherId);
        double pointsPerPerson = taskPublisher.getPrice()/taskPublisher.getNumberPerPicture();
        List<String> fileNames = taskPublisher.getImages();

        //得到所有接过该任务并且提交过任务的UserId
        List<Long> postuserIds = new ArrayList<>();
        for(RecTag recTag:tags){
            postuserIds.add(recTag.getUserId());
        }
        //筛去重复userID
        Set set = new HashSet();
        List<Long> userIds = new ArrayList<>();
        set.addAll(postuserIds);
        userIds.addAll(set);

        //userCorrectTagsNum为User对应在该任务中标对的tag数
        Map<Long,Integer> userCorrectTagsNum = new HashMap<>();
        for(Long userId:userIds){
            userCorrectTagsNum.put(userId,0);
        }
        //标准Tag总数
        int standardTags = 0;

        //结果map
        Map<String,List<CenterTag>> resCoordinate = new HashMap<>();

        for(String fileName:fileNames){
            List<CenterTag> eachFileTags = new ArrayList<>();//该张图片训练出的标签坐标和描述集合
            List<RecTag> recTags = new ArrayList<>();
            //找到该fileName的recTag,放在recTags
            for(RecTag recTag:tags){
                if(recTag.getFileName().equals(fileName)){
                    recTags.add(recTag);
                }
            }
            //以TaskWorkerId对recTags进行分组
            Map<Long,List<RecTag>> recTagMap = new HashMap<>();
            for(RecTag recTag:recTags){
                List<RecTag> tempList = recTagMap.get(recTag.getUserId());
                //之前没有加过数据
                if(tempList==null){
                    tempList = new ArrayList<>();
                    tempList.add(recTag);
                    recTagMap.put(recTag.getUserId(),tempList);
                }else{//之前加过数据
                    tempList.add(recTag);
                }
            }
            //根据图片和不同的用户Id分组后
            //判断该图片标注的众数为多少，作为聚类有多少类
            List<Integer> data = new ArrayList<>();//data为不同用户在一张图上标记的数量集合
            Collection<List<RecTag>> vs = recTagMap.values();
            Iterator<List<RecTag>> it = vs.iterator();
            while(it.hasNext()){
                List<RecTag> value = it.next();
                data.add(value.size());
            }

            HashSet<Integer> uniqueData = new HashSet<Integer>(data);
            HashMap<Integer,Integer> res = new HashMap<Integer,Integer>();
            int[] count = new int[uniqueData.size()];
            int index = 0;
            for(Integer integer:uniqueData){
                for(Integer num:data){
                    if(num.equals(integer)){
                        count[index]++;
                    }
                }
                res.put(count[index],integer);
                index++;
            }
            int max=0;
            for(int i:count){
                max=Math.max(i,max);
            }
            //max=5,mode=3
            if(max!=0){
                int mode = res.get(max);//mode为众数
                standardTags = standardTags + mode;
                //需要进行Tags左上点，右下点的聚类
                List<Vector> vectors = new ArrayList<>();
                for(RecTag recTag:recTags){
                    double[] values = new double[]{recTag.getStart().getX(),recTag.getStart().getY(),recTag.getEnd().getX(),recTag.getEnd().getY()};
                    vectors.add(Vectors.dense(values));
                }

                int numClusters = mode;//预测分为mode个簇类
                int numInterations = 20;//迭代20次
                int runs = 10;//运行10次，得出最优解

                JavaRDD<Vector> pointsData = context.parallelize(vectors);
                KMeansModel clusters = KMeans.train(pointsData.rdd(),numClusters,numInterations,runs);
    //            System.out.println(pointsData.map(v->v.toString()+"belong to cluster : "+ clusters.predict(v)).collect());
    //            System.out.println(postuserIds.get(postuserIds.size()));

                //根据位置得到的不同的簇，得到最多的描述，做用户准确性调整
                List<Integer> list = new ArrayList<>();//存放每个vector的簇号
                for(Vector v:vectors){
                    list.add(clusters.predict(v));
                }

                //聚类中心
                Vector[] vectorsCenter = clusters.clusterCenters();

                for(int i=0;i<mode;i++){
                    Vector vector = vectorsCenter[i];

                    List<String> resString = new ArrayList<>();//存储相同簇号的描述
                    for(int j=0;j<list.size();j++){
                        if(list.get(j)==i) {
                            TagDesc tagDesc = recTags.get(j).getDescription();
                            if (tagDesc.getTagDescType() == TagDescType.SINGLE) {
                                resString.add(((TagSingleDesc) tagDesc).getDescription());
                            } else {
                                Map<String, String> map = ((TagMultiDesc) tagDesc).getDescriptions();
                                List<String> list1 = new ArrayList<>();
                                for (String s : map.keySet()) {
                                    list1.add(map.get(s));
                                }
                                resString.addAll(list1);
                            }
                        }
                    }
                    //找到resString出现频率最高的描述字符串
                    HashSet<String> unique = new HashSet<>(resString);
                    HashMap<Integer,String> res1 = new HashMap<>();
                    int[] count1 = new int[unique.size()];
                    int index1=0;
                    for(String s:unique){
                        for(String str:resString){
                            if(s.equals(str)){
                                count1[index1]++;
                            }
                        }
                        res1.put(count1[index1],s);
                        index1++;
                    }
                    int max1=0;
                    for(int i1:count1){
                        max1=Math.max(i1,max1);
                    }
                    String strMode = res1.get(max1);//strMode是该范围频率出现最高的描述字符串

                    //近义词训练
//                    Word2VecModel model = Word2VecModel.load(context.sc(),"MyModelPath");


                    CenterTag centerTag = new CenterTag(vector.apply(0),vector.apply(1),vector.apply(2),vector.apply(3),strMode);
                    eachFileTags.add(centerTag);

                    //对用户标对的Tag数进行调整
                    for(int j=0;j<list.size();j++){
                        if(list.get(j)==i){
                            TagDesc tagDesc = recTags.get(j).getDescription();
                            if(tagDesc.getTagDescType()==TagDescType.SINGLE){
                                if(((TagSingleDesc) tagDesc).getDescription().equals(strMode)) {
                                    //User相应的标对的Id加一
                                    Long userId = recTags.get(j).getUserId();
                                    int num = userCorrectTagsNum.get(userId)+1;
                                    userCorrectTagsNum.put(userId,num);
                                }
                            }else{
                                Map<String, String> map = ((TagMultiDesc) tagDesc).getDescriptions();
                                List<String> list1 = new ArrayList<>();
                                for (String s : map.keySet()) {
                                    list1.add(map.get(s));
                                }
                                if(list1.contains(strMode)) {
                                    //User相应的标对的Id加一
                                    Long userId = recTags.get(j).getUserId();
                                    int num = userCorrectTagsNum.get(userId) + 1;
                                    userCorrectTagsNum.put(userId, num);
                                }
                            }
                        }
                    }
                }
            }
            resCoordinate.put(fileName,eachFileTags);
        }
        //对用户准确度和用户积分进行调整
        for(Long userId:userCorrectTagsNum.keySet()) {
            double accuracyRate = (double) userCorrectTagsNum.get(userId) / (double) standardTags;
            if (accuracyRate >= 1) {//准确率超过100%,将准确率定为100%
                accuracyRate = 1;
            }
            User user = userDao.findOne(userId);
            double postAccuracyRate = user.getAccuracyRate();
            Long postPoints = user.getPoints();
            user.setAccuracyRate((postAccuracyRate * (user.getMyTasks().size() - 1) + accuracyRate) / (double) user.getMyTasks().size());
            user.setPoints(new Double(pointsPerPerson * accuracyRate).longValue() + postPoints);
            userDao.save(user);
        }
        taskServiceImpl.write(taskPublisherId,resCoordinate);
        return ResultMessage.SUCCESS;
    }

//    public List<RecTag> getRecTags(){
//        List<Tag> tags = tagDao.getAllTags();
//        List<RecTag> rectags = new ArrayList<>();
//        for(Tag tag:tags){
//            if(tag.getTagType()== TagType.RECT){
//                rectags.add((RecTag)tag);
//            }
//        }
//        return rectags;
//    }

//    public static void main(String[] args){
//        double[] d1=new double[]{1.0,2,3,4};
//        double[] d2=new double[]{5,6,7,8.0};
//        List<Vector> vectors = new ArrayList<>();
//        vectors.add(Vectors.dense(d1));
//        vectors.add(Vectors.dense(d2));
//        System.out.println(vectors.get(1).apply(2));
//    }
    private List<RecTag> getTags(Long taskPublisherId){
        List<TaskWorker> taskWorkers = new ArrayList<>();
        taskWorkers = taskWorkerDao.findByTaskPublisherIdAndTaskState(taskPublisherId, TaskState.SUBMITTED);
        List<Long> tagIds = new ArrayList<>();
        taskWorkers.forEach((taskWorker -> {
            tagIds.addAll(taskWorker.getTags());
        }));
        List<RecTag> tags = new ArrayList<>();
        tagIds.forEach(tagId ->{
            Tag tag = tagDao.findOne(tagId);
            if (tag.getTagType() == TagType.RECT){
                tags.add((RecTag)tag);
            }
        });
        return tags;
    }
}
