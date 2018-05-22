package horizon.taglib.service.impl;

import horizon.taglib.dao.InterestDao;
import horizon.taglib.dao.TaskPublisherDao;
import horizon.taglib.dao.TaskWorkerDao;
import horizon.taglib.dao.UserDao;
import horizon.taglib.enums.InterestFactor;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.TaskState;
import horizon.taglib.model.Interest;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.service.RecommendService;
import horizon.taglib.service.recommend.ItemCFRecommend;
import horizon.taglib.service.recommend.UserCFRecommend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendServiceImpl implements RecommendService {

    private TaskPublisherDao taskPublisherDao;
    private UserDao userDao;
    private TaskWorkerDao taskWorkerDao;
    private InterestDao interestDao;
    private UserCFRecommend userCFRecommend;
    private ItemCFRecommend itemCFRecommend;

    @Autowired
    public RecommendServiceImpl(TaskPublisherDao taskPublisherDao, UserDao userDao, TaskWorkerDao taskWorkerDao, InterestDao interestDao, UserCFRecommend userCFRecommend, ItemCFRecommend itemCFRecommend){
        this.taskPublisherDao = taskPublisherDao;
        this.userDao = userDao;
        this.taskWorkerDao = taskWorkerDao;
        this.interestDao = interestDao;
        this.userCFRecommend = userCFRecommend;
        this.itemCFRecommend = itemCFRecommend;
    }

    @Override
    public List<TaskPublisher> getPersonalizedTasks(List<String> topics,long userId){
        long topicsCount = topics.size();
        List<TaskPublisher> allPostTaskPublishers = taskPublisherDao.findAll();
        List<TaskPublisher> allTaskPublishers = getFitTaskPublishers(userId,allPostTaskPublishers);
        //若总任务数小于等于12，则返回所有的发起者任务
        if(allTaskPublishers.size()<=12){
            return allTaskPublishers;
        }else{          //总任务数大于12
            //若任务话题数等于1时
            if(topicsCount==1){
                String topic = topics.get(0);
                //根据目标任务话题，得到包含目标话题的发起者任务(不包含重复任务）
                List<TaskPublisher> targetTasks = deleteSameTask(getTaskPublishersBytopic(topic,allTaskPublishers));
                //若目标任务数小于12，则从剩余的发起者任务中随机抽取，填补成12个发起者任务
                if(targetTasks.size()<12){
                    allTaskPublishers.removeAll(targetTasks);
                    long addCount = 12-targetTasks.size();
                    while(addCount>0){
                        //目标任务添加一个任意的发起者任务
                        int random = (int)(Math.random()*allTaskPublishers.size());
                        TaskPublisher taskPublisher = allTaskPublishers.get(random);
                        targetTasks.add(taskPublisher);
                        allTaskPublishers.remove(taskPublisher);
                        addCount--;
                    }
                    return targetTasks;
                }else{  //目标任务大于等于12，则随机抽取12个
                    long count = 0;
                    List<TaskPublisher> res = new ArrayList<>();
                    while(count<12){
                        int random = (int)(Math.random()*targetTasks.size());
                        TaskPublisher taskPublisher = targetTasks.get(random);
                        res.add(taskPublisher);
                        targetTasks.remove(taskPublisher);
                        count++;
                    }
                    return res;
                }
            }
            //若任务话题数大于1时
            else{
                //随机抽取两个话题,分别为random1,random2
                int random1 = (int)(Math.random()*topicsCount);
                String topic1 = topics.get(random1);
                topics.remove(topic1);
                int random2 = (int)(Math.random()*topics.size());
                String topic2 = topics.get(random2);

                //得到两个任务话题分别对应的发起者任务
                List<TaskPublisher> taskPublishers_topic1 = getTaskPublishersBytopic(topic1,allTaskPublishers);
                List<TaskPublisher> taskPublishers_topic2 = getTaskPublishersBytopic(topic2,allTaskPublishers);

                List<TaskPublisher> targetTasks = new ArrayList<>();
                targetTasks.addAll(taskPublishers_topic1);
                targetTasks.addAll(taskPublishers_topic2);
                List<TaskPublisher> addTasks = deleteSameTask(targetTasks);

                //如果加起来任务数小于12，则从剩余任务中填补
                if(addTasks.size()<12){
                    allTaskPublishers.removeAll(addTasks);
                    long count = 12-addTasks.size();
                    while(count>0){
                        int random = (int)(Math.random()*allTaskPublishers.size());
                        TaskPublisher taskPublisher = allTaskPublishers.get(random);
                        allTaskPublishers.remove(taskPublisher);
                        addTasks.add(taskPublisher);
                        count--;
                    }
                    return addTasks;
                }else{  //加起来任务数大于等于12，则随机抽取12个任务
                    int count = 0;
                    List<TaskPublisher> taskPublishers = new ArrayList<>();
                    while(count<12){
                        int random = (int)(Math.random()*addTasks.size());
                        TaskPublisher taskPublisher = addTasks.get(random);
                        addTasks.remove(taskPublisher);
                        taskPublishers.add(taskPublisher);
                        count++;
                    }
                    return taskPublishers;
                }
            }
        }
    }

    @Override
    public List<TaskPublisher> getHotestTasks(long userId){
        List<TaskPublisher> allTaskPublishers = taskPublisherDao.findAll();
        List<TaskPublisher> presentPublishers = getFitTaskPublishers(userId,allTaskPublishers);
        //若总发布者任务数量小于等于5，则直接返回所有的发布者任务
        if(presentPublishers.size()<=5){
            return presentPublishers;
        }else{      //发布者任务数大于5时
            Sort.SortByHotRank sortByHotRank = new Sort.SortByHotRank();
            Collections.sort(presentPublishers,sortByHotRank);
            return presentPublishers.subList(0,5);
        }
    }

    /**
     *根据一个任务话题筛选出含有这个任务话题的发起者任务
     * 在每个发起者任务的任务话题存在相同的情况下，有可能返回相同的发起者任务
     */
    private List<TaskPublisher> getTaskPublishersBytopic(String topic,List<TaskPublisher> taskPublishers){
        List<TaskPublisher> res = new ArrayList<>();
        for(TaskPublisher taskPublisher:taskPublishers){
            List<String> taskTopics = taskPublisher.getTopics();
            for(String taskTopic:taskTopics){
                if(taskTopic.equals(topic)){
                    res.add(taskPublisher);
                }
            }
        }
        return res;
    }

    /**
     * 通过userCF获得推荐
     * @param userId
     * @param size
     * @return
     */
    @Override
    public List<TaskPublisher> getRecommendTaskByUser(Integer userId, Integer size){
        List<TaskPublisher> allPostTaskPublishers = taskPublisherDao.findAll();
        List<TaskPublisher> allTaskPublishers = getFitTaskPublishers(userId,allPostTaskPublishers);
        List<Integer> fitTaskPublisherIds = new ArrayList<>();
        allTaskPublishers.forEach((task)-> fitTaskPublisherIds.add(task.getId().intValue()));
        List<Integer> recommendTaskIds = userCFRecommend.getRecommendItems(userId, size, fitTaskPublisherIds);

        recommendTaskIds.addAll(getRecommendTaskByInterest(userId.longValue(), allTaskPublishers, size));
        recommendTaskIds = recommendTaskIds.stream().distinct().collect(Collectors.toList());
        Collections.shuffle(recommendTaskIds);
        recommendTaskIds = recommendTaskIds.stream().limit(size).collect(Collectors.toList());

        List<TaskPublisher> recommendTask = new ArrayList<>();
        recommendTaskIds.forEach((taskId)->recommendTask.add(taskPublisherDao.findOne(taskId.longValue())));


        return recommendTask;
    }

    /**
     * 通过itemCF获得推荐
     * @param taskPublisherId
     * @param size
     * @return
     */
    @Override
    public List<TaskPublisher> getRecommendTaskByItem(Long taskPublisherId, Long userId, Integer size){
        List<Long> recommendTaskIds = itemCFRecommend.getRecommendItems(taskPublisherId);
        List<TaskPublisher> recommendTask = new ArrayList<>();
        recommendTaskIds.stream().forEach((taskId)->recommendTask.add(taskPublisherDao.findOne(taskId)));
        List<TaskPublisher> fitTasks = getFitTaskPublishers(userId, recommendTask);
        if(fitTasks.size()>size){
            fitTasks = fitTasks.subList(0, size);
        }
        return fitTasks;
    }

    @Override
    public ResultMessage addUserInterestFactor(Long userId, List<String> topics, InterestFactor factor) {
        topics.forEach(topic -> {
            Interest interest = interestDao.findInterestByUserIdAndTopic(userId, topic);
            /* 处理没有该条记录的情况 */
            if (interest == null){
                interest = new Interest();
                interest.setUserId(userId);
                interest.setTopic(topic);
                interest.setInterestFactor((long)factor.getFactor());
            } else {
                interest.setInterestFactor(interest.getInterestFactor() + factor.getFactor());
            }
            interestDao.save(interest);
        });
        return ResultMessage.SUCCESS;
    }

    /**
     *去除taskPublishers里的相同task
     */
    private List<TaskPublisher> deleteSameTask(List<TaskPublisher> taskPublishers){
        Set set = new HashSet();
        List<TaskPublisher> res = new ArrayList<>();
        set.addAll(taskPublishers);
        res.addAll(set);
        return res;
    }

    /**
     * 显示在进行中的任务，且用户从未接受过
     */
    private List<TaskPublisher> getFitTaskPublishers(long userId,List<TaskPublisher> allTaskPublishers){
        List<Long> myTaskWorkers = userDao.findOne(userId).getMyTasks();
        List<TaskPublisher> res = new ArrayList<>();
        for(TaskPublisher taskPublisher:allTaskPublishers){
            boolean isAdd = true;
            for(Long accptedTaskWorkersId:myTaskWorkers){
                if(taskWorkerDao.findOne(accptedTaskWorkersId).getTaskPublisherId()==taskPublisher.getId()){
                    isAdd = false;
                }
            }
            if(taskPublisher.getTaskState()==TaskState.PROCESSING&&isAdd){
                res.add(taskPublisher);
            }
        }
        return res;
    }

    private List<Integer> getRecommendTaskByInterest(Long userId, List<TaskPublisher> fitTasks, Integer size) {
        List<Interest> interestList = interestDao.findByUserId(userId);

        /* 用户兴趣因子Map */
        Map<String, Long> interestMap = new HashMap<>();
        interestList.forEach((interest -> {
            interestMap.put(interest.getTopic(), interest.getInterestFactor());
        }));

        Map<Long, Long> taskFactorMap = new HashMap<>();

        fitTasks.forEach((task) -> task.getTopics().forEach((topic)->{
            Long factor = interestMap.getOrDefault(topic, 0L);
            taskFactorMap.put(task.getId(), taskFactorMap.getOrDefault(task.getId(), 0L) + factor);
        }));

        Set<Map.Entry<Long, Long>> mapEntrySet =  taskFactorMap.entrySet();
        List<Map.Entry<Long, Long>> entryList = new ArrayList<>(mapEntrySet);
        entryList.sort(Comparator.comparing(Map.Entry::getValue));
        entryList = entryList.stream().limit(size).collect(Collectors.toList());

        List<Integer> recommendTasks = new ArrayList<>();
        entryList.forEach((entry)->{
            recommendTasks.add(entry.getKey().intValue());
        });
        return recommendTasks;
    }
}
