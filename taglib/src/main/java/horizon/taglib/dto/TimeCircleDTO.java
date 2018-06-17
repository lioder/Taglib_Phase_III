package horizon.taglib.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeCircleDTO {

    /**
     * 发布者任务Id
     */
    private Long taskPublisherId;

    /**
     * 任务发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 管理员审核时间
     */
    private LocalDateTime adminExamineTime;

    /**
     * 专家提交时间
     */
    private LocalDateTime expertSubmitTime;

    /**
     * 自动审核时间
     */
    private LocalDateTime autoExamineTime;

    /**
     * 任务截止时间
     */
    private LocalDateTime endTime;

    public TimeCircleDTO(Long taskPublisherId, LocalDateTime publishTime, LocalDateTime adminExamineTime, LocalDateTime expertSubmitTime, LocalDateTime autoExamineTime, LocalDateTime endTime) {
        this.taskPublisherId = taskPublisherId;
        this.publishTime = publishTime;
        this.adminExamineTime = adminExamineTime;
        this.expertSubmitTime = expertSubmitTime;
        this.autoExamineTime = autoExamineTime;
        this.endTime = endTime;
    }
}
