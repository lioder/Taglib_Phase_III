package horizon.taglib.dao.impl;

import horizon.taglib.dao.DaoHelper;
import horizon.taglib.enums.QueryMode;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.utils.Criterion;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JsonDaoHelperTest {
	@Autowired
	private DaoHelper<TaskPublisher> daoHelper;
	private static TaskPublisher taskPublisher;

	@Before
	public void setUp() {
		daoHelper.initClass(TaskPublisher.class);
	}

	@After
	public void tearDown() {
		((JsonDaoHelper<TaskPublisher>) daoHelper).destroy();
	}

	@Test
	public void test_1_add() {
		taskPublisher = new TaskPublisher();
		taskPublisher.setTitle("This is a test");
		taskPublisher.setLabels(new ArrayList<>(Arrays.asList("Test label1", "Test label2")));
		Long before = daoHelper.count();
		Assert.assertEquals(ResultMessage.SUCCESS, daoHelper.add(taskPublisher));
		Assert.assertEquals(1, daoHelper.count() - before);
	}

	@Test
	public void test_2_getNewId() {
		Assert.assertNotNull(daoHelper.getNewId());
		Assert.assertTrue(daoHelper.getNewId() > 1);
	}

	@Test
	public void test_3_exactlyQuery() {
		TaskPublisher found = daoHelper.exactlyQuery("id", taskPublisher.getId());
		Assert.assertNotNull(found);
		Assert.assertEquals(taskPublisher.getId(), found.getId());
	}

	@Test
	public void test_4_fullyQuery() {
		List<TaskPublisher> found = daoHelper.fullyQuery("id", taskPublisher.getId());
		Assert.assertNotNull(found);
		Assert.assertTrue(found.size() == 1);
		Assert.assertTrue(found.contains(taskPublisher));

		found = daoHelper.fullyQuery("labels", taskPublisher.getLabels().get(0));
		Assert.assertNotNull(found);
		Assert.assertTrue(found.size() >= 1);
		Assert.assertTrue(found.contains(taskPublisher));
	}

	@Test
	public void test_4_fuzzyQuery() {
		List<TaskPublisher> found = daoHelper.fuzzyQuery("title", taskPublisher.getTitle().substring(0, 1));
		Assert.assertNotNull(found);
		Assert.assertTrue(found.size() >= 1);
		Assert.assertTrue(found.contains(taskPublisher));

		found = daoHelper.fuzzyQuery("id", String.valueOf(taskPublisher.getId()).substring(0, 1));
		Assert.assertNotNull(found);
		Assert.assertTrue(found.size() >= 1);
		Assert.assertTrue(found.contains(taskPublisher));

		String label = taskPublisher.getLabels().get(0);
		found = daoHelper.fuzzyQuery("labels", label.substring(0, label.length() - 1));
		Assert.assertNotNull(found);
		Assert.assertTrue(found.size() >= 1);
		Assert.assertTrue(found.contains(taskPublisher));
	}

	@Test
	public void test_5_rangeQuery() {
		List<TaskPublisher> found = daoHelper.rangeQuery("id", taskPublisher.getId(), taskPublisher.getId());
		Assert.assertNotNull(found);
		Assert.assertTrue(found.size() == 1);
		Assert.assertTrue(found.contains(taskPublisher));
	}

	@Test
	public void test_6_multiQuery() {
		ArrayList<Criterion> criteria = new ArrayList<>();
		criteria.add(new Criterion<>("id", taskPublisher.getId(), QueryMode.FULL));
		criteria.add(new Criterion<>("id", taskPublisher.getId(), taskPublisher.getId()));
		List<TaskPublisher> found = daoHelper.multiQuery(criteria);
		Assert.assertNotNull(found);
		Assert.assertTrue(found.size() == 1);
		Assert.assertTrue(found.contains(taskPublisher));
	}

	@Test
	public void test_7_delete() {
		Assert.assertEquals(ResultMessage.SUCCESS, daoHelper.delete(taskPublisher.getId()));
		Assert.assertNull(daoHelper.exactlyQuery("id", taskPublisher.getId()));
	}
}