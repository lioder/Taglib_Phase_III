package horizon.taglib.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRecordVO {

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 任务Id
     */
    private Long taskPublisherId;

    /**
     * 审核时间
     */
    private LocalDate date;

    /**
     * 任务奖励
     */
    private Double price;

    /**
     * 正确tag数
     */
    private Integer correct;

    /**
     * 总tag数
     */
    private Integer sum;

    public TaskRecordVO() {
    }

    public TaskRecordVO(Long userId, Long taskPublisherId, LocalDate date, Double price, Integer correct, Integer sum) {
        this.userId = userId;
        this.taskPublisherId = taskPublisherId;
        this.date = date;
        this.price = price;
        this.correct = correct;
        this.sum = sum;
    }
}
