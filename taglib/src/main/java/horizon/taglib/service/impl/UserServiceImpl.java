package horizon.taglib.service.impl;

import horizon.taglib.dao.*;
import horizon.taglib.dto.PageDTO;
import horizon.taglib.enums.*;
import horizon.taglib.model.Tag;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.TaskWorker;
import horizon.taglib.model.User;
import horizon.taglib.service.RecommendService;
import horizon.taglib.service.UserService;
import horizon.taglib.utils.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 用户后端接口实现
 * <br>
 * created on 2018/03/15
 *
 * @author 巽
 **/
@Service
public class UserServiceImpl implements UserService{
    private UserDao userDao;
    private TaskWorkerDao taskWorkerDao;
    private TaskPublisherDao taskPublisherDao;
    private TagDao tagDao;

    @Autowired
    private UserServiceImpl(UserDao userDao , TaskWorkerDao taskWorkerDao , TaskPublisherDao taskPublisherDao, TagDao tagDao){
        this.userDao = userDao;
        this.taskWorkerDao = taskWorkerDao;
        this.taskPublisherDao = taskPublisherDao;
        this.tagDao = tagDao;
	    if (userDao.findByEmail("wsywlioder@gmail.com") == null && (userDao.findByPhoneNumber("13866666666") == null)) {
		    User admin = new User("wsywLioder", "666666", "13866666666", "wsywLioder@gmail.com", UserType.ADMIN);
		    userDao.save(admin);
	    }
    }

    @Override
    public User findUserById( long id){
        return userDao.findOne(id);
    }

    @Override
    public ResultMessage register(User user){
        if(userDao.findByPhoneNumber(user.getPhoneNumber())!=null){
            return ResultMessage.PHONE_EXIST;
        }else if(userDao.findByEmail(user.getEmail())!=null){
            return ResultMessage.EAMIL_EXIST;
        }else{
            User user1 = userDao.save(user);
            if(user1==null){
                return ResultMessage.FAILED;
            }else{

                return ResultMessage.SUCCESS;
            }
        }
    }

    @Override
    public List<Object> login(String name, String password){
        List<Object> result=new ArrayList<>();
        if(userDao.findByEmail(name)==null&&userDao.findByPhoneNumber(name)==null){
            result.add(ResultMessage.NOT_EXIST);
        }else{
            User userbyPhone = userDao.findByPhoneNumber(name);
            User userbyEmail = userDao.findByEmail(name);
            if(userbyPhone != null){
                if(userbyPhone.getPassword().equals(password)){
                    result.add(ResultMessage.SUCCESS);
                    result.add(userbyPhone);
                }else{
                    result.add(ResultMessage.FAILED);
                }
            }else{
                if(userbyEmail.getPassword().equals(password)){
                    result.add(ResultMessage.SUCCESS);
                    result.add(userbyEmail);
                }else{
                    result.add(ResultMessage.FAILED);
                }
            }
        }
        return result;
    }

    @Override
    public PageDTO<TaskWorker> findTaskWorkerByState(Long userId, TaskState taskState, Integer size, Integer currentPage){
        if(userId==0){
            List<TaskWorker> taskWorkers = taskWorkerDao.findByTaskState(taskState);
            return getPageDTO(taskWorkers,size,currentPage);
        }else {
            List<TaskWorker> taskWorkers = taskWorkerDao.findByUserIdAndTaskState(userId,taskState);
            return getPageDTO(taskWorkers, size, currentPage);
        }
    }

    @Override
    public ResultMessage submitTask(TaskWorker taskWorker,List<Tag> tags){
        //视提交时间为完成时间
        Date date = new Date();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        taskWorker.setEndDate(sd.format(date));

        List<Long> tagIds = new ArrayList<>();

        //存储Tag
        for(Tag tag : tags){
            if (tag.getId() == 0) {
                Tag added = tagDao.save(tag);
                tagIds.add(added.getId());
            } else {
                tagIds.add(tag.getId());
            }
        }

        //将tagList存入taskWorker
        taskWorker.setTags((ArrayList<Long>) tagIds);

        if(taskWorker.getTaskState()==TaskState.SUBMITTED){
            updatePunctualityRate(taskWorker.getUserId());
        }

        TaskWorker taskWorker1 = taskWorkerDao.save(taskWorker);
        if(taskWorker1==null){
            return ResultMessage.NOT_EXIST;
        }else{
            return ResultMessage.SUCCESS;
        }
    }

