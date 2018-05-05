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

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RecommendServiceTest {

    private long publisherId;
    private long workerId;
    private TaskWorker taskWorker;
    private long taskPublisherId;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private RecommendService recommendService;

    @Before
    public void setUp() throws Exception {
        User publisher = new User("c","980508","14829204857","3289748395@qq.com",UserType.REQUESTOR);
        userService.register(publisher);
        publisherId = userService.getNewUserId()-1;
        User worker = new User("a","980508","190329023748","3729281990@qq.com",UserType.WORKER);
        userService.register(worker);
        workerId = userService.getNewUserId()-1;

        List<String> images = new ArrayList<>();
        images.add("u=454443111,856819310&fm=200&gp=0.jpg");
        List<String> labels = new ArrayList<>();
        labels.add("动物");
        labels.add("动作");
        List<String> topics = new ArrayList<>();
        topics.add("动物");
        TaskPublisher taskPublisher = new TaskPublisher(publisherId,"动物","好多长颈鹿",TaskType.BOX,images,labels,topics,200.0,30L,"2018-04-21 18:12","2018-4-30 13:00");
        taskService.addTask(taskPublisher);
        taskPublisherId = taskService.getNewTaskId()-1;

        taskWorker = new TaskWorker(taskPublisherId,workerId,300.0,"2018-04-21 18:11");
        userService.acceptTask(taskWorker);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getPersonalizedTasks() {
        List<String> topics = new ArrayList<>();
        topics.add("动物");
        List<TaskPublisher> list = recommendService.getPersonalizedTasks(topics,workerId);
        Assert.assertEquals(0,list.size());
    }
}