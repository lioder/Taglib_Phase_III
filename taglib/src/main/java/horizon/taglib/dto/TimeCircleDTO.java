package horizon.taglib.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TimeCircleDTO {

    /**
     * 发布者任务Id
     */
    private Long taskPublisherId;

    /**
     * 任务发布时间
     */
    private LocalDate publishTime;

    /**
     * 管理员审核时间
     */
    private LocalDate adminExamineTime;

    /**
     * 专家提交时间
     */
    private LocalDate expertSubmitTime;

    /**
     * 自动审核时间
     */
    private LocalDate autoExamineTime;

    /**
     * 任务截止时间
     */
    private LocalDate endTime;

    public TimeCircleDTO(Long taskPublisherId, LocalDate publishTime, LocalDate adminExamineTime, LocalDate expertSubmitTime, LocalDate autoExamineTime, LocalDate endTime) {
        this.taskPublisherId = taskPublisherId;
        this.publishTime = publishTime;
        this.adminExamineTime = adminExamineTime;
        this.expertSubmitTime = expertSubmitTime;
        this.autoExamineTime = autoExamineTime;
        this.endTime = endTime;
    }
}
