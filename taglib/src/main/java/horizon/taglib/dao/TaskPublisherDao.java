package horizon.taglib.dao;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.TaskState;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.utils.Criterion;

import java.util.List;

/**
 * 任务操作持久化对象接口
 * <br>
 * created on 2018/03/20
 *
 * @author 巽
 **/
public interface TaskPublisherDao {
	/**
	 * 得到所有Task
	 *
	 * @return 包含所有Task的列表
	 */
	List<TaskPublisher> getAllTasks();

	/**
	 * 根据Task的id得到Task
	 *
	 * @param id Task的id
	 * @return 查找到的Task，若无则返回null
	 */
	TaskPublisher getTaskByTaskID(Long id);

	/**
	 * 增加Task
	 *
	 * @param taskPublisher 待添加的Task（没有id）
	 * @return SUCCESS：添加成功
	 */
	ResultMessage add(TaskPublisher taskPublisher);

	/**
	 * 删除Task
	 *
	 * @param id 待删除的Task的id
	 * @return SUCCESS：删除成功<br>
	 * NOT_EXIST：待删除的Task不存在
	 */
	ResultMessage delete(Long id);

	/**
	 * 更新Task
	 *
	 * @param taskPublisher 待更新的Task
	 * @return SUCCESS：更新成功<br>
	 * NOT_EXIST：待更新的Task不存在
	 */
	ResultMessage update(TaskPublisher taskPublisher);

	/**
	 * 获取新Task的id（不触发id自增，即获取的id并未分配给某个Task）
	 *
	 * @return 新id
	 */
	Long getNewId();

	/**
	 * 根据发布者id和任务状态得到所有TaskPublisher
	 *
	 * @param userId    发布者id
	 * @param taskState 任务状态
	 * @return 包含所有符合条件的任务的列表，若无则返回空表
	 */
	List<TaskPublisher> getByUserIdAndTaskState(Long userId, TaskState taskState);

	/**
	 * 多重条件查询
	 *
	 * @param criteria 所有条件的集合
	 * @return 查询到的对象的集合
	 */
	List<TaskPublisher> multiQuery(List<Criterion> criteria);

	/**
	 * 完全匹配，多用于查询字段为某个值的所有TaskPublisher
	 *
	 * @param field 要查询的字段，字段类型为集合类型(extends Collection)时调用Collection.contains(value)进行匹配
	 * @param value 要匹配的值
	 * @return 查询到的所有TaskPublisher的集合
	 */
	List<TaskPublisher> fullyQuery(String field, Object value);
}
