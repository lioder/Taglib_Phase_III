package horizon.taglib.service;

import horizon.taglib.dao.UserDao;
import horizon.taglib.enums.UserType;
import horizon.taglib.model.User;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RankServiceTest {

    @Autowired
    private RankService rankService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    private User worker1;
    private User worker2;
    private User publisher1;
    private User publisher2;
    private long workerId1;
    private long workerId2;
    private long publisherId1;
    private long publisherId2;

    @Before
    public void setUp() throws Exception {
        worker1 = new User("e","980508","18928390485","63724372949@qq.com",UserType.WORKER);
        worker2 = new User("f","980508","183904492029","332463297@qq.com",UserType.WORKER);
        publisher1 = new User("g","980508","1893039284","27389137918@qq.com",UserType.REQUESTOR);
        publisher2 = new User("h","980508","13782920110","382048302@qq.com",UserType.REQUESTOR);
        userService.register(worker1);
        workerId1 = userService.getNewUserId()-1;
        worker1.setPoints(20L);
        worker1.setExp(70L);
        worker1.setAccuracyRate(0.33);
        worker1.setSatisfactionRate(0.89);
        userDao.save(worker1);

        userService.register(worker2);
        workerId2 = userService.getNewUserId()-1;
        worker2.setPoints(14L);
        worker2.setExp(80L);
        worker2.setAccuracyRate(0.12);
        worker2.setSatisfactionRate(0.90);
        userDao.save(worker2);

        userService.register(publisher1);
        publisherId1 = userService.getNewUserId()-1;
        publisher1.setPoints(78L);
        publisher1.setExp(77L);
        userDao.save(publisher1);

        userService.register(publisher2);
        publisherId2 = userService.getNewUserId()-1;
        publisher2.setPoints(58L);
        publisher2.setExp(90L);
        userDao.save(publisher2);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getWorkerPointRank_test1() {
        List<User> userList = rankService.getWorkerPointRank(workerId2).getRankList();
        Assert.assertEquals("18928390485",userList.get(0).getPhoneNumber());
    }

    @Test
    public void getWorkerPointRank_test2() {
        List<User> userList = rankService.getWorkerPointRank(0L).getRankList();
        Assert.assertEquals("183904492029",userList.get(1).getPhoneNumber());
    }

    @Test
    public void getWorkerExpRank_test1() {
        List<User> users = rankService.getWorkerExpRank(workerId2).getRankList();
        Assert.assertEquals("332463297@qq.com",users.get(0).getEmail());
    }

    @Test
    public void getWorkerAccuracyRateRank() {
        List<User> users= rankService.getWorkerAccuracyRateRank(workerId1).getRankList();
        Assert.assertEquals("183904492029",users.get(1).getPhoneNumber());
    }

    @Test
    public void getWorkerSatisfactionRank() {
        List<User> users = rankService.getWorkerSatisfactionRank(workerId2).getRankList();
        Assert.assertEquals("63724372949@qq.com",users.get(1).getEmail());
    }

    @Test
    public void getPublisherPointRank() {
        List<User> users = rankService.getPublisherPointRank(publisherId1).getRankList();
        Assert.assertEquals("1893039284",users.get(0).getPhoneNumber());
    }

    @Test
    public void getPublisherExpRank() {
        List<User> users =  rankService.getPublisherExpRank(publisherId1).getRankList();
        Assert.assertEquals("382048302@qq.com",users.get(0).getEmail());
    }

    @Test
    public void getPublisherTaskRank() {
        List<User> users = rankService.getPublisherTaskRank(publisherId1).getRankList();
        Assert.assertEquals("1893039284",users.get(0).getPhoneNumber());
    }
}