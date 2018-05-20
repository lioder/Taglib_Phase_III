package horizon.taglib.dao;

import horizon.taglib.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 日志操作持久化对象接口
 * <br>
 * created on 2018/04/25
 *
 * @author 巽
 **/
public interface LogDao extends JpaRepository<Log, Long>, BaseRepository<Log, Long> {
}
