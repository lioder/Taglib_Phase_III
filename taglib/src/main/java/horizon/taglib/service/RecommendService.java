package horizon.taglib.service;

import horizon.taglib.model.TaskPublisher;

import java.util.List;

public interface RecommendService {

    /**
     * 个性化推荐发起者任务
     * @param topics
     * @return
     */
    public List<TaskPublisher> getPersonalizedTasks(List<String> topics,long userId);

    /**
     * 推荐当前最热的五个任务
     * 若总发布者任务数量小于等于5，则直接返回所有的发布者任务
     * @return
     */
    public List<TaskPublisher> getHotestTasks(long userId);

    /**
     * 通过userCF获得推荐
     * @param userId
     * @param size
     * @return
     */
    public List<TaskPublisher> getRecommendTaskByUser(Integer userId, Integer size);
}
