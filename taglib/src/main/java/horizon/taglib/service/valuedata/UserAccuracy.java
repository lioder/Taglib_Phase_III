package horizon.taglib.service.valuedata;

import horizon.taglib.dao.*;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.TagType;
import horizon.taglib.enums.TaskState;
import horizon.taglib.model.*;
import horizon.taglib.service.AdminService;
import horizon.taglib.service.TaskService;
import horizon.taglib.service.UserAccuracyService;
import horizon.taglib.model.CenterTag;
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

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 根据标注的所有数据自动调整用户的准确度
 */

@Service
@Component
public class UserAccuracy implements UserAccuracyService{
    @Autowired
    private TaskPublisherDao taskPublisherDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AdminService adminService;

    @Autowired
    private TaskWorkerDao taskWorkerDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private EMEstimator emEstimator;

    private CenterTagDao centerTagDao;

    private WorkerResultDao workerResultDao;

    private JavaSparkContext context;

    private TaskService taskService;

	/**
	 * 标准标注数量
	 */
    private int standardTags = 0;
    /**
	 * 聚类结果
	 */
    private List<MyCluster> clusters;
	/**
	 * 完成任务的工人的id集合
	 */
    private List<Long> userIds;
	/**
	 * Map &lt; 标签, 序号 &gt;
	 */
    private Map<String, Integer> descMap;
    /**
     * Map &lt; TaskWorkerId, 工人标注集合 &gt;
     */
    private Map<Long, Set<RecTag>> workerTags;

    private Logger logger = LoggerFactory.getLogger(UserAccuracy.class);

