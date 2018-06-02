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
import org.apache.spark.sql.sources.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import scala.util.parsing.combinator.testing.Str;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private EMEstimator emEstimator;

    private SparkUtil sparkUtil;
    JavaSparkContext context;

    int standardTags = 0;
    double pointsPerPerson;
    List<MyCluster> clusters;
    List<Long> userIds;
    Map<String, Integer> descMap;
    @Autowired
    public UserAccuracy(SparkUtil sparkUtil){
        this.sparkUtil = sparkUtil;
        context = sparkUtil.getSparkContext();
        clusters = new ArrayList<>();
        descMap = new HashMap<>();
        userIds = new ArrayList<>();
        descMap.put("阿拉斯加", 1);
        descMap.put("哈士奇", 0);
    }

    public ResultMessage adjustUserAccuracy(long taskPublisherId){
        cluster(taskPublisherId); // 将所有的Tag分簇

        Integer[][] observations = getObservations();
        double[][] result = emEstimator.estimate(observations, descMap.entrySet().size());
        calUserAccuracy(taskPublisherId, observations, result);
        return ResultMessage.SUCCESS;
    }

    private void calUserAccuracy(long taskPublisherId, Integer[][] observations, double[][] result){
        Map<String, List<CenterTag>> resCoordinate = new HashMap<>();
        Map<Long, Integer> userCorrectTagsNum = new HashMap<>();
        for (Long userId : userIds) {
            userCorrectTagsNum.put(userId, 0);
        }

        for (MyCluster myCluster:clusters) {
            Vector vector = myCluster.center;
            String ans = "";
            double prior = 0;
            String[] array = new String[descMap.keySet().size()];
            for (String key : descMap.keySet()) {
                array[descMap.get(key)] = key;
            }
            for (int i = 0; i < descMap.keySet().size(); i++) {
                if (result[myCluster.clusterNo][i] > prior) {
                    prior = result[myCluster.clusterNo][i];
                    ans = array[i];
                }
            }
            CenterTag centerTag = new CenterTag(vector.apply(0), vector.apply(1), vector.apply(2), vector.apply(3), ans);
            List<CenterTag> centertags = resCoordinate.getOrDefault(myCluster.filename, new ArrayList<>());
            centertags.add(centerTag);
            resCoordinate.put(myCluster.filename, centertags);

            for (RecTag recTag : myCluster.recTags) {
                if (((TagSingleDesc)(recTag.getDescription())).getDescription().equals(ans)) {
                    Long userId = recTag.getUserId();
                    int num = userCorrectTagsNum.get(userId)+1;
                    userCorrectTagsNum.put(userId,num);
                }
            }

        }
        // 对用户准确度和用户积分还有用户经验进行调整
        for(Long userId:userCorrectTagsNum.keySet()){
            double accuracyRate = (double)userCorrectTagsNum.get(userId)/(double)standardTags;
            if(accuracyRate>=1){//准确率超过100%,将准确率定为100%
                accuracyRate=1;
            }
            User user = userDao.findOne(userId);
            double postAccuracyRate = user.getAccuracyRate();
            Long postPoints = user.getPoints();
            user.setAccuracyRate((postAccuracyRate*(user.getMyTasks().size()-1)+accuracyRate)/(double)user.getMyTasks().size());
            user.setPoints(new Double(pointsPerPerson*accuracyRate).longValue()+postPoints);
            user.setExp(user.getExp() + userCorrectTagsNum.get(userId));
            userDao.save(user);
        }
        taskServiceImpl.write(taskPublisherId,resCoordinate);
    }

    private Integer[][] getObservations() {
        Map<Long, Integer> workerIdMap = new HashMap<>();
        int count = 0; // 最后存的是worker的数量

        // 为每个worker重编号
        for (MyCluster myCluster : this.clusters) {
            for (RecTag tag : myCluster.recTags) {
                if (workerIdMap.get(tag.getUserId()) == null) {
                    workerIdMap.put(tag.getUserId(), count);
                    count++;
                }
            }
        }
        Integer[][] observations = new Integer[this.clusters.size()][workerIdMap.keySet().size()];
        for (MyCluster myCluster : this.clusters) {
            Integer object = myCluster.clusterNo;
            for (RecTag tag : myCluster.recTags) {
                observations[object][workerIdMap.get(tag.getUserId())] = descMap.get(((TagSingleDesc) (tag.getDescription())).getDescription());
            }
        }
        return observations;
    }
    /**
     * 将标签聚类
     */
    public void cluster(long taskPublisherId) {
        List<RecTag> tags = getTags(taskPublisherId);
        TaskPublisher taskPublisher = taskPublisherDao.findOne(taskPublisherId);
        pointsPerPerson = taskPublisher.getPrice() / taskPublisher.getNumberPerPicture();
        List<String> fileNames = taskPublisher.getImages();

        //得到所有接过该任务并且提交过任务的UserId
        List<Long> postuserIds = new ArrayList<>();
        for (RecTag recTag : tags) {
            postuserIds.add(recTag.getUserId());
        }
        //筛去重复userID
        userIds = postuserIds.stream().distinct().collect(Collectors.toList());

        for (String fileName : fileNames) {
            List<RecTag> recTags = new ArrayList<>();
            //找到该fileName的recTag,放在recTags
            for (RecTag recTag : tags) {
                if (recTag.getFileName().equals(fileName)) {
                    recTags.add(recTag);
                }
            }

            //以TaskWorkerId对recTags进行分组
            Map<Long, List<RecTag>> recTagMap = new HashMap<>();
            for (RecTag recTag : recTags) {
                List<RecTag> tempList = recTagMap.get(recTag.getUserId());
                //之前没有加过数据
                if (tempList == null) {
                    tempList = new ArrayList<>();
                    tempList.add(recTag);
                    recTagMap.put(recTag.getUserId(), tempList);
                } else {//之前加过数据
                    tempList.add(recTag);
                }
            }
            //根据图片和不同的用户Id分组后
            //判断该图片标注的众数为多少，作为聚类有多少类
            List<Integer> data = new ArrayList<>();//data为不同用户在一张图上标记的数量集合
            Collection<List<RecTag>> vs = recTagMap.values();
            for (List<RecTag> value : vs) {
                data.add(value.size());
            }

            HashSet<Integer> uniqueData = new HashSet<Integer>(data);
            HashMap<Integer, Integer> res = new HashMap<Integer, Integer>();
            int[] count = new int[uniqueData.size()];
            int index = 0;
            for (Integer integer : uniqueData) {
                for (Integer num : data) {
                    if (num.equals(integer)) {
                        count[index]++;
                    }
                }
                res.put(count[index], integer);
                index++;
            }
            int max = 0;
            for (int i : count) {
                max = Math.max(i, max);
            }
            //max=5,mode=3
            if (max != 0) {
                int mode = res.get(max);//mode为众数
                standardTags = standardTags + mode;
                //需要进行Tags左上点，右下点的聚类
                List<Vector> vectors = new ArrayList<>();
                for (RecTag recTag : recTags) {
                    double[] values = new double[]{recTag.getStart().getX(), recTag.getStart().getY(), recTag.getEnd().getX(), recTag.getEnd().getY()};
                    vectors.add(Vectors.dense(values));
                }

                int numClusters = mode;//预测分为mode个簇类
                int numInterations = 20;//迭代20次
                int runs = 10;//运行10次，得出最优解

                JavaRDD<Vector> pointsData = context.parallelize(vectors);
                KMeansModel clusters = KMeans.train(pointsData.rdd(), numClusters, numInterations, runs);

                ArrayList<Integer> noList = new ArrayList<>();
                //存放每个vector的簇号
                for (Vector v : vectors) {
                    noList.add(clusters.predict(v));
                }

                //把这张图所有的聚类中心放到类变量vectorsCenters中
                Vector[] centers = clusters.clusterCenters();
                for (int i = 0; i < centers.length; i++){
                    this.clusters.add(new MyCluster(standardTags - mode + i,recTags, fileName, centers[i]));
                }
            }
        }
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
//
//    public static void main(String[] args){
//        double[] d1=new double[]{1.0,2,3,4};
//        double[] d2=new double[]{5,6,7,8.0};
//        List<Vector> vectors = new ArrayList<>();
//        vectors.add(Vectors.dense(d1));
//        vectors.add(Vectors.dense(d2));
//        System.out.println(vectors.get(1).apply(2));
//    }
    private List<RecTag> getTags(Long taskPublisherId){
        List<TaskWorker> taskWorkers = taskWorkerDao.findByTaskPublisherIdAndTaskState(taskPublisherId, TaskState.SUBMITTED);
        List<Long> tagIds = new ArrayList<>();
        taskWorkers.forEach((taskWorker -> tagIds.addAll(taskWorker.getTags())));
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
