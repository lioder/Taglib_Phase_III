package horizon.taglib.service;

import horizon.taglib.dto.PageDTO;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AdminService {
    /**
     * 显示众包工人
     * @param keywords
     * @param sortBy
     * @param isSec
     * @param size
     * @param currentPage
     * @return
     */
    public PageDTO showWorkerList(String keywords , String sortBy , Boolean isSec , Integer size , Integer currentPage );

    /**
     * 显示众包发起者
     * @param keywords
     * @param sortBy
     * @param isSec
     * @param size
     * @param currentPage
     * @return
     */
    public PageDTO showPublisherList(String keywords , String sortBy , Boolean isSec , Integer size , Integer currentPage );

    /**
     * 通过id得到发起者任务
     * @param taskId
     * @return
     */
    public TaskPublisher getTaskPublisherById(long taskId);

    /**
     * 更新发起者任务
     * @param taskPublisher
     * @return
     */
    public ResultMessage updateTaskPublisher(TaskPublisher taskPublisher);

    /**
     * 更新用户
     * @param user
     * @return
     */
    public ResultMessage updateUser(User user);

    /**
     * 更新任务
     * @param taskWorker
     * @return
     */
    public ResultMessage updateTaskWorker(TaskWorker taskWorker);

    /**
     * 根据用户Id得到所有完成了的并审核了的任务数量
     * @param userId 工人Id
     * @return
     */
    public long getExaminedTasksNumber(long userId);

    /**
     *根据发起者任务Id得到所有已完成任务的数量
     * @param taskPublisherId
     * @return
     */
    public long getFinishedTaskWorkersNumber(long taskPublisherId);

    /**
     * 根据发起者任务Id得到所有标框标签
     * @param taskpublisherId
     * @return
     */
    public List<RecTag> getRecTagsByTaskPublisherId(long taskpublisherId);

    /**
     * 记录审批结果
     * @param taskPublisherId
     * @param sum
     * @return
     */
    public ResultMessage recordCheckResult(Map<Long, Integer> userResult, Long taskPublisherId, Integer sum);

    /**
     * 根据时间查找审批记录
     * @param date
     * @return
     */
    public Integer findWrongRecordCountByDate(LocalDate date, Long userId);

    /**
     * 查找待审核的专家
     * @return
     */
    List<User> showApplyingProList();
}
