package horizon.taglib.vo;

import horizon.taglib.enums.TaskType;
import lombok.Data;

import java.util.List;

/*
    任务的缩略信息
 */
@Data
public class TaskInfoVO {
    /**
     * 任务id
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 任务首图的名称
     */
    private String picName;
    /**
     * 任务所有图片数量
     */
    private Integer picNum;
    /**
     * 任务类型
     */
    private Integer taskType;
    /**
     * 任务所属类型
     */
    private List<String> topics;
    /**
     * 任务单价
     */
    private Double price;
    /**
     * 创建时间
     */
    private String startDate;
    /**
     * 结束时间
     */
    private String endDate;
    /**
     * 评价
     */
    private Double rating;

    public TaskInfoVO(){

    }

    public TaskInfoVO(Long id, String title, String description, String picName, Integer picNum, Integer taskType, List<String> topics, Double price, String startDate, String endDate, Double rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.picName = picName;
        this.picNum = picNum;
        this.taskType = taskType;
        this.topics = topics;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rating = rating;
    }
}
