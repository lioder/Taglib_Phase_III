package horizon.taglib.model.multikeys;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Activity的主键类
 * <br>
 * created on 2018/05/24
 *
 * @author 巽
 **/
@Setter
@Getter
public class ActivityMultiKeys implements Serializable {
	/**
	 * 用户id
	 */
	private Long userId;
	/**
	 * 日期
	 */
	private LocalDate date;

	public ActivityMultiKeys() {
	}

	public ActivityMultiKeys(Long userId, LocalDate date) {
		this.userId = userId;
		this.date = date;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ActivityMultiKeys that = (ActivityMultiKeys) o;
		return Objects.equals(getUserId(), that.getUserId()) &&
				Objects.equals(getDate(), that.getDate());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getUserId(), getDate());
	}
}
