package horizon.taglib.service;

import horizon.taglib.dao.TaskPublisherDao;
import horizon.taglib.dao.TaskWorkerDao;
import horizon.taglib.dao.UserDao;
import horizon.taglib.enums.QueryMode;
import horizon.taglib.enums.TaskState;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.TaskWorker;
import horizon.taglib.model.User;
import horizon.taglib.utils.Criterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class ScheduleTasks {
	private Logger logger = LoggerFactory.getLogger(ScheduleTasks.class);

	private UserService userService;

	private UserDao userDao;
	private TaskPublisherDao taskPublisherDao;
	private TaskWorkerDao taskWorkerDao;

	private Integer isAttendantCount = 1;
	private Integer hotRankCount = 1;
	private Integer taskPublisherExpiredCount = 1;

	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	@Autowired
	public ScheduleTasks(UserDao userDao, TaskPublisherDao taskPublisherDao, TaskWorkerDao taskWorkerDao, UserService userService) {
		this.userDao = userDao;
		this.taskPublisherDao = taskPublisherDao;
		this.taskWorkerDao = taskWorkerDao;
		this.userService = userService;
	}

	@Scheduled(cron = "0 0 0 * * *")	// 每天0点0分进行一次
	public void updateIsAttendant() {
		logger.info("第{}次执行更新签到信息任务", isAttendantCount++);

		List<User> users = userDao.getAllUser();
		for (User user : users) {
			if (user.getIsAttendant()) {
				user.setIsAttendant(false);
				userDao.update(user);
			}
		}
	}

	@Scheduled(cron = "0 0 * * * *")	// 每小时0分进行一次
	public void updateHotRank() {
		logger.info("第{}次执行更新热度任务", hotRankCount++);

		List<TaskPublisher> taskPublishers = taskPublisherDao.getAllTasks();
		if (taskPublishers.isEmpty()) {
			return;
		}
		for (TaskPublisher taskPublisher : taskPublishers) {	// 每次刷新将热度原始计数降低0.5成
			taskPublisher.setHotCount(taskPublisher.getHotCount() * 0.95);
		}
		taskPublishers.sort(Comparator.comparing(TaskPublisher::getHotCount));
		double min = taskPublishers.get(0).getHotCount();
		double max = taskPublishers.get(taskPublishers.size() - 1).getHotCount();
		if (max - min < 0.00001) {	// 若过于接近：全设置为两星半
			for (TaskPublisher taskPublisher : taskPublishers) {
				taskPublisher.setHotRank(2.5D); // (0 + 5) / 2
				taskPublisherDao.update(taskPublisher);
			}
		} else {	// 将热度原始计数投影至[0, 5]的区间得到热度值
			double tmp = 5D / (max - min);  // f(x) = 5 * (x - min) / (max - min)
			for (TaskPublisher taskPublisher : taskPublishers) {
				taskPublisher.setHotRank(tmp * (taskPublisher.getHotCount() - min));
				taskPublisherDao.update(taskPublisher);
			}
		}
	}

	@Scheduled(cron = "0 * * * * *")	// 每分钟0秒进行一次
	public void updateTaskPublisherStateIfExpired() {
		logger.info("第{}次执行检查TaskPublisher是否到期任务", taskPublisherExpiredCount++);

		// 遍历查找所有进行中的，预计结束日期已过的TaskPublisher
		List<TaskPublisher> taskPublishers = taskPublisherDao.fullyQuery("taskState", TaskState.PROCESSING);
		for (TaskPublisher taskPublisher : taskPublishers) {
			LocalDateTime endDate = LocalDateTime.parse(taskPublisher.getEndDate(), dateTimeFormatter);
			if (LocalDateTime.now().isAfter(endDate)) {	// 若已过期：强制结束该TaskPublisher和相关的进行中的TaskWorker
				taskPublisher.setTaskState(TaskState.OVERTIME);
				taskPublisherDao.update(taskPublisher);

				ArrayList<Criterion> criteria = new ArrayList<>();
				criteria.add(new Criterion<>("taskPublisherId", taskPublisher.getId(), QueryMode.FULL));
				criteria.add(new Criterion<>("taskState", TaskState.PROCESSING, QueryMode.FULL));
				for (TaskWorker taskWorker : taskWorkerDao.multiQuery(criteria)) {
					taskWorker.setTaskState(TaskState.OVERTIME);
					taskWorkerDao.updateTaskWorker(taskWorker);
					userService.updatePunctualityRate(taskWorker.getUserId());	// 更新准时率
				}
			}
		}
	}
}