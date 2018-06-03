package horizon.taglib.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@Entity
public class TaskRecord extends PO implements Serializable {

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

    public TaskRecord() {
    }

    public TaskRecord(Long userId, Long taskPublisherId, LocalDate date, Double price, Integer correct, Integer sum) {
        this.userId = userId;
        this.taskPublisherId = taskPublisherId;
        this.date = date;
        this.price = price;
        this.correct = correct;
        this.sum = sum;
    }
}