    @Override
    public TaskWorker acceptTask(TaskWorker taskWorker){
        taskWorker.setTaskState(TaskState.PROCESSING);
//        long taskWorkerId = taskWorkerDao.getNewId();
        TaskWorker taskWorker1 = taskWorkerDao.save(taskWorker);
        long taskWorkerId = taskWorker1.getId();
        //得到用户
        User user = userDao.findOne(taskWorker1.getUserId());
        //得到用户目前接受任务的情况
        List<Long> list = user.getMyTasks();
        list.add(taskWorkerId);
        user.setMyTasks(list);
        //更新用户的接受情况
        userDao.save(user);
        //发起者热度加一
        TaskPublisher taskPublisher = taskPublisherDao.findOne(taskWorker1.getTaskPublisherId());
        taskPublisher.setHotCount(taskPublisher.getHotCount()+1);
        taskPublisherDao.save(taskPublisher);

        taskWorker1.setId(taskWorkerId);
        taskWorkerDao.save(taskWorker1);

        return taskWorker1;
    }

    @Override
    public ResultMessage attend(Long userId){
        User user = userDao.findOne(userId);
        if(user.getIsAttendant()==true){
            return ResultMessage.ALREADY_ATTENDANT;
        }else{
            user.setIsAttendant(true);
            //初设定user的经验值和积分都加一
            user.setPoints(user.getPoints()+1);
            user.setExp(user.getExp()+1);

            User user1 = userDao.save(user);
            if(user1==null){
                return ResultMessage.NOT_EXIST;
            }else{
                return ResultMessage.SUCCESS;
            }
        }
    }

    @Override
    public ResultMessage deleteTask(Long taskWorkerId){
        taskWorkerDao.delete(taskWorkerDao.findOne(taskWorkerId));
        return ResultMessage.SUCCESS;
    }

    @Override
    public ResultMessage recharge(Long userId, double amount){
        User user = userDao.findOne(userId);
        BigDecimal bigDecimal = new BigDecimal(amount);
        double a = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        user.setPoints(user.getPoints() + (long)a*10);
        User user1 = userDao.save(user);
        if(user1==null){
            return ResultMessage.NOT_EXIST;
        }else{
            return ResultMessage.SUCCESS;
        }
    }

    @Override
    public TaskPublisher findTaskPublisherById(long id){
        return taskPublisherDao.findOne(id);
    }

    @Override
    public List<Tag> getFitTags(long taskWorkerId){
        List<Long> tagIds = taskWorkerDao.findOne(taskWorkerId).getTags();
        List<Tag> tags = new ArrayList<>();
        for(Long tagId:tagIds){
            tags.add(tagDao.findOne(tagId));
        }
        return tags;
    }

