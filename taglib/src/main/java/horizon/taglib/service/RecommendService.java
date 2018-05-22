package horizon.taglib.service;

import horizon.taglib.enums.InterestFactor;
import horizon.taglib.enums.ResultMessage;
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

    /**
     * 通过itemCF获得推荐
     * @param taskPublisherId
     * @param size
     * @return
     */
    public List<TaskPublisher> getRecommendTaskByItem(Long taskPublisherId, Long userId, Integer size);

    /**
     * 增加用户兴趣因子
     * @param userId
     * @param topics
     * @return
     */
    public ResultMessage addUserInterestFactor(Long userId, List<String> topics, InterestFactor interestFactor);
}
