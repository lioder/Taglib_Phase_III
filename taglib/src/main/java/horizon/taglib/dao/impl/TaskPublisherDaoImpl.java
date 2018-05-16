//package horizon.taglib.dao.impl;
//
//import horizon.taglib.dao.DaoHelper;
//import horizon.taglib.dao.TaskPublisherDao;
//import horizon.taglib.enums.QueryMode;
//import horizon.taglib.enums.ResultMessage;
//import horizon.taglib.enums.TaskState;
//import horizon.taglib.enums.TaskType;
//import horizon.taglib.model.TaskPublisher;
//import horizon.taglib.utils.Criterion;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import java.util.*;
//
///**
// * 操作任务持久化对象实现类
// * <br>
// * created on 2018/03/20
// *
// * @author 巽
// **/
//@Repository
//public class TaskPublisherDaoImpl implements TaskPublisherDao {
//	private DaoHelper<TaskPublisher> daoHelper;
//
//	@Autowired
//	public TaskPublisherDaoImpl(DaoHelper<TaskPublisher> daoHelper) {
//		this.daoHelper = daoHelper;
//		daoHelper.initClass(TaskPublisher.class);
//
////		this.tmpCreateData();
//	}
//
//	/**
//	 * 项目构建阶段用于临时创建数据以方便调试
//	 */
//	@SuppressWarnings("unused")
//	private void tmpCreateData() {
//		if (daoHelper.count() < 3) {
//			String[] names = {"191003287_2915c11d8e.jpg", "69189650_6687da7280.jpg", "191003284_1025b0fb7d.jpg",
//					"237547381_aa17c805e0.jpg", "263854883_0f320c1562.jpg", "1351764581_4d4fb1b40f.jpg",
//					"2513260012_03d33305cf.jpg"};
//			TaskType[] types = {TaskType.BOX, TaskType.REGION, TaskType.SORT};
//			List<TaskType> typeList = new ArrayList<>(Arrays.asList(types));
//			Collections.shuffle(typeList);
//			for (TaskType aTypeList : typeList) {
//				TaskPublisher taskPublisher = new TaskPublisher();
//				taskPublisher.setTitle("Le wants " + String.valueOf(daoHelper.count() + 1) + " animals");
//				taskPublisher.setDescription("针对图像中具体情景，用中文进行一句话描述，并框出其中的生物");
//				List<String> images = new ArrayList<>();
//				for (int number = (int) (Math.random() * names.length); number >= 0; number--) {
//					images.save(names[number]);
//				}
//				Collections.shuffle(images);    // 打乱
//				taskPublisher.setImages(images);
//				List<String> labels = new ArrayList<>();
//				taskPublisher.setLabels(labels);
//				taskPublisher.setTaskType(aTypeList);
//				daoHelper.save(taskPublisher);
//			}
//		}
//	}
//
//	/**
//	 * 得到所有Task
//	 *
//	 * @return 包含所有Task的列表
//	 */
//	@Override
//	public List<TaskPublisher> findAll() {
//		return daoHelper.multiQuery(new ArrayList<>());
//	}
//
//	/**
//	 * 根据Task的id得到Task
//	 *
//	 * @param id Task的id
//	 * @return 查找到的Task，若无则返回null
//	 */
//	@Override
//	public TaskPublisher findOne(Long id) {
//		return daoHelper.exactlyQuery("id", id);
//	}
//
//	/**
//	 * 增加Task
//	 *
//	 * @param taskPublisher 待添加的Task
//	 * @return SUCCESS：添加成功
//	 */
//	@Override
//	public ResultMessage save(TaskPublisher taskPublisher) {
//		return daoHelper.save(taskPublisher);
//	}
//
//	/**
//	 * 删除Task
//	 *
//	 * @param id 待删除的Task的id
//	 * @return SUCCESS：删除成功<br>
//	 * NOT_EXIST：待删除的Task不存在
//	 */
//	@Override
//	public ResultMessage delete(Long id) {
//		return daoHelper.delete(id);
//	}
//
//	/**
//	 * 更新Task
//	 *
//	 * @param taskPublisher 待更新的Task
//	 * @return SUCCESS：更新成功<br>
//	 * NOT_EXIST：待更新的Task不存在
//	 */
//	@Override
//	public ResultMessage save(TaskPublisher taskPublisher) {
//		return daoHelper.save(taskPublisher);
//	}
//
//	/**
//	 * 获取新Task的id（不触发id自增，即获取的id并未对应到Task）
//	 *
//	 * @return 新id
//	 */
//	@Override
//	public Long getNewId() {
//		return daoHelper.getNewId();
//	}
//
//	/**
//	 * 根据发布者id和任务状态得到所有TaskPublisher
//	 *
//	 * @param userId    发布者id
//	 * @param taskState 任务状态
//	 * @return 包含所有符合条件的任务的列表，若无则返回空表
//	 */
//	@Override
//	public List<TaskPublisher> findByUserIdAndTaskState(Long userId, TaskState taskState) {
//		ArrayList<Criterion> criteria = new ArrayList<>();
//		criteria.save(new Criterion<>("userId", userId, QueryMode.FULL));
//		criteria.save(new Criterion<>("taskState", taskState, QueryMode.FULL));
//		return daoHelper.multiQuery(criteria);
//	}
//
//	/**
//	 * 多重条件查询
//	 *
//	 * @param criteria 所有条件的集合
//	 * @return 查询到的对象的集合
//	 */
//	@Override
//	public List<TaskPublisher> multiQuery(List<Criterion> criteria) {
//		return daoHelper.multiQuery(criteria);
//	}
//
//	/**
//	 * 完全匹配，多用于查询字段为某个值的所有TaskPublisher
//	 *
//	 * @param field 要查询的字段，字段类型为集合类型(extends Collection)时调用Collection.contains(value)进行匹配
//	 * @param value 要匹配的值
//	 * @return 查询到的所有TaskPublisher的集合
//	 */
//	@Override
//	public List<TaskPublisher> fullyQuery(String field, Object value) {
//		return daoHelper.fullyQuery(field, value);
//	}
//}
