package horizon.taglib.model;

import horizon.taglib.model.multikeys.ActivityMultiKeys;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * 用户每日活跃度
 * <br>
 * created on 2018/05/24
 *
 * @author 巽
 **/
@Setter
@Getter
@Entity
@Table(name = "user_activity")
@IdClass(ActivityMultiKeys.class)
public class Activity {
	/**
	 * 用户id
	 */
	@Id
	private Long userId;
	/**
	 * 日期
	 */
	@Id
	private LocalDate date;
	/**
	 * 活跃度计数
	 */
	private Integer count;

	public Activity() {
	}

	public Activity(Long userId, LocalDate date, Integer count) {
		this.userId = userId;
		this.date = date;
		this.count = count;
	}
}
