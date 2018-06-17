package horizon.taglib.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TimeCircleVO {
    /**
     * 发布者任务Id
     */
    private Long taskPublisherId;

    /**
     * 任务发布时间至管理员审核时间
     */
    private Long publishToExamineTime;

    /**
     * 管理员审核时间至专家提交时间
     */
    private Long examineToExpertSubmitTime;

    /**
     * 专家提交时间至自动审核时间
     */
    private Long expertSubmitToAutoExamineTime;

    /**
     * 自动审核时间至任务截止时间
     */
    private Long autoExamineToEndTime;

    public TimeCircleVO() {
    }

    public TimeCircleVO(Long taskPublisherId, Long publishToExamineTime, Long examineToExpertSubmitTime, Long expertSubmitToAutoExamineTime, Long autoExamineToEndTime) {
        this.taskPublisherId = taskPublisherId;
        this.publishToExamineTime = publishToExamineTime;
        this.examineToExpertSubmitTime = examineToExpertSubmitTime;
        this.expertSubmitToAutoExamineTime = expertSubmitToAutoExamineTime;
        this.autoExamineToEndTime = autoExamineToEndTime;
    }
}