    @Autowired
    public UserAccuracy(SparkUtil sparkUtil, CenterTagDao centerTagDao, WorkerResultDao workerResultDao, TaskService taskService){
        this.centerTagDao = centerTagDao;
        this.workerResultDao = workerResultDao;
        this.taskService = taskService;
        context = sparkUtil.getSparkContext();
        clusters = new ArrayList<>();
        descMap = new HashMap<>();
        userIds = new ArrayList<>();
        workerTags = new HashMap<>();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResultMessage adjustUserAccuracy(long taskPublisherId){
        reset();
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
        generateWorkerResults();

        // 将标准标注保存为JSON格式
        return taskService.saveCenterTagAsJSON(taskPublisherId, clusters);
    }

    /**
     * 每次自动评估前重置成员变量
     */
    private void reset() {
        this.standardTags = 0;
        this.clusters.clear();
        this.descMap.clear();
        this.userIds.clear();
        this.workerTags.clear();
    }

//    @Override
//    @Transactional(rollbackOn = Exception.class)
//    public ResultMessage testTransaction(long taskPublisherId) {
//        TaskPublisher tmpTaskPublisher = taskPublisherDao.findOne(2L);
//        for (String image : tmpTaskPublisher.getImages()) {
//            System.out.println(image);
//        }
//        for (String label : tmpTaskPublisher.getLabels()) {
//            System.out.println(label);
//        }
//        return ResultMessage.SUCCESS;
//    }

    private void calUserAccuracy(long taskPublisherId, Integer[][] observations, double[][] result){
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
            myCluster.label = ans;
            CenterTag centerTag = new CenterTag(taskPublisherId, myCluster.filename,
                    vector.apply(0), vector.apply(1), vector.apply(2), vector.apply(3), ans);
            centerTag = centerTagDao.save(centerTag);
            myCluster.centerTagId = centerTag.getId();

            for (RecTag recTag : myCluster.recTags) {
                if (((TagSingleDesc)(recTag.getDescription())).getDescription().equals(ans)) {
                    Long userId = recTag.getUserId();
                    int num = userCorrectTagsNum.get(userId)+1;
                    userCorrectTagsNum.put(userId,num);
                }
            }
        }
        for (Long workerId: userIds){
            TaskWorker taskWorker = taskWorkerDao.findByUserIdAndTaskPublisherId(workerId, taskPublisherId);
            taskWorker.setTaskState(TaskState.PASS);
            taskWorkerDao.save(taskWorker);
        }
        // 将taskPublisher状态改成完成
        TaskPublisher taskPublisher = taskPublisherDao.findOne(taskPublisherId);
        taskPublisher.setTaskState(TaskState.DONE);
        taskPublisherDao.save(taskPublisher);

        adminService.recordCheckResult(userCorrectTagsNum, taskPublisherId, standardTags);

        // 对用户准确度和用户积分还有用户经验进行调整
        for(Long userId:userCorrectTagsNum.keySet()){
            double accuracyRate = (double)userCorrectTagsNum.get(userId)/(double)standardTags;
            if(accuracyRate>=1){//准确率超过100%,将准确率定为100%
                accuracyRate=1;
            }
            User user = userDao.findOne(userId);
            double postAccuracyRate = user.getAccuracyRate();
            user.setAccuracyRate((postAccuracyRate*(user.getMyTasks().size()-1)+accuracyRate)/(double)user.getMyTasks().size());
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
            System.out .print("这是第" + myCluster.clusterNo + "个簇：");
            Integer object = myCluster.clusterNo;
            for (RecTag tag : myCluster.recTags) {
                System.out.print(tag.getId() + " ");
                observations[object][workerIdMap.get(tag.getUserId())] = descMap.get(((TagSingleDesc) (tag.getDescription())).getDescription());
            }
            System.out.println();
        }
        return observations;
    }
    /**
     * 将标签聚类
     */
    private void cluster(long taskPublisherId) {
        List<RecTag> tags = getTags(taskPublisherId);
        TaskPublisher taskPublisher = taskPublisherDao.findOne(taskPublisherId);

        int countOption = 0;
        for (String option : taskPublisher.getOptions()){
            descMap.putIfAbsent(option, countOption++);
        }
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
        this.workerTags.clear();
        taskWorkers.forEach((taskWorker -> {
            tagIds.addAll(taskWorker.getTags());
            this.workerTags.put(taskWorker.getId(), new HashSet<>());
        }));
        List<RecTag> tags = new ArrayList<>();
        tagIds.forEach(tagId ->{
            Tag tag = tagDao.findOne(tagId);
            if (tag.getTagType() == TagType.RECT){
                tags.add((RecTag)tag);
                this.workerTags.get(tag.getTaskWorkerId()).add((RecTag) tag);
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

	/**
	 * 依照标准标注和聚类结果对个工人的标注进行评估并保存评估结果
	 */
	private void generateWorkerResults() {
        Map<Integer, Long> clusterNoToCenterTagId = new HashMap<>();   // Map<MyCluster.clusterNo, CenterTag.Id>
        // 添加CenterTag，记录其Id与MyCluster的序号的对应关系
        for (MyCluster myCluster : this.clusters) {
            clusterNoToCenterTagId.put(myCluster.clusterNo, myCluster.centerTagId);
        }
        // 评估工人的标注
        for (Long taskWorkerId : this.workerTags.keySet()) {
            boolean[] matchStandardTag = new boolean[this.clusters.size()];  // m[聚类中心簇号]: 该聚类中是否有该工人的标注
            List<Long> correctTagIds = new ArrayList<>();
            List<Long> wrongTagIds = new ArrayList<>();
            List<Long> missTagIds = new ArrayList<>(clusterNoToCenterTagId.values());   // 未开始时所有标准Tag视为Miss
            // 检测每个标注是否在聚类结果中，是则判为正确，否则判为错误
            for (RecTag recTag : this.workerTags.get(taskWorkerId)) {
                boolean found = false;
                for (MyCluster myCluster : this.clusters) {
                    if (myCluster.recTags.contains(recTag)) {
                        if (((TagSingleDesc) (recTag.getDescription())).getDescription().equals(myCluster.label)) {
                            found = true;
                            correctTagIds.add(recTag.getId());  // 判为正确
                            if (!matchStandardTag[myCluster.clusterNo]) {
                                matchStandardTag[myCluster.clusterNo] = true;
                                missTagIds.remove(clusterNoToCenterTagId.get(myCluster.clusterNo)); // 不再视为Miss
                            }
                        }
                        break;
                    }
                }
                if (!found) {
                    wrongTagIds.add(recTag.getId());  // 判为错误
                }
            }
            this.workerResultDao.save(new WorkerResult(taskWorkerId, correctTagIds, wrongTagIds, missTagIds));
        }
	}
}
