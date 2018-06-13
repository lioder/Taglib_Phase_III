package horizon.taglib.service.impl;

import horizon.taglib.dao.*;
import horizon.taglib.dto.PageDTO;
import horizon.taglib.enums.*;
import horizon.taglib.model.*;
import horizon.taglib.service.UserService;
import horizon.taglib.service.valuedata.UserAccuracy;
import horizon.taglib.utils.Criterion;
import horizon.taglib.utils.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private UserDao userDao;
    private TaskWorkerDao taskWorkerDao;
    private TaskPublisherDao taskPublisherDao;
    private TagDao tagDao;
    private ActivityDao activityDao;
    private UserAccuracy userAccuracy;
    private TaskRecordDao taskRecordDao;
    private WorkerResultDao workerResultDao;
	private CenterTagDao centerTagDao;

    @Autowired
    private UserServiceImpl(UserDao userDao , TaskWorkerDao taskWorkerDao , TaskPublisherDao taskPublisherDao, TagDao tagDao, ActivityDao activityDao, UserAccuracy userAccuracy, TaskRecordDao taskRecordDao, WorkerResultDao workerResultDao, CenterTagDao centerTagDao){
        this.userDao = userDao;
        this.taskWorkerDao = taskWorkerDao;
        this.taskPublisherDao = taskPublisherDao;
        this.tagDao = tagDao;
        this.activityDao = activityDao;
        this.userAccuracy = userAccuracy;
        this.taskRecordDao  = taskRecordDao;
	    this.workerResultDao  = workerResultDao;
	    this.centerTagDao  = centerTagDao;
	    if (userDao.findByEmail("wsywlioder@gmail.com") == null && (userDao.findByPhoneNumber("13866666666") == null)) {
		    User admin = new User("wsywLioder", "666666", "13866666666", "wsywLioder@gmail.com", UserType.ADMIN);
		    userDao.save(admin);
	    }

//	    this.createData();  // 造出的数据暂时还有未知问题。。。
    }

	/**
	 * 批发假数据啦<br>
	 * 在别的电脑上使用需要将file路径设为自己的只存有图片的文件夹
	 */
	private void createData() {
	    if (userDao.count() < 1000) {
		    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		    int userCode = 10000000;    // user code
		    int tpCode = 10000000;    // task publisher code
		    String[] topics = {"DotA2", "IT", "动物", "植物", "人类", "美食", "汽车", "文字", "建筑", "衣服", "道路", "表情"};
		    String[] labels = {"DotA2", "人", "猪", "兔子", "键盘", "水杯", "汽车", "汉字", "灯", "衣服", "道路", "笑脸"};
		    File file = new File("D:\\图片\\Dota2\\dota2主题精美超清大图\\DOTA2已加入游戏内的饰品壁纸");
		    List<String> images = new ArrayList<>();
		    for (File image : Objects.requireNonNull(file.listFiles())) {
			    images.add(image.getName());
		    }
		    List<Long> tpIds = new ArrayList<>();
		    for (int n = 0; n < 10; n++) {  //  + 3000 Publisher
			    User user = new User("tester" + userCode, "123456", "138" + userCode,
					    userCode + "@test.com", UserType.REQUESTOR);
			    userCode++;
			    user.setLevel(Level.values()[(int) (Math.random() * 7)]);
			    List<Long> myTasks = new ArrayList<>();
			    Map<String, Double> topic_factor = new HashMap<>();
			    for (int i = (int) (Math.random() * 4) + 1; i > 0; i--) {
				    topic_factor.put(topics[(int) (Math.random() * topics.length)], Math.random());
			    }
			    user.setTopics(topic_factor);
			    user = userDao.save(user);
			    for (int nt = (int) (Math.random() * 9 + 1); nt > 0; nt--) {  // [1, 50) TaskPublisher
				    List<String> tpImages = new ArrayList<>();
				    for (int nti = (int) (Math.random() * 100) + 1; nti > 0; nti--) {  // [1, 101) image
					    String image = images.get((int) (Math.random() * images.size()));
					    if (!tpImages.contains(image)) {
						    tpImages.add(image);
					    }
				    }
				    List<String> tpLabels = new ArrayList<>();
				    for (int i = (int) (Math.random() * 3) + 1; i > 0; i--) {   // [1, 4) labels
					    String label = labels[(int) (Math.random() * labels.length)];
					    if (!tpLabels.contains(label)) {
						    tpLabels.add(label);
					    }
				    }
				    List<String> tpTopics = new ArrayList<>();
				    for (int i = (int) (Math.random() * 3) + 1; i > 0; i--) {   // [1, 4) topics
					    String topic = topics[(int) (Math.random() * topics.length)];
					    if (!tpTopics.contains(topic)) {
						    tpTopics.add(topic);
					    }
				    }
				    // TODO options, replaced by null now
				    TaskPublisher taskPublisher = new TaskPublisher(user.getId(), "Test " + tpCode,
						    "A test: " + tpCode, TaskType.REGION, tpImages, tpLabels, tpTopics,
						    Math.random() * 4000 + 1, (long) (Math.random() * 10 + 1),
						    LocalDateTime.now().format(dateTimeFormatter), LocalDateTime.now().plusMonths(1L).format(dateTimeFormatter), null);
				    tpCode++;
				    taskPublisher.setTaskState(Math.random() > 0.6 ? TaskState.PROCESSING : TaskState.DONE);
				    long tpId = taskPublisherDao.save(taskPublisher).getId();
				    myTasks.add(tpId);
				    tpIds.add(tpId);
			    }
			    user.setMyTasks(myTasks);
			    userDao.save(user);
		    }
		    for (int n = 0; n < 10; n++) {  //  + 7000 Worker
			    User user = new User("tester" + userCode, "123456", "138" + userCode,
					    userCode + "@test.com", UserType.WORKER);
			    userCode++;
			    user.setSatisfactionRate(Math.random());
			    user.setPunctualityRate(Math.random());
			    user.setAccuracyRate(Math.random());
			    user.setExp((long) (Math.random() * 500) + 1);
			    user.setLevel(Level.values()[(int) (Math.random() * 7)]);
			    user.setPoints((long) (Math.random() * 1000));
			    List<Long> myTasks = new ArrayList<>();
			    user = userDao.save(user);
			    for (int nt = (int) (Math.random() * 9 + 1); nt > 0; nt--) {  // [1, 200) TaskWorker
				    TaskPublisher taskPublisher = taskPublisherDao.findOne(tpIds.get((int) (Math.random() * tpIds.size())));
				    TaskWorker taskWorker = new TaskWorker(taskPublisher.getId(), user.getId(),
						    Math.random() * 2000 + 500, LocalDateTime.now().minusDays(2).format(dateTimeFormatter));
				    taskWorker.setTaskState(TaskState.PASS);
				    taskWorker.setEndDate(LocalDateTime.now().format(dateTimeFormatter));
				    long twId = taskWorkerDao.save(taskWorker).getId();
				    List<Long> tags = new ArrayList<>();
				    for (String image : taskPublisher.getImages()) {  // All tag needed by task publisher
					    TagDesc description = new TagSingleDesc();
					    description.setTagDescType(TagDescType.SINGLE);
					    RecTag tag = new RecTag(taskPublisher.getId(), twId, image, user.getId(), description, null,
							    TagType.RECT, new Point(Math.random() * 200, Math.random() * 200),
							    new Point(Math.random() * 200, Math.random() * 200));
					    tag = tagDao.save(tag);
					    tags.add(tag.getId());
				    }
				    taskWorker.setTags(tags);
				    taskWorkerDao.save(taskWorker);
				    myTasks.add(twId);
			    }
			    user.setMyTasks(myTasks);
			    userDao.save(user);
		    }
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
        Integer count = 0;

        //存储Tag
        for(Tag tag : tags){
            if (tag.getId() == 0) {
                Tag added = tagDao.save(tag);
                tagIds.add(added.getId());
                count++;
            } else {
                tagIds.add(tag.getId());
            }
        }

        increaseUserActivity(taskWorker.getUserId(), count);

        //将tagList存入taskWorker
        taskWorker.setTags(tagIds);

        if(taskWorker.getTaskState()==TaskState.SUBMITTED){
            updatePunctualityRate(taskWorker.getUserId());
        }

        TaskWorker taskWorker1 = taskWorkerDao.save(taskWorker);
        if(taskWorker1==null){
            return ResultMessage.NOT_EXIST;
        }else{
            if (taskWorker1.getTaskState() == TaskState.SUBMITTED){
                User user = userDao.findOne(taskWorker1.getUserId());
                Long postTaskLimit = user.getTaskLimit();
                user.setTaskLimit(postTaskLimit+1);
                userDao.save(user);
                int length = taskWorkerDao.findByTaskPublisherIdAndTaskState(taskWorker1.getTaskPublisherId(), TaskState.SUBMITTED).size();
                if (length >= taskPublisherDao.findOne(taskWorker1.getTaskPublisherId()).getNumberPerPicture()){
                    taskPublisherDao.findOne(taskWorker.getTaskPublisherId()).setTaskState(TaskState.DONE);

                    new Thread(() -> userAccuracy.adjustUserAccuracy(taskWorker.getTaskPublisherId())).start();
                }
            }
            return ResultMessage.SUCCESS;
        }
    }

    @Override
    public TaskWorker acceptTask(TaskWorker taskWorker){
        //得到用户
        User user = userDao.findOne(taskWorker.getUserId());
        Long postTaskLimit = user.getTaskLimit();

        if(postTaskLimit>0&&user.getProhibitTime()==0) {
            taskWorker.setTaskState(TaskState.PROCESSING);
//        long taskWorkerId = taskWorkerDao.getNewId();
            TaskWorker taskWorker1 = taskWorkerDao.save(taskWorker);
            long taskWorkerId = taskWorker1.getId();
            //得到用户目前接受任务的情况
            List<Long> list = user.getMyTasks();
            list.add(taskWorkerId);
            user.setMyTasks(list);
            user.setTaskLimit(postTaskLimit - 1);
            //更新用户的接受情况
            userDao.save(user);
            //发起者热度加一
            TaskPublisher taskPublisher = taskPublisherDao.findOne(taskWorker1.getTaskPublisherId());
            taskPublisher.setHotCount(taskPublisher.getHotCount() + 1);
            taskPublisherDao.save(taskPublisher);

            taskWorker1.setId(taskWorkerId);
            taskWorkerDao.save(taskWorker1);

            return taskWorker1;
        }else{
            System.out.println("接受任务失败");
            return null;
        }
    }

    @Override
    public ResultMessage attend(Long userId){
        User user = userDao.findOne(userId);
        if(user.getIsAttendant()){
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
    public PageDTO<TaskPublisher> searchTask(String keywords , String sortBy , Boolean isSec , Integer size , Integer currentPage , Long userId,List<String> topics){
        User user = userDao.findOne(userId);
        //得到该用户所有已接受的taskPublisher的id
        List<Long> taskWorkerList = user.getMyTasks();
        //taskPublisherArrays为该用户接受的所有发起者任务
        List<Long> taskPublisherArrays = new ArrayList<>();
        for(Long taskWorkerId:taskWorkerList){
            taskPublisherArrays.add(taskWorkerDao.findOne(taskWorkerId).getTaskPublisherId());
        }
        //taskPublisherArrays去除重复元素，保存在list中
        Set set = new HashSet(taskPublisherArrays);
        List<Long> list = new ArrayList<>(set);

        //筛选出该用户未接受的发起者任务
        //allTaskPublishers为所有的发起者任务
        List<TaskPublisher> allTaskPublishers = taskPublisherDao.findAll();
        List<TaskPublisher> taskPublishers = new ArrayList<>();
        List<TaskPublisher> taskPublisherList = new ArrayList<>();
        //得到该用户未接受的taskPublisher,list为该用户已接受的发起者任务
        for(TaskPublisher taskPublisher : allTaskPublishers){
            boolean isAccept = false;
            for(Long taskId : list){
                if(taskId.equals(taskPublisher.getId())){
                    isAccept = true;
                }
            }
            if(!isAccept){
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
                      new Criterion<>("title", "%" + keywords + "%", QueryMode.FUZZY),
                      new Criterion<>("description", "%" + keywords + "%", QueryMode.FUZZY)
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
            taskPublisherList.addAll(taskPublishers4);
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

            //对任务话题进行筛选
            List<TaskPublisher> publisherList = new ArrayList<>();
            if(topics!=null){
                if(topics.size()>0){
                    for(String topic:topics){
                        for(TaskPublisher taskPublisher:taskPublisherList){
                            if(taskPublisher.getTopics().contains(topic)&&(!publisherList.contains(taskPublisher))){
                                publisherList.add(taskPublisher);
                            }
                        }
                    }
                }else{
                    publisherList.addAll(taskPublisherList);
                }
            }else{
                publisherList.addAll(taskPublisherList);
            }

            //如果是反序
            if(!isSec){
                Collections.reverse(publisherList);
            }

            PageDTO<TaskPublisher> taskPublisherPageDTO = new PageDTO<>(currentPage,size,publisherList.size());
            Integer dataIndex = taskPublisherPageDTO.getDataIndex();
            if(currentPage*size<publisherList.size()){
                taskPublisherPageDTO.setPageData(publisherList.subList(dataIndex - 1, dataIndex - 1 + size));
            }else{
                taskPublisherPageDTO.setPageData(publisherList.subList(dataIndex-1,publisherList.size()));
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
            if(taskWorker1.getTaskPublisherId().equals(taskPublisherId)){
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

    /**
     * 通过用户id查找时间段内的用户活跃度，若无则返回空表
     *
     * @param userId     用户id
     * @param lowerLimit （含）下限
     * @param upperLimit （含）上限
     * @return 用户活跃度的列表
     */
    @Override
    public List<Activity> findActivities(Long userId, LocalDate lowerLimit, LocalDate upperLimit) {
        return activityDao.findByUserIdAndDateBetween(userId, lowerLimit, upperLimit);
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

	/**
	 * 增加用户当日活跃度
	 *
	 * @param userId 用户id
	 * @param count  待增加量
	 */
	private void increaseUserActivity(Long userId, Integer count) {
		LocalDate now = LocalDate.now();
		List<Activity> activities = activityDao.findByUserIdAndDate(userId, now);
		Activity activity;
		if (activities.isEmpty()) {
			activity = new Activity(userId, now, count);
		} else {
			if (activities.size() > 1) {
				logger.warn("检测到重复的用户活跃度({})，请检查数据库表：userId={}, date={}",
						Activity.class.getSimpleName(), userId, now);
			}
			activity = activities.get(0);
			activity.setCount(activity.getCount() + count);
		}
		activityDao.save(activity);
	}

    @Override
    public TaskRecord findTaskRecordByUserIdAndTaskPublisherId(Long userId, Long taskPublisherId) {
	    TaskRecord taskRecord = taskRecordDao.findTaskRecordByUserIdAndTaskPublisherId(userId, taskPublisherId);
	    if(taskRecord != null){
	        return taskRecord;
        }
        return null;
    }

    @Override
    public TaskRecord findTaskRecordById(Long recordId){
	    TaskRecord taskRecord = taskRecordDao.findOne(recordId);
	    return taskRecord;
    }

    @Override
    public ResultMessage updateTaskRecord(TaskRecord taskRecord){
        taskRecordDao.saveAndFlush(taskRecord);
        return ResultMessage.SUCCESS;
    }

	@Override
	public Map<RecTag, TagResult> getTaskWorkerResult(Long taskWorkerId) {
		Map<RecTag, TagResult> ret = new HashMap<>();
		WorkerResult workerResult = workerResultDao.findOne(taskWorkerId);
		if (workerResult == null) {
			return ret;
		}
		for (Long recTagId : workerResult.getCorrectTagIds()) {
			ret.put((RecTag) tagDao.findOne(recTagId), TagResult.CORRECT);
		}
		for (Long recTagId : workerResult.getWrongTagIds()) {
			ret.put((RecTag) tagDao.findOne(recTagId), TagResult.WRONG);
		}
		for (Long recTagId : workerResult.getMissTagIds()) {
			CenterTag centerTag = centerTagDao.findOne(recTagId);
			RecTag recTag = new RecTag(null, centerTag.getTaskPublisherId(), null,
					centerTag.getFileName(), null, new TagSingleDesc(TagDescType.SINGLE,
					centerTag.getDescription()), null, TagType.RECT, new Point(centerTag.getStart_x(),
					centerTag.getStart_y()), new Point(centerTag.getEnd_x(), centerTag.getEnd_y()));
			ret.put(recTag, TagResult.MISS);
		}
		return ret;
	}
}
