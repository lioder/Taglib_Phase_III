package horizon.taglib.dao;

import horizon.taglib.enums.TaskState;
import horizon.taglib.model.TaskWorker;
import javafx.concurrent.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 任务（众包工人视角）情况操作持久化对象接口
 * <br>
 * created on 2018/04/06
 *
 * @author 巽
 **/
public interface TaskWorkerDao extends JpaRepository<TaskWorker, Long>, BaseRepository<TaskWorker, Long> {
	/**
	 * 根据用户id和任务状态查找TaskWorker
	 * @param userId 用户id
	 * @param taskState 任务状态
	 * @return 查找到的TaskWorker的列表，若一个也无则为空列表
	 */
	List<TaskWorker> findByUserIdAndTaskState(Long userId, TaskState taskState);

	/**
	 * 根据任务状态查找TaskWorker
	 *
	 * @param taskState 任务状态
	 * @return 查找到的TaskWorker集合，若无则返回空表
	 */
	List<TaskWorker> findByTaskState(TaskState taskState);

	List<TaskWorker> findByTaskPublisherIdAndTaskState(Long taskPublisherId, TaskState taskState);

	TaskWorker findByUserIdAndTaskPublisherId(Long userId, Long taskPublisherId);
}
