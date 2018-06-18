package horizon.taglib.service;

import horizon.taglib.dao.TagDao;
import horizon.taglib.dao.TaskPublisherDao;
import horizon.taglib.dao.TaskWorkerDao;
import horizon.taglib.dao.UserDao;
import horizon.taglib.enums.*;
import horizon.taglib.model.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.JVM)
public class TaskServiceTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskPublisherDao taskPublisherDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskWorkerDao taskWorkerDao;

    private User publisher;
    private long publisherId;
    private User worker;
    private long workerId;
    private TaskPublisher taskPublisher;
    private long taskPublisherId;
    private TaskWorker taskWorker;
    private long taskWorkerId;
    private long tagId;

    @Before
    public void setUp() throws Exception {
        //添加发布者和工人
        publisher = new User("zlk","980508","12345678900","293023892@qq.com",UserType.REQUESTOR);
        userDao.save(publisher);
        List<User> users = userDao.findAll();
        publisherId = users.get(users.size()-1).getId();

        worker = new User("a","980508","190329023748","3729281990@qq.com",UserType.WORKER);
        userService.register(worker);
        List<User> users1 = userDao.findAll();
        workerId = users1.get(users1.size()-1).getId();

        //添加TaskPublisher
        List<String> images = new ArrayList<>();
        images.add("u=454443111,856819310&fm=200&gp=0.jpg");
        List<String> labels = new ArrayList<>();
        labels.add("动物");
        labels.add("动作");
        List<String> topics = new ArrayList<>();
        topics.add("动物");
        taskPublisher = new TaskPublisher(publisherId,"动物","好多鱼",TaskType.BOX,images,labels,topics,500.0,30L,"2018-04-21 18:12","2019-4-30 13:00",null);
        taskService.addTask(taskPublisher);
        List<TaskPublisher> taskPublishers = taskPublisherDao.findAll();
        taskPublisherId = taskPublishers.get(taskPublishers.size()-1).getId();

        //添加工人任务
        taskWorker = new TaskWorker(taskPublisherId,workerId,300.0,"2018-04-21 15:12");
        userService.acceptTask(taskWorker);
        List<TaskWorker> taskWorkers = taskWorkerDao.findAll();
        taskWorkerId = taskWorkers.get(taskWorkers.size()-1).getId();
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("Done");
    }


    @Test
    public void getTaskPublisherById() {
        TaskPublisher taskPublisher1 = taskService.getTaskPublisherById(taskPublisherId);
        Assert.assertEquals("动物",taskPublisher1.getTitle());
    }

    @Test
    public void deleteTag() {
        TagDesc tagDesc = new TagDesc();
        Tag tag = new Tag(taskPublisherId,taskWorkerId,"u=454443111,856819310&fm=200&gp=0.jpg",workerId,tagDesc,"#1F0F0F",TagType.RECT);
        tagDao.save(tag);
        List<Tag> tags = tagDao.findAll();
        tagId = tags.get(tags.size()-1).getId();
        ResultMessage acres = taskService.deleteTag(tagId);
        Assert.assertEquals(ResultMessage.SUCCESS,acres);
    }

    @Test
    public void addTask() {
        List<String> images = new ArrayList<>();
        images.add("u=1610360805,2898911473&fm=200&gp=0.jpg");
        List<String> labels = new ArrayList<>();
        labels.add("植物");
        labels.add("多肉");
        List<String> topics = new ArrayList<>();
        topics.add("植物");
        taskPublisher = new TaskPublisher(publisherId,"植物","好多植物",TaskType.BOX,images,labels,topics,500.0,30L,"2018-04-21 18:12","2019-4-30 13:00",null);
        taskPublisherDao.save(taskPublisher);
        ResultMessage acres = ResultMessage.SUCCESS;
        Assert.assertEquals(ResultMessage.SUCCESS,acres);
    }

    @Test
    public void findTaskPublisherByState_test1() {
        taskPublisher.setTaskState(TaskState.REJECT);
        taskPublisherDao.save(taskPublisher);
        List<TaskPublisher> taskPublishers = taskService.findTaskPublisherByState(publisherId,TaskState.REJECT,10,1).getPageData();
        Assert.assertEquals(1,taskPublishers.size());
    }

    @Test
    public void findTaskPublisherByState_test2() {
        taskPublisher.setTaskState(TaskState.GIVE_UP);
        taskPublisherDao.save(taskPublisher);
        List<TaskPublisher> taskPublishers = taskService.findTaskPublisherByState(0,TaskState.GIVE_UP,10,1).getPageData();
        Assert.assertEquals(1,taskPublishers.size());
    }
}