package horizon.taglib.dao.impl;

import horizon.taglib.dao.UserDao;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.UserType;
import horizon.taglib.model.User;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserDaoImplTest {
	@Autowired
	private UserDao userDao;
	private static User user;

	@Test
	public void test_1_add() {
		user = new User("tester", "123456", "13800000000", String.valueOf(this.hashCode()) + "@test.com", UserType.WORKER);
		Assert.assertEquals(ResultMessage.SUCCESS, userDao.save(user));
	}

	@Test
	public void test_2_findById() {
		Assert.assertNotNull(userDao.findOne(user.getId()));
		Assert.assertEquals(user, userDao.findOne(user.getId()));
	}

	@Test
	public void test_3_getAllUser() {
		Assert.assertNotNull(userDao.findAll());
		Assert.assertTrue(userDao.findAll().size() >= 1);
	}

	@Test
	public void test_4_findByEmail() {
		Assert.assertNotNull(userDao.findByEmail(user.getEmail()));
		Assert.assertEquals(user, userDao.findOne(user.getId()));
	}

	@Test
	public void test_5_update() {
		user.setUsername("tested");
		Assert.assertEquals(ResultMessage.SUCCESS, userDao.save(user));
		Assert.assertEquals(user.getUsername(), userDao.findOne(user.getId()).getUsername());
	}

	@Test
	public void test_6_findByPhoneNumber() {
		Assert.assertNotNull(userDao.findByPhoneNumber(user.getPhoneNumber()));
		Assert.assertEquals(user, userDao.findByPhoneNumber(user.getPhoneNumber()));
	}

	@Test
	public void test_7_delete() {
		Assert.assertEquals(ResultMessage.SUCCESS, userDao.delete(user.getId()));
		Assert.assertNull(userDao.findOne(user.getId()));
	}
}