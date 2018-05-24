package horizon.taglib.dao;

import horizon.taglib.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * 活跃度操作持久化对象接口
 * <br>
 * created on 2018/05/24
 *
 * @author 巽
 **/
public interface ActivityDao extends JpaRepository<Activity, Long>, BaseRepository<Activity, Long> {
	/**
	 * 通过用户id查找时间段内的用户活跃度，若无则返回空表
	 *
	 * @param userId 用户id
	 * @param lowerLimit （含）下限
	 * @param upperLimit （含）上限
	 * @return 用户活跃度的列表
	 */
	List<Activity> findByUserIdAndDateBetween(Long userId, LocalDate lowerLimit, LocalDate upperLimit);

	/**
	 * 通过用户id和日期查找用户活跃度，若无则返回空表（理论上应该只有一个）
	 *
	 * @param userId 用户id
	 * @param date 日期
	 * @return 用户活跃度的列表
	 */
	List<Activity> findByUserIdAndDate(Long userId, LocalDate date);
}
