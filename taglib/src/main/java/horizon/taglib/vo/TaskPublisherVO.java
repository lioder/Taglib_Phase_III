package horizon.taglib.vo;

import horizon.taglib.enums.TaskState;
import horizon.taglib.enums.TaskType;
import lombok.Data;

import java.util.List;

/**
 * 任务的具体信息
 */
@Data
public class TaskPublisherVO {
    /**
     * 任务id
     */
    private Long id;
    /**
     * 创建task的用户Id
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
     * 数据的id的集合<br>
     * List&lt;imageId&gt;
     */
    private List<String> images;
    /**
     * 分类任务类型的标签集合
     */
    private List<String> labels;
    /**
     * 任务所属类型
     */
    private List<String> topics;
    /**
     * 任务总价
     */
    private Double price;
    /**
     * 一张图片所需的标注人数
     */
    private Long numPerPic;
    /**
     * 任务发布的时间
     */
    private String startDate;
    /**
     * 结束时间
     */
    private String endDate;
    /**
     * 任务状态
     */
    private TaskState taskState;
    /**
     * 好评度
     */
    private Double rating;
    /**
     * 热度初值
     */
    private Double hotCount;
    /**
     * 热度
     */
    private Double hotRank;

    public TaskPublisherVO() {
    }

    public TaskPublisherVO(Long id, Long userId, String title, String description, Integer taskType, List<String> images, List<String> labels, List<String> topics, Double price, Long numPerPic, String startDate, String endDate, TaskState taskState, Double rating, Double hotCount, Double hotRank) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.taskType = taskType;
        this.images = images;
        this.labels = labels;
        this.topics = topics;
        this.price = price;
        this.numPerPic = numPerPic;
        this.startDate = startDate;
        this.endDate = endDate;
        this.taskState = taskState;
        this.rating = rating;
        this.hotCount = hotCount;
        this.hotRank = hotRank;
    }
}
