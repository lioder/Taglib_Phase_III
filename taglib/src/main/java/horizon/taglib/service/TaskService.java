package horizon.taglib.service;

import horizon.taglib.dto.PageDTO;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.TaskState;
import horizon.taglib.model.Tag;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.TaskRecord;

import java.util.List;
import java.util.Map;

public interface TaskService {
    /**
     * 得到所有task
     */
    public List<TaskPublisher> getAllTasks();
    /**
     * 根据taskid得到task
     */
    public TaskPublisher getTaskPublisherById(long id);
//    /**
//     * 增加tag
//     */
//    public ResultMessage addTag(Tag tag);
//    /**
//     * 更新tag
//     */
//    public ResultMessage updateTag(Tag tag);
//
//    /**
//     *根据图片名称和任务ID拿到所有关于它的tag
//     */
//    public List<Tag> getFitTag(String name,long taskID);

    /**
     *根据tagId删除Tag,删除成功返回SUCCESS，删除失败返回FAILED
     */
    public ResultMessage deleteTag(long tagId);
    /**
     * 保存任务Task,返回SUCCESS的情况，可以得到id
     */
    public List<Object> addTask(TaskPublisher taskPublisher);

//    /**
//     * 拿到新的发布者任务ID
//     * @return
//     */
//    public long getNewTaskId();

    /**
     * 根据状态拿到发起者角度的业务
     * @param taskState
     * @return
     */
    public PageDTO<TaskPublisher> findTaskPublisherByState(long userId, TaskState taskState, Integer size, Integer currentPage);

    /**
     * 更新taskPublisher中imageList,删掉压缩包名
     * @param taskId
     * @param imageInZipList
     * @param zipName
     * @return
     */
    ResultMessage updateImageList(Long taskId, List<String> imageInZipList, String zipName);

    /**
     * 根据TaskPublisherId得到所有的TaskRecords
     * @param taskPublisherId
     * @return
     */
    Map<Long,Double> getAllTaskRecordsByTaskPublisherId(Long taskPublisherId);
}
