package horizon.taglib.service.impl;

import horizon.taglib.dao.TagDao;
import horizon.taglib.dao.TaskPublisherDao;
import horizon.taglib.dao.TaskWorkerDao;
import horizon.taglib.dao.UserDao;
import horizon.taglib.dto.PageDTO;
import horizon.taglib.enums.*;
import horizon.taglib.model.*;
import horizon.taglib.service.AdminService;
import horizon.taglib.utils.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private TaskPublisherDao taskPublisherDao;
    private UserDao userDao;
    private TaskWorkerDao taskWorkerDao;
    private TagDao tagDao;

    @Autowired
    public AdminServiceImpl(TaskPublisherDao taskPublisherDao,UserDao userDao,TaskWorkerDao taskWorkerDao,TagDao tagDao){
        this.taskPublisherDao = taskPublisherDao;
        this.userDao = userDao;
        this.taskWorkerDao = taskWorkerDao;
        this.tagDao = tagDao;
    }

    @Override
    public PageDTO showWorkerList(String keywords , String sortBy , Boolean isSec , Integer size , Integer currentPage ){
        List<User> users = userDao.findAll();
        List<User> workers = new ArrayList<>();
        for(User user:users){
            if(user.getUserType()== UserType.WORKER){
                workers.add(user);
            }
        }

        List<User> res = searchByKeywords(keywords,workers);

        switch (sortBy){
            case "经验值":
                Sort.SortByExp sortByExp = new Sort.SortByExp();
                Collections.sort(res,sortByExp);
                break;
            case "积分":
                Sort.SortByPoints sortByPoints = new Sort.SortByPoints();
                Collections.sort(res,sortByPoints);
                break;
            case "准确度":
                Sort.SortByAccuracyRate sortByAccuracyRate = new Sort.SortByAccuracyRate();
                Collections.sort(res,sortByAccuracyRate);
                break;
            case "客户满意度":
                Sort.SortByWorkerSatisfaction sortByWorkerSatisfaction = new Sort.SortByWorkerSatisfaction();
                Collections.sort(res,sortByWorkerSatisfaction);
                break;
            case "全部":
                break;
            default:
                break;
        }

        if(isSec==false){
            Collections.reverse(res);
        }

        PageDTO<User> pageDTO = new PageDTO<>(currentPage,size, res.size());
        Integer dataIndex = pageDTO.getDataIndex();
        if(currentPage*size<res.size()){
            pageDTO.setPageData(res.subList(dataIndex - 1, dataIndex - 1 + size));
        }else{
            pageDTO.setPageData(res.subList(dataIndex-1,res.size()));
        }
        return pageDTO;
    }

    @Override
    public PageDTO showPublisherList(String keywords , String sortBy , Boolean isSec , Integer size , Integer currentPage ){
        List<User> users = userDao.findAll();
        List<User> publishers = new ArrayList<>();
        for(User user:users){
            if(user.getUserType()==UserType.REQUESTOR){
                publishers.add(user);
            }
        }

        List<User> res = searchByKeywords(keywords,publishers);

        switch (sortBy){
            case "积分":
                Sort.SortByPoints sortByPoints = new Sort.SortByPoints();
                Collections.sort(res,sortByPoints);
                break;
            case "经验值":
                Sort.SortByExp sortByExp = new Sort.SortByExp();
                Collections.sort(res,sortByExp);
                break;
            case "任务量":
                Sort.SortByTaskNum sortByTaskNum = new Sort.SortByTaskNum();
                Collections.sort(res,sortByTaskNum);
                break;
            case "全部":
                break;
            default:
                break;
        }

        if(isSec==false){
            Collections.reverse(res);
        }

        PageDTO<User> pageDTO = new PageDTO<>(currentPage,size,res.size());
        Integer dataIndex = pageDTO.getDataIndex();
        if(currentPage*size<res.size()){
            pageDTO.setPageData(res.subList(dataIndex - 1, dataIndex - 1 + size));
        }else{
            pageDTO.setPageData(res.subList(dataIndex-1,res.size()));
        }
        return pageDTO;
    }

    @Override
    public TaskPublisher getTaskPublisherById(long taskId){
        return taskPublisherDao.findOne(taskId);
    }

    @Override
    public ResultMessage updateTaskPublisher(TaskPublisher taskPublisher){
        TaskPublisher taskPublisher1 = taskPublisherDao.save(taskPublisher);
        if(taskPublisher1==null){
            return ResultMessage.NOT_EXIST;
        }else {
            return ResultMessage.SUCCESS;
        }
    }

    @Override
    public ResultMessage updateUser(User user) {
        User user1 = userDao.save(user);
        if(user1==null){
            return ResultMessage.NOT_EXIST;
        }else {
            return ResultMessage.SUCCESS;
        }
    }

    @Override
    public ResultMessage updateTaskWorker(TaskWorker taskWorker) {
        TaskWorker taskWorker1 = taskWorkerDao.save(taskWorker);
        if(taskWorker1==null){
            return ResultMessage.NOT_EXIST;
        }else {
            return ResultMessage.SUCCESS;
        }
    }

    @Override
    public long getExaminedTasksNumber(long userId){
        List<Long> userTaskIds = userDao.findOne(userId).getMyTasks();
        long count = 0;
        for(long taskId:userTaskIds){
            TaskState taskState = taskWorkerDao.findOne(taskId).getTaskState();
            if(taskState==TaskState.PASS||taskState==TaskState.REJECT){
                count++;
            }
        }
        return count;
    }

    @Override
    public long getFinishedTaskWorkersNumber(long taskPublisherId){
        List<TaskWorker> taskWorkers = taskWorkerDao.findAll();
        long count = 0;
        for(TaskWorker taskWorker:taskWorkers){
            if(taskWorker.getTaskPublisherId()==taskPublisherId&&taskWorker.getTaskState()==TaskState.PASS){
                count++;
            }
        }
        return count;
    }

    private List<User> searchByKeywords(String keywords,List<User> publishers){
        List<User> res = new ArrayList<>();
        if(!keywords.equals("")){
            ArrayList<Criterion> criteria = new ArrayList<>();
            criteria.add(
                    new Criterion(
                            new Criterion<>("username","%" + keywords + "%",QueryMode.FUZZY),
                            new Criterion(
                                    new Criterion<>("phoneNumber","%" + keywords + "%",QueryMode.FUZZY),
                                    new Criterion<>("email","%" + keywords + "%",QueryMode.FUZZY)
                            )
                    )
            );
            List<User> users1 = userDao.multiQuery(criteria);
            for(User user:users1){
                for(User user1:publishers){
                    if(user.getId()==user1.getId()){
                        res.add(user1);
                    }
                }
            }
        }else{
            for(User user:publishers){
                res.add(user);
            }
        }
        return res;
    }

    @Override
    public List<RecTag> getRecTagsByTaskPublisherId(long taskpublisherId){
        List<Tag> tags = tagDao.findByTaskPublisherIdAndTagType(taskpublisherId,TagType.RECT);
        List<RecTag> recTags = new ArrayList<>();
        for(Tag tag:tags){
            recTags.add((RecTag) tag);
        }
        return recTags;
    }
}
