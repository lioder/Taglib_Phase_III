package horizon.taglib.service.valuedata;

import horizon.taglib.dao.TagDao;
import horizon.taglib.dao.TaskPublisherDao;
import horizon.taglib.dao.TaskWorkerDao;
import horizon.taglib.dao.UserDao;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.TagType;
import horizon.taglib.enums.TaskState;
import horizon.taglib.model.*;
import horizon.taglib.service.AdminService;
import horizon.taglib.service.UserService;
import horizon.taglib.service.impl.TaskServiceImpl;
import horizon.taglib.utils.CenterTag;
import horizon.taglib.utils.DBSCAN;
import horizon.taglib.utils.SparkUtil;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private AdminService adminService;

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

    private Logger logger = LoggerFactory.getLogger(UserAccuracy.class);

    @Autowired
    public UserAccuracy(SparkUtil sparkUtil){
        this.sparkUtil = sparkUtil;
        context = sparkUtil.getSparkContext();
        clusters = new ArrayList<>();
        descMap = new HashMap<>();
        userIds = new ArrayList<>();
    }

    public ResultMessage adjustUserAccuracy(long taskPublisherId){
        clusterByDBSCAN(taskPublisherId); // 将所有的Tag分簇

        Integer[][] observations = getObservations();
        for (int i = 0; i < observations.length; i++){
            for (int j = 0; j  < observations[0].length; j++) {
                System.out.print(observations[i][j] + " ");
            }
            System.out.println();
        }
        double[][] result = emEstimator.estimate(observations, descMap.entrySet().size());
        for (int i = 0; i < result.length; i++){
            for (int j = 0; j  < result[0].length; j++) {
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }
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
        for (Long workerId: userIds){
            TaskWorker taskWorker = taskWorkerDao.findTaskWorkerByUserIdAndAndTaskPublisherId(workerId, taskPublisherId);
            taskWorker.setTaskState(TaskState.PASS);
            taskWorkerDao.save(taskWorker);
        }
        taskServiceImpl.write(taskPublisherId,resCoordinate);
        adminService.recordCheckResult(userCorrectTagsNum, taskPublisherId, standardTags);
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
            user.setExp(user.getExp() + userCorrectTagsNum.get(userId));
            //一天内准确率过低任务大于等于3个，则一天内其余任务奖励减半
            int taskCount = adminService.findWrongRecordCountByDate(LocalDate.now(), userId);
            if(taskCount > 2 && taskCount <= 4) {
                user.setPoints(new Double(pointsPerPerson * accuracyRate /2).longValue() + postPoints);
            }
            else if(taskCount > 4){
                user.setProhibitTime(new Long(1));
            }
            else{
                user.setPoints(new Double(pointsPerPerson * accuracyRate).longValue() + postPoints);
            }
            userDao.save(user);
        }
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
        for (Integer[] observation : observations) {
            Arrays.fill(observation, -1);
        }
        for (MyCluster myCluster : this.clusters) {
            System.out .println("这是第" + myCluster.clusterNo + "个簇：");
            Integer object = myCluster.clusterNo;
            for (RecTag tag : myCluster.recTags) {
                System.out.println(tag.getId());
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

        int countOption = 0;
        for (String option : taskPublisher.getOptions()){
            descMap.putIfAbsent(option, countOption++);
        }
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
                    List<RecTag> tagInCluster = new ArrayList<>();
                    for (int j = 0; j < recTags.size(); j++){
                        if (noList.get(j) == i){
                            tagInCluster.add(recTags.get(j));
                        }
                    }
                    this.clusters.add(new MyCluster(standardTags - mode + i,tagInCluster, fileName, centers[i]));
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

    /**
     * 要改变的类成员：standardTags, pointsPerPerson, clusters, userIds, descMap <br>
     * int standardTags = 0 <br>
     * double pointsPerPerson <br>
     * List &lt; MyCluster &gt; clusters <br>
     * List &lt; Long &gt; userIds <br>
     * Map &lt; String, Integer &gt; descMap
     */
    private void clusterByDBSCAN(long taskPublisherId) {
        List<RecTag> tags = getTags(taskPublisherId);
        TaskPublisher taskPublisher = taskPublisherDao.findOne(taskPublisherId);

        int countOption = 0;
        for (String option : taskPublisher.getOptions()) {
            this.descMap.putIfAbsent(option, countOption++);
        }
        this.pointsPerPerson = taskPublisher.getPrice() / taskPublisher.getNumberPerPicture();
        List<String> fileNames = taskPublisher.getImages();

        // 得到所有接过该任务并且提交过任务的UserId
        Set<Long> allUserIds = new HashSet<>();
        for (RecTag recTag : tags) {
            allUserIds.add(recTag.getUserId());
        }
        this.userIds = new ArrayList<>(allUserIds);

        for (String fileName : fileNames) {
            List<RecTag> recTags = new ArrayList<>();
            // 找到该fileName的recTag,放在recTags
            for (RecTag recTag : tags) {
                if (recTag.getFileName().equals(fileName)) {
                    recTags.add(recTag);
                }
            }

            List<List<RecTag>> clusterResult = DBSCAN.cluster(recTags);
            for (List<RecTag> cluster : clusterResult) {
                // 需要进行Tags左上点，右下点的聚类
                List<Vector> vectors = new ArrayList<>();
                for (RecTag recTag : cluster) {
                    double[] values = new double[]{recTag.getStart().getX(), recTag.getStart().getY(),
                            recTag.getEnd().getX(), recTag.getEnd().getY()};
                    vectors.add(Vectors.dense(values));
                }

                int numberClusters = 1;    // 预测分为1个簇类
                int numberIterations = 20;    // 迭代20次
                int runs = 10;  // 运行10次，得出最优解

                JavaRDD<Vector> pointsData = context.parallelize(vectors);
                KMeansModel clusters = KMeans.train(pointsData.rdd(), numberClusters, numberIterations, runs);

                // 把这一簇的聚类中心放到类变量vectorsCenters中
                Vector[] centers = clusters.clusterCenters();
                if (centers.length > 0) {
                    this.clusters.add(new MyCluster(this.standardTags++, cluster, fileName, centers[0]));
                } else {
                    this.logger.error("聚类中心数量为零！TaskPublisherID={}, fileName={}, cluster.size={}",
                            taskPublisherId, fileName, cluster.size());
                }
            }
        }
    }
}
