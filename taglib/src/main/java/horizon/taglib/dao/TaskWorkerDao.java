package horizon.taglib.dao;

import horizon.taglib.enums.TaskState;
import horizon.taglib.model.TaskWorker;
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
//	/**
//	 * 添加TaskWorker（未分配id）
//	 * @param taskWorker 待添加的TaskWorker
//	 * @return SUCCESS：添加成功
//	 */
//	ResultMessage save(TaskWorker taskWorker);
//	/**
//	 * 更新TaskWorker
//	 * @param taskWorker 更新后的任务情况
//	 * @return SUCCESS：更新成功<br>
//	 * NOT_EXIST：待更新的TaskWorker不存在
//	 */
//	ResultMessage save(TaskWorker taskWorker);
//	/**
//	 * 删除TaskWorker
//	 * @param id 待删除的TaskWorker
//	 * @return SUCCESS：删除成功<br>
//	 * NOT_EXIST：待删除的TaskWorker不存在
//	 */
//	ResultMessage delete(Long id);
	/**
	 * 根据用户id和任务状态查找TaskWorker
	 * @param userId 用户id
	 * @param taskState 任务状态
	 * @return 查找到的TaskWorker的列表，若一个也无则为空列表
	 */
	List<TaskWorker> findByUserIdAndTaskState(Long userId, TaskState taskState);
//	/**
//	 * 通过id得到TaskWorker对象
//	 * @param id 任务情况id
//	 * @return 查找到的TaskWorker对象，若无则为null
//	 */
//	TaskWorker findOne(Long id);

//	/**
//	 * 获取新Task的id（不触发id自增，即获取的id并未分配给某一Task）
//	 *
//	 * @return 新id
//	 */
//	Long getNewId();

	/**
	 * 根据任务状态查找TaskWorker
	 *
	 * @param taskState 任务状态
	 * @return 查找到的TaskWorker集合，若无则返回空表
	 */
	List<TaskWorker> findByTaskState(TaskState taskState);

//	/**
//	 * 得到所有TaskWorker
//	 *
//	 * @return 包含所有TaskWorker的集合，若一个也无则返回空表
//	 */
//	List<TaskWorker> findAll();

//	/**
//	 * 多重条件查询
//	 *
//	 * @param criteria 所有条件的集合
//	 * @return 查询到的对象的集合
//	 */
//	List<TaskWorker> multiQuery(List<Criterion> criteria);
}
