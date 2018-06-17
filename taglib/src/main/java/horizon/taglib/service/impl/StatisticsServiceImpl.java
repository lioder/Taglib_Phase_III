package horizon.taglib.service.impl;

import horizon.taglib.dao.LogDao;
import horizon.taglib.dao.TaskPublisherDao;
import horizon.taglib.dao.TaskWorkerDao;
import horizon.taglib.dao.UserDao;
import horizon.taglib.dto.TimeCircleDTO;
import horizon.taglib.enums.*;
import horizon.taglib.model.Log;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.TaskWorker;
import horizon.taglib.model.User;
import horizon.taglib.service.StatisticsService;
import horizon.taglib.utils.Criterion;
import horizon.taglib.vo.TaskProVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
		return taskPublisherDao.findAll();
	}

	@Override
	public List<TaskWorker> getAllTaskWorkers() {
		return taskWorkerDao.findAll();
	}

	@Override
	public List<TaskWorker> getTaskWorkersByUserId(long id) {
		User user = userDao.findOne(id);
		List<Long> taskIds = user.getMyTasks();
		List<TaskWorker> taskWorkers = new ArrayList<>();
		for (Long taskId : taskIds) {
			taskWorkers.add(taskWorkerDao.findOne(taskId));
		}
		return taskWorkers;
	}

	@Override
	public List<User> allParticipateUsersById(Long taskPublisherId) {
		//得到所有工人
		List<User> allUsers = userDao.findAll();
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
				if (taskWorkerDao.findOne(id).getTaskPublisherId().equals(taskPublisherId)) {
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
		criteria.add(new Criterion<>("operationObjectType", User.class.getSimpleName(), QueryMode.FULL));
		criteria.add(new Criterion<>("dateAndTime", LocalDateTime.of(startDate, LocalTime.of(0, 0)), LocalDateTime.of(endDate, LocalTime.of(23, 59, 59, 999999999))));
		return (long) logDao.multiQuery(criteria).size();
	}

	@Override
	public List<User> getWorkers(){
		return userDao.findByUserType(UserType.WORKER);
	}

	/**
	 * 统计时间段内的任务（发布者视角）的生命周期信息
	 *
	 * @param start 开始时间
	 * @param end 截止时间
	 * @return 各任务（发布者视角）的生命周期信息
	 */
	@Override
	public List<TimeCircleDTO> getTaskTimelines(LocalDate start, LocalDate end) {
		LocalDateTime startDateTime = LocalDateTime.of(start, LocalTime.of(0, 0));
		LocalDateTime endDateTime = LocalDateTime.of(start, LocalTime.of(23, 59, 59));
		List<TimeCircleDTO> ret = new ArrayList<>();
		List<TaskPublisher> taskPublishers = taskPublisherDao.findAll();
		Set<TaskState> states = new HashSet<>(Arrays.asList(TaskState.SUBMITTED, TaskState.PASS, TaskState.PROCESSING,
				TaskState.DONE, TaskState.OVERTIME));   // 符合要求的任务状态
		for (TaskPublisher taskPublisher : taskPublishers) {
			LocalDateTime taskStartDate = LocalDateTime.parse(
					taskPublisher.getStartDate(), DateTimeFormatter.ofPattern(TaskPublisher.getDateFormat()));
			if (taskStartDate.isAfter(startDateTime) && taskStartDate.isBefore(endDateTime)
					&& states.contains(taskPublisher.getTaskState())) { // 任务开始时间在查找时间段内且状态符合要求
				LocalDateTime adminExamineTime = null;  // 管理员审核时间
				LocalDateTime expertSubmitTime = null;  // 专家提交专家任务时间
				LocalDateTime autoExamineTime = null;   // 自动审核任务时间
				// 查找管理员审核任务日志
				List<Log> found = logDao.findByOperationTypeAndOperationObjectType(
						OperationType.ADMIN_EXAMINE, String.valueOf(taskPublisher.getId()));
				if (found.size() > 0) {
					adminExamineTime = found.get(0).getDateAndTime();
				}
				// 查找专家提交专家任务日志
				found = logDao.findByOperationTypeAndOperationObjectTypeAndDetailsContaining(
						OperationType.ADD, TaskProVO.class.getSimpleName(),
						"taskPublisherId=" + taskPublisher.getId() + ",");
				if (found.size() > 0) {
					expertSubmitTime = found.get(0).getDateAndTime();
				}
				// 查找自动审核任务日志
				found = logDao.findByOperationTypeAndOperationObjectType(
						OperationType.AUTO_EXAMINE, String.valueOf(taskPublisher.getId()));
				if (found.size() > 0) {
					autoExamineTime = found.get(0).getDateAndTime();
				}

				TimeCircleDTO timeCircleDTO = new TimeCircleDTO(taskPublisher.getId(), taskStartDate, adminExamineTime,
						expertSubmitTime, autoExamineTime, LocalDateTime.parse(taskPublisher.getEndDate(),
						DateTimeFormatter.ofPattern(TaskPublisher.getDateFormat())));
				ret.add(timeCircleDTO);
			}
		}
		return ret;
	}
}