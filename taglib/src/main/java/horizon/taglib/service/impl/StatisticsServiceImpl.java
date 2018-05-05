package horizon.taglib.service.impl;

import horizon.taglib.dao.LogDao;
import horizon.taglib.dao.TaskPublisherDao;
import horizon.taglib.dao.TaskWorkerDao;
import horizon.taglib.dao.UserDao;
import horizon.taglib.enums.OperationType;
import horizon.taglib.enums.QueryMode;
import horizon.taglib.enums.UserType;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.TaskWorker;
import horizon.taglib.model.User;
import horizon.taglib.service.StatisticsService;
import horizon.taglib.utils.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {
	private TaskPublisherDao taskPublisherDao;
	private TaskWorkerDao taskWorkerDao;
	private UserDao userDao;
	private LogDao logDao;

	@Autowired
	public StatisticsServiceImpl(TaskPublisherDao taskPublisherDao, TaskWorkerDao taskWorkerDao, UserDao userDao, LogDao logDao) {
		this.taskPublisherDao = taskPublisherDao;
		this.taskWorkerDao = taskWorkerDao;
		this.userDao = userDao;
		this.logDao = logDao;
	}

	@Override
	public List<TaskPublisher> getAllTaskPublishers() {
		return taskPublisherDao.getAllTasks();
	}

	@Override
	public List<TaskWorker> getAllTaskWorkers() {
		return taskWorkerDao.getAllTaskWorker();
	}

	@Override
	public List<TaskWorker> getTaskWorkersByUserId(long id) {
		User user = userDao.findById(id);
		List<Long> taskIds = user.getMyTasks();
		List<TaskWorker> taskWorkers = new ArrayList<>();
		for (Long taskId : taskIds) {
			taskWorkers.add(taskWorkerDao.findById(taskId));
		}
		return taskWorkers;
	}

	@Override
	public List<User> allParticipateUsersById(Long taskPublisherId) {
		//得到所有工人
		List<User> allUsers = userDao.getAllUser();
		List<User> workers = new ArrayList<>();
		for (User user : allUsers) {
			if (user.getUserType() == UserType.WORKER) {
				workers.add(user);
			}
		}
		//筛选出所有参与过该任务的用户
		List<User> res = new ArrayList<>();
		for (User user : workers) {
			List<Long> taskWorkerIds = user.getMyTasks();
			for (Long id : taskWorkerIds) {
				if (taskWorkerDao.findById(id).getTaskPublisherId().equals(taskPublisherId)) {
					res.add(user);
				}
			}
		}
		return res;
	}

	/**
	 * 统计某时间段内的新注册用户数量
	 *
	 * @param startDate 开始日期
	 * @param endDate   截止日期
	 * @return 统计数量
	 */
	@Override
	public Long countNewUserByDates(LocalDate startDate, LocalDate endDate) {
		// 遍历查找所有添加User的记录
		ArrayList<Criterion> criteria = new ArrayList<>();
		criteria.add(new Criterion<>("operationType", OperationType.ADD, QueryMode.FULL));
		criteria.add(new Criterion<>("operationObjectType", User.class, QueryMode.FULL));
		criteria.add(new Criterion<>("dateAndTime", LocalDateTime.of(startDate, LocalTime.of(0, 0)), LocalDateTime.of(endDate, LocalTime.of(23, 59, 59, 999999999))));
		return (long) logDao.multiQuery(criteria).size();
	}
}
