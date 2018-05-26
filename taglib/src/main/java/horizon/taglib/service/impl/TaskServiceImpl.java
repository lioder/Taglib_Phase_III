package horizon.taglib.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import horizon.taglib.dao.TagDao;
import horizon.taglib.dao.TaskPublisherDao;
import horizon.taglib.dao.UserDao;
import horizon.taglib.dto.PageDTO;
import horizon.taglib.enums.QueryMode;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.TaskState;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.User;
import horizon.taglib.service.TaskService;
import horizon.taglib.utils.CenterTag;
import horizon.taglib.utils.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TaskServiceImpl implements TaskService {
	private final static String FILE_SEPARATOR = System.getProperty("file.separator");
	private final static String DIR = "taglib" + FILE_SEPARATOR + "database" + FILE_SEPARATOR + "tag-data";
	private TaskPublisherDao taskPublisherDao;
	private TagDao tagDao;
	private UserDao userDao;
	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	public TaskServiceImpl(TaskPublisherDao taskPublisherDao, TagDao tagDao, UserDao userDao) {
		this.taskPublisherDao = taskPublisherDao;
		this.tagDao = tagDao;
		this.userDao = userDao;
		File dirs = new File(DIR);
		if (!dirs.exists() || !dirs.isDirectory()) {
			boolean isFailed = dirs.mkdirs();
			if (isFailed) {
				System.out.println("Make dirs failed in TaskServiceImpl! ");
			}
		}
	}

	@Override
	public List<TaskPublisher> getAllTasks() {
		return taskPublisherDao.findAll();
	}

	@Override
	public TaskPublisher getTaskPublisherById(long id) {
		return taskPublisherDao.findOne(id);
	}


	@Override
	public ResultMessage deleteTag(long tagId) {
		tagDao.delete(tagDao.findOne(tagId));
		return ResultMessage.SUCCESS;
	}

	@Override
	public List<Object> addTask(TaskPublisher taskPublisher) {
		List<Object> list = new ArrayList<>();
		TaskPublisher taskPublisher1 = taskPublisherDao.save(taskPublisher);
		ResultMessage resultMessage = ResultMessage.FAILED;
		if (taskPublisher1 != null) {
			resultMessage = ResultMessage.SUCCESS;
		}
		list.add(resultMessage);

		User user = userDao.findOne(taskPublisher.getUserId());
		Long id = null;
		if (taskPublisher1 != null) {
			id = taskPublisher1.getId();
		}
		List<Long> list1 = user.getMyTasks();
		list1.add(id);
		user.setMyTasks(list1);
		userDao.save(user);

		if (resultMessage == ResultMessage.SUCCESS) {
			list.add(taskPublisher1.getId());
		}
		return list;
	}

//    @Override
//    public long getNewTaskId(){
//        return taskPublisherDao.getNewId();
//    }

	@Override
	public PageDTO<TaskPublisher> findTaskPublisherByState(long userId, TaskState taskState, Integer size, Integer currentPage) {
		if (userId == 0) {
			ArrayList<Criterion> criteria = new ArrayList<>();
			criteria.add(new Criterion<>("taskState", taskState, QueryMode.FULL));
			List<TaskPublisher> taskPublishers = taskPublisherDao.multiQuery(criteria);
			return getPageDTO(taskPublishers, size, currentPage);

		} else {
			List<TaskPublisher> taskPublishers = taskPublisherDao.findByUserIdAndTaskState(userId, taskState);
			return getPageDTO(taskPublishers, size, currentPage);
		}
	}

	private PageDTO<TaskPublisher> getPageDTO(List<TaskPublisher> taskPublishers, Integer size, Integer currentPage) {
		PageDTO<TaskPublisher> taskPublisherPageDTO = new PageDTO<>(currentPage, size, taskPublishers.size());
		Integer dataIndex = taskPublisherPageDTO.getDataIndex();
		if (currentPage * size < taskPublishers.size()) {
			taskPublisherPageDTO.setPageData(taskPublishers.subList(dataIndex - 1, dataIndex - 1 + size));
		} else {
			taskPublisherPageDTO.setPageData(taskPublishers.subList(dataIndex - 1, taskPublishers.size()));
		}
		return taskPublisherPageDTO;
	}

	public ResultMessage write(Long taskPublisherId, Map<String, List<CenterTag>> toWrite) {
		File file = new File(DIR + FILE_SEPARATOR + taskPublisherId + ".json");
		try (FileWriter fileWriter = new FileWriter(file, false);
		     BufferedWriter writer = new BufferedWriter(fileWriter)) {
			objectMapper.writerFor(new TypeReference<Map<String, List<CenterTag>>>() {
			}).writeValue(writer, toWrite);
			return ResultMessage.SUCCESS;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResultMessage.FAILED;
	}
}
