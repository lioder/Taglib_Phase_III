package horizon.taglib.service.impl;

import horizon.taglib.dao.TagDao;
import horizon.taglib.dao.TaskPublisherDao;
import horizon.taglib.dao.UserDao;
import horizon.taglib.dto.PageDTO;
import horizon.taglib.enums.QueryMode;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.TaskState;
import horizon.taglib.model.Tag;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.User;
import horizon.taglib.service.TaskService;
import horizon.taglib.utils.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService{

    private TaskPublisherDao taskPublisherDao;
    private TagDao tagDao;
    private UserDao userDao;

    @Autowired
    public TaskServiceImpl(TaskPublisherDao taskPublisherDao, TagDao tagDao,UserDao userDao){
        this.taskPublisherDao = taskPublisherDao;
        this.tagDao=tagDao;
        this.userDao = userDao;
    }

    @Override
    public List<TaskPublisher> getAllTasks(){
        return taskPublisherDao.findAll();
    }

    @Override
    public TaskPublisher getTaskPublisherById(long id){
        return taskPublisherDao.findOne(id);
    }

    @Override
    public ResultMessage addTag(Tag tag){
        return tagDao.save(tag);
    }

    @Override
    public ResultMessage updateTag(Tag tag){
        return tagDao.save(tag);
    }

    @Override
    public ResultMessage deleteTag(long tagId){
        return tagDao.delete(tagId);
    }

    @Override
    public List<Object> addTask(TaskPublisher taskPublisher){
        List<Object> list = new ArrayList<>();
        ResultMessage resultMessage = taskPublisherDao.save(taskPublisher);
        list.add(resultMessage);

        User user = userDao.findOne(taskPublisher.getUserId());
        Long id = taskPublisherDao.getNewId();
        List<Long> list1 = user.getMyTasks();
        list1.add(id);
        user.setMyTasks(list1);
        userDao.save(user);

        if(resultMessage == ResultMessage.SUCCESS){
            List<TaskPublisher> taskPublisherList = taskPublisherDao.findAll();
            list.add(taskPublisherDao.getNewId()-1L);
        }
        return list;
    }

    @Override
    public long getNewTaskId(){
        return taskPublisherDao.getNewId();
    }

    @Override
    public PageDTO<TaskPublisher> findTaskPublisherByState(long userId, TaskState taskState, Integer size, Integer currentPage) {
        if (userId == 0) {
            ArrayList<Criterion> criteria = new ArrayList<>();
            criteria.add(new Criterion<TaskState>("taskState",taskState,QueryMode.FULL));
            List<TaskPublisher> taskPublishers = taskPublisherDao.multiQuery(criteria);
            return getPageDTO(taskPublishers,size,currentPage);

        } else {
            List<TaskPublisher> taskPublishers = taskPublisherDao.findByUserIdAndTaskState(userId, taskState);
            return getPageDTO(taskPublishers,size,currentPage);
        }
    }

    private PageDTO<TaskPublisher> getPageDTO(List<TaskPublisher> taskPublishers,Integer size,Integer currentPage){
        PageDTO<TaskPublisher> taskPublisherPageDTO = new PageDTO<>(currentPage, size, taskPublishers.size());
        Integer dataIndex = taskPublisherPageDTO.getDataIndex();
        if (currentPage * size < taskPublishers.size()) {
            taskPublisherPageDTO.setPageData(taskPublishers.subList(dataIndex - 1, dataIndex - 1 + size));
        } else {
            taskPublisherPageDTO.setPageData(taskPublishers.subList(dataIndex - 1, taskPublishers.size()));
        }
        return taskPublisherPageDTO;
    }
}
