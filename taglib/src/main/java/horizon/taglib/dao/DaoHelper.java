package horizon.taglib.dao;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.PO;
import horizon.taglib.utils.Criterion;

import java.util.List;

/**
 * Dao助手类接口，负责所有Dao的共同操作
 * <br>
 * created on 2018/03/15
 *
 * @param <T> 要操作的表的映射类
 * @author 巽
 **/
public interface DaoHelper<T extends PO> {
	/**
	 * 初始化
	 *
	 * @param clazz 泛型T的类型
	 */
	void initClass(Class<T> clazz);

	/**
	 * 添加对象
	 *
	 * @param po 待添加对象
	 * @return SUCCESS：添加成功
	 */
	ResultMessage add(T po);

	/**
	 * 删除对象
	 *
	 * @param id 待删除对象的id
	 * @return SUCCESS：删除成功<br>
	 * NOT_EXIST：对象不存在
	 */
	ResultMessage delete(Long id);

	/**
	 * 更新对象
	 *
	 * @param po 待更新对象
	 * @return SUCCESS：更新成功<br>
	 * NOT_EXIST：对象不存在
	 */
	ResultMessage update(T po);

	/**
	 * 得到待分配的新id
	 *
	 * @return 待分配的新id
	 */
	Long getNewId();

	/**
	 * 精确查询，多用于通过ID查询，默认结果数<=1
	 *
	 * @param field 要查询的字段
	 * @param value 要匹配的值
	 * @return 查询到的对象
	 */
	T exactlyQuery(String field, Object value);

	/**
	 * 完全匹配，多用于查询字段为某个值的所有对象
	 *
	 * @param field 要查询的字段，字段类型为集合类型(extends Collection)时调用Collection.contains(value)进行匹配
	 * @param value 要匹配的值
	 * @return 查询到的所有对象的集合
	 */
	List<T> fullyQuery(String field, Object value);

	/**
	 * 模糊查询
	 *
	 * @param field 要查询的字段，支持String、原始数据包裹类型(extends Number)和以String为泛型的集合类型(extends Collection&lt;String&gt;)字段
	 * @param value 要匹配的关键字
	 * @return 查询到的对象的集合
	 */
	List<T> fuzzyQuery(String field, String value);

	/**
	 * 范围查询
	 *
	 * @param field 要查询的字段
	 * @param min   要匹配的值的下限（若无则为null）
	 * @param max   要匹配的值的上限（若无则为null）
	 * @return 查询到的对象的集合
	 */
	<E> List<T> rangeQuery(String field, Comparable<E> min, Comparable<E> max);

	/**
	 * 多重条件查询
	 *
	 * @param criteria 所有条件的集合
	 * @return 查询到的对象的集合
	 */
	List<T> multiQuery(List<Criterion> criteria);

	/**
	 * 统计持久化对象数量
	 *
	 * @return 持久化对象的个数
	 */
	Long count();
}
