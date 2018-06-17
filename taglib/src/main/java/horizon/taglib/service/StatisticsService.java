package horizon.taglib.service;

import horizon.taglib.dto.TimeCircleDTO;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.TaskWorker;
import horizon.taglib.model.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface StatisticsService {
    /**
     * 得到所有的TaskPublisher
     * @return
     */
    public List<TaskPublisher> getAllTaskPublishers();

    /**
     * 得到所有的TaskWorker
     * @return
     */
    public List<TaskWorker> getAllTaskWorkers();

    /**
     * 通过UserId得到用户参与的所有TaskWorker
     * @return
     */
    public List<TaskWorker> getTaskWorkersByUserId(long id);

    /**
     * 通过发布者任务id得到所有的参与任务的用户
     * @param taskPublisherId
     * @return
     */
    public List<User> allParticipateUsersById(Long taskPublisherId);

    /**
     * 统计某时间段内的新注册用户数量
     *
     * @param startDate 开始日期
     * @param endDate 截止日期
     * @return 统计数量
     */
    Long countNewUserByDates(LocalDate startDate, LocalDate endDate);

    /**
     *工人列表
     *
     * @return 工人列表
     */
    List<User> getWorkers();

    /**
     * 统计时间段内的任务（发布者视角）的生命周期信息
     *
     * @param start 开始时间
     * @param end 截止时间
     * @return 各任务（发布者视角）的生命周期信息
     */
    List<TimeCircleDTO> getTaskTimelines(LocalDate start, LocalDate end);
}
