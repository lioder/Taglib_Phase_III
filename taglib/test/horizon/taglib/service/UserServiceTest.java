package horizon.taglib.service;

import horizon.taglib.dao.TaskPublisherDao;
import horizon.taglib.dao.TaskWorkerDao;
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
public class UserServiceTest {

    @Autowired
    private TaskPublisherDao taskPublisherDao;

    @Autowired
    private TaskWorkerDao taskWorkerDao;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    private User publisher;
    private long publisherId;
    private User worker;
    private long workerId;
    private TaskPublisher taskPublisher;
    private TaskWorker taskWorker;
    private long taskPublisherId;
    private long taskWorkerId;

    @Before
    public void setUp() throws Exception {
        //添加user
        publisher = new User("zlk","980508","12345678900","293023892@qq.com",UserType.REQUESTOR);
        userService.register(publisher);
        publisherId = userService.getNewUserId()-1;

        worker = new User("a","980508","190329023748","3729281990@qq.com",UserType.WORKER);
        userService.register(worker);
        workerId = userService.getNewUserId()-1;

        //添加TaskPublisher
        List<String> images = new ArrayList<>();
        images.add("u=454443111,856819310&fm=200&gp=0.jpg");
        List<String> labels = new ArrayList<>();
        labels.add("动物");
        labels.add("动作");
        List<String> topics = new ArrayList<>();
        topics.add("动物");
        taskPublisher = new TaskPublisher(publisherId,"动物","好多鱼",TaskType.BOX,images,labels,topics,500.0,30L,"2018-04-21 18:12","2018-4-30 13:00");
        taskService.addTask(taskPublisher);
        taskPublisherId = taskPublisherDao.getNewId()-1;
        //添加TaskWorker
        taskWorker = new TaskWorker(taskPublisherId,workerId,30.0,"2018-04-21 18:12");
        userService.acceptTask(taskWorker);
        taskWorkerId = taskWorkerDao.getNewId()-1;
    }

    @After
    public void tearDown() throws Exception {
        System.out.print("Done");
        }

    @Test
    public void findUserById() {
        User expectUser = userService.findUserById(workerId);
        Assert.assertEquals("19037389204",expectUser.getPhoneNumber());
    }

    @Test
    public void register() {
        User user1 = new User("loko","980508","19037389204","93207540@qq.com",UserType.REQUESTOR);
        ResultMessage acres = userService.register(user1);
        Assert.assertEquals(ResultMessage.SUCCESS,acres);
    }

    @Test
    public void login() {
        ResultMessage acRes = (ResultMessage) userService.login("12345678900","980508").get(0);
        Assert.assertEquals(ResultMessage.SUCCESS,acRes);
    }

    @Test
    public void findTaskWorkerByState_test1() {
        taskWorker.setTaskState(TaskState.REJECT);
        taskWorkerDao.save(taskWorker);
        List<TaskWorker> taskWorkers = userService.findTaskWorkerByState(workerId,TaskState.REJECT,10,1).getPageData();
        Assert.assertEquals(1,taskWorkers.size());
    }

    @Test
    public void findTaskWorkerByState_test2() {
        taskWorker.setTaskState(TaskState.GIVE_UP);
        taskWorkerDao.save(taskWorker);
        List<TaskWorker> taskWorkers = userService.findTaskWorkerByState(0L,TaskState.GIVE_UP,10,1).getPageData();
        Assert.assertEquals(1,taskWorkers.size());
    }

    @Test
    public void submitTask() {
        List<Tag> tags = new ArrayList<>();
        TagDesc tagDesc = new TagDesc();
        Tag tag = new Tag(taskPublisherId,taskWorkerId,"u=454443111,856819310&fm=200&gp=0.jpg",workerId,tagDesc,"#1F0F0F",TagType.RECT);
        tags.add(tag);
        tag.setId(0L);
        ResultMessage acres = userService.submitTask(taskWorker,tags);
        Assert.assertEquals(ResultMessage.SUCCESS,acres);
    }

    @Test
    public void attend() {
        ResultMessage acres = userService.attend(workerId);
        Assert.assertEquals(ResultMessage.SUCCESS,acres);
    }

    @Test
    public void deleteTask() {
        ResultMessage acres = userService.deleteTask(taskWorkerId);
        Assert.assertEquals(ResultMessage.SUCCESS,acres);
    }

    @Test
    public void recharge() {
        ResultMessage acres = userService.recharge(publisherId,300.0);
        Assert.assertEquals(ResultMessage.SUCCESS,acres);
    }

    @Test
    public void findTaskPublisherById() {
        TaskPublisher taskPublisher = userService.findTaskPublisherById(taskPublisherId);
        Assert.assertEquals("2018-04-21 18:12",taskPublisher.getStartDate());
    }

    @Test
    public void getFitTags() {
        List<Tag> tags = new ArrayList<>();
        TagDesc tagDesc = new TagDesc();
        Tag tag = new Tag(taskPublisherId,taskWorkerId,"u=454443111,856819310&fm=200&gp=0.jpg",workerId,tagDesc,"#1F0F0F",TagType.RECT);
        tag.setId(0L);
        tags.add(tag);
        userService.submitTask(taskWorker,tags);
        List<Tag> tags1 = userService.getFitTags(taskWorkerId);
        Assert.assertEquals("#1F0F0F",tags1.get(tags1.size()-1).getColor());
    }

    @Test
    public void searchTask() {
    }

    @Test
    public void findTaskWorkerById() {
        TaskWorker taskWorker = userService.findTaskWorkerById(taskWorkerId);
        Assert.assertEquals("2018-04-21 18:12",taskWorker.getStartDate());
    }

    @Test
    public void acceptTask() {
        userService.acceptTask(taskWorker);
        taskWorkerId = taskWorkerDao.getNewId()-1;
        TaskWorker taskWorker1 = taskWorkerDao.findOne(taskWorkerId);
        Assert.assertEquals(taskWorker1.getUserId(),taskWorker.getUserId());
    }

    @Test
    public void updateRating() {
        ResultMessage acres = userService.updateRating(taskWorkerId,4);
        Assert.assertEquals(ResultMessage.SUCCESS,acres);
    }
}