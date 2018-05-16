package horizon.taglib.dao;

import horizon.taglib.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 日志操作持久化对象接口
 * <br>
 * created on 2018/04/25
 *
 * @author 巽
 **/
public interface LogDao extends JpaRepository<Log, Long>, BaseRepository<Log, Long> {
	/**
	 * 得到所有日志
	 *
	 * @return 所有日志记录的集合，若无则返回空表
	 */
	List<Log> findAll();
}