    @Override
    public PageDTO<TaskPublisher> searchTask(String keywords , String sortBy , Boolean isSec , Integer size , Integer currentPage , Long userId){
        User user = userDao.findOne(userId);
        //得到该用户所有已接受的taskPublisher的id
        List<Long> taskWorkerList = user.getMyTasks();
        //taskPublisherArrays为该用户接受的所有发起者任务
        List<Long> taskPublisherArrays = new ArrayList<>();
        for(Long taskWorkerId:taskWorkerList){
            taskPublisherArrays.add(taskWorkerDao.findOne(taskWorkerId).getTaskPublisherId());
        }
        //taskPublisherArrsys去除重复元素，保存在list中
        Set set = new HashSet();
        List<Long> list = new ArrayList<>();
        set.addAll(taskPublisherArrays);
        list.addAll(set);

        //筛选出该用户未接受的发起者任务
        //allTaskPublishers为所有的发起者任务
        List<TaskPublisher> allTaskPublishers = taskPublisherDao.findAll();
        List<TaskPublisher> taskPublishers = new ArrayList<>();
        List<TaskPublisher> taskPublisherList = new ArrayList<>();
        //得到该用户未接受的taskPublisher,list为该用户已接受的发起者任务
        for(TaskPublisher taskPublisher : allTaskPublishers){
            boolean isAccept = false;
            for(Long taskId : list){
                if(taskId==taskPublisher.getId()){
                    isAccept = true;
                }
            }
            if(isAccept==false){
                taskPublishers.add(taskPublisher);
            }
        }

        //筛选出接受人数不超过任务接受人数上限的发起者任务
        //得到所有的用户
        List<User> workers = getWorkers();
        List<TaskPublisher> taskPublishers1 = new ArrayList<>();
        //超过任务接受上限的发起者任务移走
        for(TaskPublisher taskPublisher:taskPublishers) {
            long count = 0;
            for (User worker : workers) {
                List<Long> taskWorkerIds = worker.getMyTasks();
                for(Long taskWorkerId:taskWorkerIds){
                    TaskWorker taskWorker = taskWorkerDao.findOne(taskWorkerId);
                    TaskState taskState = taskWorker.getTaskState();
                    if(taskWorker.getTaskPublisherId()==taskPublisher.getId()&&taskState!=TaskState.REJECT){
                        count++;
                    }
                }
            }
            if(count < taskPublisher.getNumberPerPicture()){
                taskPublishers1.add(taskPublisher);
            }
        }

        //筛选出符合条件的任务，当前时间在结束时间之后（任务已经结束）移除
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String present = sdf.format(new Date());
        List<TaskPublisher> taskPublishers2 = new ArrayList<>();
        for(TaskPublisher taskPublisher:taskPublishers1){
            if(present.compareTo(taskPublisher.getEndDate())<0){
                taskPublishers2.add(taskPublisher);
            }
        }

        //筛选出进行中的任务
        List<TaskPublisher> taskPublishers4 = new ArrayList<>();
        for(TaskPublisher taskPublisher:taskPublishers2){
            if(taskPublisher.getTaskState()==TaskState.PROCESSING){
                taskPublishers4.add(taskPublisher);
            }
        }

            //keywords不为空串时,根据keywords搜索出符合要求的taskPublisher
        if(!keywords.equals("")){
            ArrayList<Criterion> criteria = new ArrayList<>();
            criteria.add(
              new Criterion(
                      new Criterion<String>("title",keywords,QueryMode.FUZZY),
                      new Criterion<String>("description",keywords,QueryMode.FUZZY)
                      )
            );
            List<TaskPublisher> taskPublishers3 = taskPublisherDao.multiQuery(criteria);
            for(TaskPublisher taskPublisher : taskPublishers4){
                for(TaskPublisher taskPublisher1 : taskPublishers3){
                    if(taskPublisher.getId() == taskPublisher1.getId()){
                        taskPublisherList.add(taskPublisher);
                    }
                }
            }
        }else{
            for(TaskPublisher taskPublisher:taskPublishers4){
                taskPublisherList.add(taskPublisher);
            }
        }

            switch (sortBy){
                case "时间":
                    //按最新发布任务排序
                    SortByTime sortByTime = new SortByTime();
                    Collections.sort(taskPublisherList,sortByTime);
                    break;
                case "好评率":
                    //按好评率高优先排序
                    SortByRating sortByHotRank = new SortByRating();
                    Collections.sort(taskPublisherList,sortByHotRank);
                    break;
                case "奖励":
                    //按奖励多优先排序
                    SortByReword sortByReword = new SortByReword();
                    Collections.sort(taskPublisherList,sortByReword);
                    break;
                case "任务量":
                    //按任务量多优先排序
                    SortByAssignments sortByAssignments = new SortByAssignments();
                    Collections.sort(taskPublisherList,sortByAssignments);
                    break;
                case "全部":
                    break;
                default:
                    break;
            }
            //如果是反序
            if(isSec == false){
                Collections.reverse(taskPublisherList);
            }

            PageDTO<TaskPublisher> taskPublisherPageDTO = new PageDTO<>(currentPage,size,taskPublisherList.size());
            Integer dataIndex = taskPublisherPageDTO.getDataIndex();
            if(currentPage*size<taskPublisherList.size()){
                taskPublisherPageDTO.setPageData(taskPublisherList.subList(dataIndex - 1, dataIndex - 1 + size));
            }else{
                taskPublisherPageDTO.setPageData(taskPublisherList.subList(dataIndex-1,taskPublisherList.size()));
            }

            return taskPublisherPageDTO;
    }

    @Override
    public TaskWorker findTaskWorkerById(long id){
        return taskWorkerDao.findOne(id);
    }

