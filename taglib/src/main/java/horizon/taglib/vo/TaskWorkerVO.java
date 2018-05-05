package horizon.taglib.vo;

import horizon.taglib.enums.TaskState;
import lombok.Data;

import java.util.List;

@Data
public class TaskWorkerVO {

    /**
     * 用户task的id
     */
    private Long id;
    /**
     * 所属公共任务Id
     */
    private Long taskId;
    /**
     * 用户Id
     */
    private Long userId;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 任务类型
     */
    private Integer taskType;
    /**
     * 任务价格
     */
    private Double price;
    /**
     * 任务状态
     *
     */
    private TaskState taskState;
    /**
     * 数据的id的集合<br>
     * List&lt;imageId&gt;
     */
    private List<QuestionVO> images;
    /**
     * 分类任务类型的标签集合
     */
    private List<String> labels;
    /**
     * 任务所属类型
     */
    private List<String> topics;
    /**
     * 接受任务的时间
     */
    private String startDate;
    /**
     * 结束时间
     */
    private String endDate;
    /**
     * 用户对该任务的评价
     */
    private Integer rating;

    public TaskWorkerVO(){

    }

    public TaskWorkerVO(Long id, Long taskId, Long userId, String title, String description, Integer taskType, Double price, TaskState taskState, List<QuestionVO> images, List<String> labels, List<String> topics, String startDate, String endDate, Integer rating) {
        this.id = id;
        this.taskId = taskId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.taskType = taskType;
        this.price = price;
        this.taskState = taskState;
        this.images = images;
        this.labels = labels;
        this.topics = topics;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rating = rating;
    }
}
