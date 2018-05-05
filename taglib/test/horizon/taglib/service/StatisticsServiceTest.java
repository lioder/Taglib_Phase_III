package horizon.taglib.service;

import horizon.taglib.enums.TaskType;
import horizon.taglib.enums.UserType;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.TaskWorker;
import horizon.taglib.model.User;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StatisticsServiceTest {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    private User publisher;
    private Long publisherId;
    private User worker;
    private long workerId;
    private TaskPublisher taskPublisher;
    private Long taskPublisherId;
    private TaskWorker taskWorker;

    @Before
    public  void setUp() throws Exception {
        //添加发布者和工人
        publisher = new User("zlk","980508","12345678900","293023892@qq.com",UserType.REQUESTOR);
        userService.register(publisher);
        publisherId = userService.getNewUserId()-1;

        worker = new User("a","980508","190329023748","3729281990@qq.com",UserType.WORKER);
        userService.register(worker);
        workerId = userService.getNewUserId()-1;

        //添加发布者任务
        List<String> images = new ArrayList<>();
        images.add("u=454443111,856819310&fm=200&gp=0.jpg");
        List<String> labels = new ArrayList<>();
        labels.add("动物");
        labels.add("动作");
        List<String> topics = new ArrayList<>();
        topics.add("动物");
        taskPublisher = new TaskPublisher(publisherId,"动物","好多鱼",TaskType.BOX,images,labels,topics,500.0,30L,"2018-04-21 18:12","2018-4-30 13:00");
        taskService.addTask(taskPublisher);
        taskPublisherId = taskService.getNewTaskId()-1;
        //添加工人任务
        taskWorker = new TaskWorker(taskPublisherId,workerId,300.0,"2018-04-21 15:12");
        userService.acceptTask(taskWorker);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getTaskWorkersByUserId() {
        List<TaskWorker> taskWorkers = statisticsService.getTaskWorkersByUserId(workerId);
        Assert.assertEquals("2018-04-21 15:12",taskWorkers.get(taskWorkers.size()-1).getStartDate());
    }

    @Test
    public void allParticipateUsersById(){
        List<User> workers = statisticsService.allParticipateUsersById(taskPublisherId);
        Assert.assertEquals(1,workers.size());
    }

    @Test
    public void getAllTaskPublishers() {
        List<TaskPublisher> taskPublishers = statisticsService.getAllTaskPublishers();
        Assert.assertEquals(2,taskPublishers.size());
    }

    @Test
    public void getAllTaskWorkers() {
        List<TaskWorker> taskWorkers = statisticsService.getAllTaskWorkers();
        Assert.assertEquals(3,taskWorkers.size());
    }

}