package horizon.taglib.dao.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import horizon.taglib.dao.DaoHelper;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.PO;
import horizon.taglib.utils.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Dao助手类，负责所有Dao的共同操作
 * <br>
 * created on 2018/03/15
 *
 * @author 巽
 **/
@Component
@Scope("prototype")
public class JsonDaoHelper<T extends PO> implements DaoHelper<T> {
	private static String fileSeparator = System.getProperty("file.separator");
	private String pathname;
	private ObjectMapper objectMapper = new ObjectMapper();
	private IdRecorder idRecorder;
	private Map<Long, T> cache;
	private Class<T> clazz;

	@Autowired
	public JsonDaoHelper(IdRecorder idRecorder) {
		this.idRecorder = idRecorder;
		this.pathname = "." + fileSeparator + "taglib" + fileSeparator + "database";
		objectMapper.registerModule(new JavaTimeModule());  // 使Jackson支持对LocalDateTime的解析
		File directory = new File(pathname);
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				System.out.println("Make directory failed: " + directory.getPath());
			}
		}
	}

	@Override
	public void initClass(Class<T> clazz) {
		this.pathname += fileSeparator + clazz.getSimpleName() + ".json";
		this.clazz = clazz;
		this.load();

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				save();
			}
		}, 2000, 1000);
	}

	private void load() {
		File file = new File(pathname);
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					System.out.println("Make directory failed: " + file.getPath());
				}
				cache = new HashMap<>();
				this.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try (FileReader fileReader = new FileReader(file);
			     BufferedReader reader = new BufferedReader(fileReader)) {
				cache = objectMapper.readValue(reader, new TypeReference<Map<Long, T>>() {
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void save() {
		if (cache != null) {
			File file = new File(pathname);
			try (FileWriter fileWriter = new FileWriter(file, false);
			     BufferedWriter writer = new BufferedWriter(fileWriter)) {
				objectMapper.writerFor(new TypeReference<Map<Long, T>>() {
				}).writeValue(writer, cache);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 添加对象，在此处为持久化对象添加id
	 *
	 * @param po 待添加对象
	 * @return SUCCESS：添加成功
	 */
	@Override
	public ResultMessage add(T po) {
		po.setId(idRecorder.getNewIdAndIncrease(po.getClass().getName()));
		cache.put(po.getId(), po);
		return ResultMessage.SUCCESS;
	}

	/**
	 * 删除对象
	 *
	 * @param id 待删除对象的id
	 * @return SUCCESS：删除成功<br>
	 * NOT_EXIST：对象不存在
	 */
	@Override
	public ResultMessage delete(Long id) {
		if (cache.containsKey(id)) {
			cache.remove(id);
			return ResultMessage.SUCCESS;
		}
		return ResultMessage.NOT_EXIST;
	}

	/**
	 * 更新对象
	 *
	 * @param po 待更新对象
	 * @return SUCCESS：更新成功<br>
	 * NOT_EXIST：对象不存在
	 */
	@Override
	public ResultMessage update(T po) {
		if (cache.containsKey(po.getId())) {
			cache.replace(po.getId(), po);
			return ResultMessage.SUCCESS;
		}
		return ResultMessage.NOT_EXIST;
	}

	/**
	 * 得到待分配的新id
	 *
	 * @return 待分配的新id
	 */
	@Override
	public Long getNewId() {
		return idRecorder.getNewId(clazz.getName());
	}

	/**
	 * 精确查询，多用于通过ID查询，默认结果数<=1
	 *
	 * @param field 要查询的字段
	 * @param value 要匹配的值
	 * @return 查询到的对象
	 */
	@Override
	public T exactlyQuery(String field, Object value) {
		try {
			Field variable = this.getField(field);
			variable.setAccessible(true);
			for (T t : cache.values()) {
				if (variable.get(t).equals(value)) {
					return t;
				}
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 完全匹配，多用于查询字段为某个值的所有对象
	 *
	 * @param field 要查询的字段，字段类型为集合类型(extends Collection)时调用Collection.contains(value)进行匹配
	 * @param value 要匹配的值
	 * @return 查询到的所有对象的集合
	 */
	@Override
	public List<T> fullyQuery(String field, Object value) {
		ArrayList<T> ret = new ArrayList<>();
		try {
			Field variable = this.getField(field);
			variable.setAccessible(true);
			if (Collection.class.isAssignableFrom(variable.getType())) {
				for (T t : cache.values()) {
					Collection list = (Collection) variable.get(t);
					if (list.contains(value)) {
						ret.add(t);
					}
				}
			} else {
				for (T t : cache.values()) {
					if (variable.get(t).equals(value)) {
						ret.add(t);
					}
				}
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 模糊查询
	 *
	 * @param field 要查询的字段，支持String、原始数据包裹类型(extends Number)和以String为泛型的集合类型(extends Collection&lt;String&gt;)字段
	 * @param value 要匹配的关键字
	 * @return 查询到的对象的集合
	 */
	@Override
	public List<T> fuzzyQuery(String field, String value) {
		ArrayList<T> ret = new ArrayList<>();
		try {
			Field variable = this.getField(field);
			variable.setAccessible(true);
			if (String.class.isAssignableFrom(variable.getType())) {
				for (T t : cache.values()) {
					if (((String) variable.get(t)).contains(value)) {
						ret.add(t);
					}
				}
			} else if (Number.class.isAssignableFrom(variable.getType())) {
				for (T t : cache.values()) {
					if (String.valueOf(variable.get(t)).contains(value)) {
						ret.add(t);
					}
				}
			} else if (Collection.class.isAssignableFrom(variable.getType())) {
				for (T t : cache.values()) {
					Collection list = (Collection) variable.get(t);
					for (Object str : list) {
						if (((String) str).contains(value)) {
							ret.add(t);
							break;
						}
					}
				}
			} else {
				System.out.println("ERROR：调用JsonDaoHelper.fuzzyQuery()时传进不支持的类型的字段名：" + field);
			}
			return ret;
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 范围查询
	 *
	 * @param field 要查询的字段
	 * @param min   要匹配的值的下限（若无则为null）
	 * @param max   要匹配的值的上限（若无则为null）
	 * @param <E>   字段类型，用以保证上下限参数类型相同
	 * @return 查询到的对象的集合
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <E> List<T> rangeQuery(String field, Comparable<E> min, Comparable<E> max) {
		ArrayList<T> ret = new ArrayList<>();
		try {
			Field variable = this.getField(field);
			if (!Comparable.class.isAssignableFrom(variable.getType())) {
				System.out.println("ERROR：调用JsonDaoHelper.rangeQuery()时传入未实现Comparable的字段类型：" + field);
				return ret;
			}
			variable.setAccessible(true);
			for (T t : cache.values()) {
				E value = (E) variable.get(t);
				boolean found = false;
				if (min == null) {
					found = true;
				} else if (min.compareTo(value) <= 0) {
					found = true;
				}
				if (max == null) {
					found &= true;
				} else if (max.compareTo(value) >= 0) {
					found &= true;
				}
				if (found) {
					ret.add(t);
				}
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 多重条件查询
	 *
	 * @param criteria 所有条件的集合
	 * @return 查询到的对象的集合
	 */
	@Override
	public List<T> multiQuery(List<Criterion> criteria) {
		ArrayList<T> ret = new ArrayList<>(this.cache.values());
		for (Criterion criterion : criteria) {
			List found = this.queryByCriterion(criterion);
			ret.retainAll(found);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private List<T> queryByCriterion(Criterion criterion) {
		switch (criterion.getQueryMode()) {
			case FULL:
				return this.fullyQuery(criterion.getField(), criterion.getValue());
			case FUZZY:
				return this.fuzzyQuery(criterion.getField(), (String) criterion.getValue());
			case RANGE:
				return this.rangeQuery(criterion.getField(), criterion.getMin(), criterion.getMax());
			case OR:
				List<T> ret = this.queryByCriterion(criterion.getCriterion1());
				ret.addAll(this.queryByCriterion(criterion.getCriterion2()));
				return ret;
		}
		System.out.println("ERROR：调用JsonDaoHelper.queryByCriterion()时出现未定义其行为的查询模式：" + criterion.getQueryMode());
		return new ArrayList<>();
	}

	private Field getField(String field) throws NoSuchFieldException {
		Field ret = null;
		Class classNowFinding = clazz;
		for (boolean isFound = false; !isFound; ) {
			try {
				ret = classNowFinding.getDeclaredField(field);
				isFound = true;
			} catch (NoSuchFieldException e) {
				classNowFinding = classNowFinding.getSuperclass();
				if (classNowFinding == null) {
					throw e;
				}
			}
		}
		return ret;
	}

	/**
	 * 统计持久化对象数量
	 *
	 * @return 持久化对象的个数
	 */
	@Override
	public Long count() {
		return (long) cache.size();
	}

	@PreDestroy
	public void destroy() {
		this.save();
	}
}
