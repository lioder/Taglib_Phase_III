package horizon.taglib.service;

import horizon.taglib.dao.UserDao;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.TaskState;
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
public class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserDao userDao;

    private long userId;
    private long workerId;
    private long taskPublisherId;
    private User worker1;
    private TaskWorker taskWorker;

    @Before
    public void setUp() throws Exception {
        worker1 = new User("a","980508","190329023748","3729281990@qq.com",UserType.WORKER);
        User worker2 = new User("b","980508","13923328229","4728395439@qq.com",UserType.WORKER);
        User publisher1 = new User("c","980508","14829204857","3289748395@qq.com",UserType.REQUESTOR);
        User publisher2 = new User("d","980508","14893920290","8320098092@qq.com",UserType.REQUESTOR);
        userService.register(worker1);
        workerId = userDao.getNewId()-1;
        worker1.setExp(50L);
        worker1.setPoints(90L);
        worker1.setAccuracyRate(0.56);
        worker1.setSatisfactionRate(0.89);
        userDao.update(worker1);

        userService.register(worker2);
        worker2.setExp(40L);
        worker2.setPoints(33L);
        worker2.setAccuracyRate(0.66);
        worker2.setSatisfactionRate(0.90);
        userDao.update(worker2);

        userService.register(publisher1);
        publisher1.setPoints(30L);
        publisher1.setExp(89L);
        userDao.update(publisher1);

        userService.register(publisher2);
        publisher2.setPoints(29L);
        publisher2.setExp(78L);
        userDao.update(publisher2);

        userId = userService.getNewUserId()-1;
        List<String> images = new ArrayList<>();
        images.add("u=454443111,856819310&fm=200&gp=0.jpg");
        List<String> labels = new ArrayList<>();
        labels.add("动物");
        labels.add("动作");
        List<String> topics = new ArrayList<>();
        topics.add("动物");
        TaskPublisher taskPublisher = new TaskPublisher(userId,"动物","好多长颈鹿",TaskType.BOX,images,labels,topics,200.0,30L,"2018-04-21 18:12","2018-4-30 13:00");
        taskService.addTask(taskPublisher);
        taskPublisherId = taskService.getNewTaskId()-1;

        taskWorker = new TaskWorker(taskPublisherId,workerId,300.0,"2018-04-21 18:11");
        userService.acceptTask(taskWorker);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void showWorkerList_test1() {
        List<User> users = adminService.showWorkerList("","经验值",true,10,1).getPageData();
        Assert.assertEquals("190329023748",users.get(0).getPhoneNumber());
    }

    @Test
    public void showWorkerList_test2(){
        List<User> users = adminService.showWorkerList("","积分",true,10,1).getPageData();
        Assert.assertEquals("3729281990@qq.com",users.get(0).getEmail());
    }

    @Test
    public void showWorkerList_test3(){
        List<User> users = adminService.showWorkerList("","准确度",true,10,1).getPageData();
        Assert.assertEquals("4728395439@qq.com",users.get(0).getEmail());
    }

    @Test
    public void showWorkerList_test4(){
        List<User> users = adminService.showWorkerList("","客户满意度",true,10,1).getPageData();
        Assert.assertEquals("4728395439@qq.com",users.get(0).getEmail());
    }

    @Test
    public void showWorkerList_test5(){
        List<User> users = adminService.showWorkerList("","全部",true,10,1).getPageData();
        Assert.assertEquals("190329023748",users.get(0).getPhoneNumber());
    }

    @Test
    public void showWorkerList_test6(){
        List<User> users = adminService.showWorkerList("","全部",false,10,1).getPageData();
        Assert.assertEquals("4728395439@qq.com",users.get(0).getEmail());
    }

    @Test
    public void showWorkerList_test7(){
        List<User> users = adminService.showWorkerList("990@qq.com","全部",true,10,1).getPageData();
        Assert.assertEquals("190329023748",users.get(0).getPhoneNumber());
    }

    @Test
    public void showPublisherList_test1() {
        List<User> users = adminService.showPublisherList("","积分",true,10,1).getPageData();
        Assert.assertEquals("14829204857",users.get(0).getPhoneNumber());
    }

    @Test
    public void showPublisherList_test2() {
        List<User> users = adminService.showPublisherList("","经验值",true,10,1).getPageData();
        Assert.assertEquals("14829204857",users.get(0).getPhoneNumber());
    }

    @Test
    public void showPublisherList_test3() {
        List<User> users = adminService.showPublisherList("","任务量",true,10,1).getPageData();
        Assert.assertEquals("8320098092@qq.com",users.get(0).getEmail());
    }

    @Test
    public void showPublisherList_test4() {
        List<User> users = adminService.showPublisherList("","全部",true,10,1).getPageData();
        Assert.assertEquals("3289748395@qq.com",users.get(0).getEmail());
    }

    @Test
    public void showPublisherList_test5() {
        List<User> users = adminService.showPublisherList("","全部",false,10,1).getPageData();
        Assert.assertEquals("8320098092@qq.com",users.get(0).getEmail());
    }

    @Test
    public void showPublisherList_test6() {
        List<User> users = adminService.showPublisherList("20290","全部",true,10,1).getPageData();
        Assert.assertEquals("8320098092@qq.com",users.get(0).getEmail());
    }

    @Test
    public void getTaskPublisherById() {
        TaskPublisher taskPublisher1 = adminService.getTaskPublisherById(taskPublisherId);
        Assert.assertEquals("动物",taskPublisher1.getTitle());
    }

    @Test
    public void updateTaskPublisher() {
        TaskPublisher taskPublisher1 = adminService.getTaskPublisherById(taskPublisherId);
        taskPublisher1.setTitle("植物");
        ResultMessage res = adminService.updateTaskPublisher(taskPublisher1);
        Assert.assertEquals(ResultMessage.SUCCESS,res);
    }

    @Test
    public void updateUser() {
        User worker = userDao.findById(workerId);
        worker.setExp(77L);
        ResultMessage res = adminService.updateUser(worker);
        Assert.assertEquals(ResultMessage.SUCCESS,res);
    }

    @Test
    public void updateTaskWorker() {
        taskWorker.setPrice(230.0);
        ResultMessage res = adminService.updateTaskWorker(taskWorker);
        Assert.assertEquals(ResultMessage.SUCCESS,res);
    }

    @Test
    public void getExaminedTasksNumber() {
        taskWorker.setTaskState(TaskState.PASS);
        adminService.updateTaskWorker(taskWorker);
        Assert.assertEquals(1,adminService.getExaminedTasksNumber(workerId));
    }

    @Test
    public void getFinishedTaskWorkersNumber() {
        taskWorker.setTaskState(TaskState.PASS);
        adminService.updateTaskWorker(taskWorker);
        Assert.assertEquals(1,adminService.getFinishedTaskWorkersNumber(taskPublisherId));
    }
}