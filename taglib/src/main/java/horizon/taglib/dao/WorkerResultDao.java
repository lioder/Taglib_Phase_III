package horizon.taglib.dao;

import horizon.taglib.model.WorkerResult;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 工人任务结果操作持久化对象接口
 * <br>
 * created on 2018/06/08
 *
 * @author 巽
 **/
public interface WorkerResultDao extends JpaRepository<WorkerResult, Long>, BaseRepository<WorkerResult, Long> {
}
