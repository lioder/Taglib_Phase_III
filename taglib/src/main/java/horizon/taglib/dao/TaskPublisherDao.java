package horizon.taglib.dao;

import horizon.taglib.enums.TaskState;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.TaskWorker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 任务操作持久化对象接口
 * <br>
 * created on 2018/03/20
 *
 * @author 巽
 **/
public interface TaskPublisherDao extends JpaRepository<TaskPublisher, Long>, BaseRepository<TaskPublisher, Long> {

	/**
	 * 根据发布者id和任务状态得到所有TaskPublisher
	 *
	 * @param userId    发布者id
	 * @param taskState 任务状态
	 * @return 包含所有符合条件的任务的列表，若无则返回空表
	 */
	List<TaskPublisher> findByUserIdAndTaskState(Long userId, TaskState taskState);
}
