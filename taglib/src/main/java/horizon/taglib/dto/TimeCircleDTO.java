package horizon.taglib.dto;

import java.time.LocalDate;

public class TimeCircleDTO {

    /**
     * 发布者任务Id
     */
    Long taskPublisherId;

    /**
     * 任务发布时间
     */
    LocalDate publishTime;

    /**
     * 管理员审核时间
     */
    LocalDate adminExamineTime;

    /**
     * 专家提交时间
     */
    LocalDate expertSubmitTime;

    /**
     * 自动审核时间
     */
    LocalDate autoExamineTime;

    /**
     * 任务截止时间
     */
    LocalDate endTime;

    public TimeCircleDTO(Long taskPublisherId, LocalDate publishTime, LocalDate adminExamineTime, LocalDate expertSubmitTime, LocalDate autoExamineTime, LocalDate endTime) {
        this.taskPublisherId = taskPublisherId;
        this.publishTime = publishTime;
        this.adminExamineTime = adminExamineTime;
        this.expertSubmitTime = expertSubmitTime;
        this.autoExamineTime = autoExamineTime;
        this.endTime = endTime;
    }
}