    @Override
    public ResultMessage updateRating(long taskWorkerId,Integer rating){
        TaskWorker taskWorker = taskWorkerDao.findOne(taskWorkerId);
        Long taskPublisherId = taskWorker.getTaskPublisherId();
        TaskPublisher taskPublisher = taskPublisherDao.findOne(taskPublisherId);
        List<TaskWorker> allTaskWorkers = taskWorkerDao.findByTaskState(TaskState.PASS);
        Integer count = 0;
        for(TaskWorker taskWorker1 : allTaskWorkers){
            if(taskWorker1.getTaskPublisherId()==taskPublisherId){
                count++;
            }
        }
        double rate = (taskPublisher.getRating()*(count-1)+rating)/count;
        taskPublisher.setRating(rate);
        TaskPublisher taskPublisher1 = taskPublisherDao.save(taskPublisher);
        if(taskPublisher1==null){
            return ResultMessage.NOT_EXIST;
        }else{
            return ResultMessage.SUCCESS;
        }
    }


//    @Override
//    public Long getTaskWorkerId(){
//        return taskWorkerDao.getNewId();
//    }

    @Override
    public ResultMessage updatePunctualityRate(long userId){
        User user = userDao.findOne(userId);
        List<Long> myTaskWorkers = user.getMyTasks();
        long allCount = 0;
        long overTimeCount = 0;
        for(Long taskWorkerId:myTaskWorkers){
            TaskState taskState = taskWorkerDao.findOne(taskWorkerId).getTaskState();
            if(taskState==TaskState.PASS||taskState==TaskState.REJECT||taskState==TaskState.PROCESSING||taskState==TaskState.OVERTIME){
                allCount++;
            }
            if(taskState==TaskState.OVERTIME){
                overTimeCount++;
            }
        }
        double punctuality = (double)overTimeCount/(double)allCount;
        user.setPunctualityRate(punctuality);
        User user1 = userDao.save(user);
        if(user1==null){
            return ResultMessage.NOT_EXIST;
        }else{
            return ResultMessage.SUCCESS;
        }

    }

    @Override
    public ResultMessage updateAvatar(long userId,String avatar){
        User user = userDao.findOne(userId);
        user.setAvatar(avatar);
        User user1 = userDao.save(user);
        if(user1==null){
            return ResultMessage.NOT_EXIST;
        }else{
            return ResultMessage.SUCCESS;
        }
    }

    class SortByTime implements Comparator{
        public int compare(Object o1,Object o2){
            TaskPublisher t1 = (TaskPublisher)o1;
            TaskPublisher t2 = (TaskPublisher)o2;
            return t2.getStartDate().compareTo(t1.getStartDate());
        }
    }

    class SortByRating implements Comparator{
        public int compare(Object o1,Object o2){
            TaskPublisher t1 = (TaskPublisher)o1;
            TaskPublisher t2 = (TaskPublisher)o2;
            return t2.getRating().compareTo(t1.getRating());
        }
    }

    class SortByReword implements Comparator{
        public int compare(Object o1,Object o2){
            TaskPublisher t1 = (TaskPublisher)o1;
            TaskPublisher t2 = (TaskPublisher)o2;
            Double price1 = t1.getPrice() / t1.getNumberPerPicture();
            Double price2 = t2.getPrice() / t2.getNumberPerPicture();
            return price1.compareTo(price2);
        }
    }

    class SortByAssignments implements Comparator{
        public int compare(Object o1,Object o2){
            TaskPublisher t1 = (TaskPublisher)o1;
            TaskPublisher t2 = (TaskPublisher)o2;
            Integer a1 = t1.getImages().size();
            Integer a2 = t2.getImages().size();
            return a2.compareTo(a1);
        }
    }

    private PageDTO<TaskWorker> getPageDTO(List<TaskWorker> taskWorkers,Integer size,Integer currentPage){
        //创建一个PageBean对象
        PageDTO<TaskWorker> pageDTO = new PageDTO<>(currentPage,size,taskWorkers.size());
        //得到当前页数数据的第一条目数
        Integer dataIndex = pageDTO.getDataIndex();
        //设置当前页数据
        if(currentPage*size<taskWorkers.size()){
            pageDTO.setPageData(taskWorkers.subList(dataIndex - 1, dataIndex - 1 + size));
        }else{
            pageDTO.setPageData(taskWorkers.subList(dataIndex-1,taskWorkers.size()));
        }
        return pageDTO;
    }

    private List<User> getWorkers(){
        List<User> allUsers = userDao.findAll();
        List<User> res = new ArrayList<>();
        for(User user:allUsers){
            if(user.getUserType()==UserType.WORKER){
                res.add(user);
            }
        }
        return res;
    }

}
