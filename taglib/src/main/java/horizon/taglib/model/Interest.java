package horizon.taglib.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.io.Serializable;


@Getter
@Setter
@Entity
public class Interest extends PO implements Serializable{

    /* 行id */
    Long id;

    /* 用户id */
    Long userId;

    /* 话题 */
    String topic;

    /* 兴趣因子 */
    Long interestFactor;

    public Interest() {
    }
}
