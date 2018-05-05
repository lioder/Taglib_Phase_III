package horizon.taglib.dao.impl;

import horizon.taglib.dao.DaoHelper;
import horizon.taglib.dao.TaskWorkerDao;
import horizon.taglib.enums.QueryMode;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.TaskState;
import horizon.taglib.model.TaskWorker;
import horizon.taglib.utils.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * no_description
 * <br>
 * created on 2018/04/06
 *
 * @author 巽
 **/
@Repository
public class TaskWorkerDaoImpl implements TaskWorkerDao {
	private DaoHelper<TaskWorker> daoHelper;

	@Autowired
	public TaskWorkerDaoImpl(DaoHelper<TaskWorker> daoHelper) {
		this.daoHelper = daoHelper;
		daoHelper.initClass(TaskWorker.class);
	}

	/**
	 * 添加TaskWorker（未分配id）
	 *
	 * @param taskWorker 待添加的TaskWorker
	 * @return SUCCESS：添加成功
	 */
	@Override
	public ResultMessage addTaskWorker(TaskWorker taskWorker) {
		return daoHelper.add(taskWorker);
	}

	/**
	 * 更新TaskWorker
	 *
	 * @param taskWorker 更新后的任务情况
	 * @return SUCCESS：更新成功<br>
	 * NOT_EXIST：待更新的TaskWorker不存在
	 */
	@Override
	public ResultMessage updateTaskWorker(TaskWorker taskWorker) {
		return daoHelper.update(taskWorker);
	}

	/**
	 * 删除TaskWorker
	 *
	 * @param id 待删除的TaskWorker
	 * @return SUCCESS：删除成功<br>
	 * NOT_EXIST：待删除的TaskWorker不存在
	 */
	@Override
	public ResultMessage deleteTaskWorker(Long id) {
		return daoHelper.delete(id);
	}

	/**
	 * 根据用户id和任务状态查找TaskWorker
	 *
	 * @param userId    用户id
	 * @param taskState 任务状态
	 * @return 查找到的TaskWorker的列表，若一个也无则为空列表
	 */
	@Override
	public List<TaskWorker> findByUserIdAndTaskState(Long userId, TaskState taskState) {
		ArrayList<Criterion> criteria = new ArrayList<>();
		criteria.add(new Criterion<>("userId", userId, QueryMode.FULL));
		criteria.add(new Criterion<>("taskState", taskState, QueryMode.FULL));
		return daoHelper.multiQuery(criteria);
	}

	/**
	 * 通过id得到TaskWorker对象
	 *
	 * @param id 任务情况id
	 * @return 查找到的TaskWorker对象，若无则为null
	 */
	@Override
	public TaskWorker findById(Long id) {
		return daoHelper.exactlyQuery("id", id);
	}

	/**
	 * 获取新Task的id（不触发id自增，即获取的id并未对应到Task）
	 *
	 * @return 新id
	 */
	@Override
	public Long getNewId() {
		return daoHelper.getNewId();
	}

	/**
	 * 根据任务状态查找TaskWorker
	 *
	 * @param taskState 任务状态
	 * @return 查找到的TaskWorker集合，若无则返回空表
	 */
	@Override
	public List<TaskWorker> findByTaskState(TaskState taskState) {
		return daoHelper.fullyQuery("taskState", taskState);
	}

	/**
	 * 得到所有TaskWorker
	 *
	 * @return 包含所有TaskWorker的集合，若一个也无则返回空表
	 */
	@Override
	public List<TaskWorker> getAllTaskWorker() {
		return daoHelper.multiQuery(new ArrayList<>());
	}

	/**
	 * 多重条件查询
	 *
	 * @param criteria 所有条件的集合
	 * @return 查询到的对象的集合
	 */
	@Override
	public List<TaskWorker> multiQuery(List<Criterion> criteria) {
		return daoHelper.multiQuery(criteria);
	}
}
