package horizon.taglib.utils;

import horizon.taglib.enums.QueryMode;
import lombok.Data;

/**
 * 枚举各种查询数据库的方式
 * <br>
 * created on 2018/04/08
 *
 * @author 巽
 **/
@Data
public class Criterion<T> {
	/**
	 * 要查询的字段
	 */
	private String field;
	/**
	 * 匹配值
	 */
	private T value;
	/**
	 * 范围查询方式时的下限
	 */
	private Comparable<T> min;
	/**
	 * 范围查询方式时的上限
	 */
	private Comparable<T> max;
	/**
	 * 查询数据库的方式
	 */
	private QueryMode queryMode;
	/**
	 * 并集查询方式时的约束1
	 */
	private Criterion criterion1;
	/**
	 * 并集查询方式时的约束2
	 */
	private Criterion criterion2;

	/**
	 * 完全/模糊匹配查询
	 * @param field 字段
	 * @param value 匹配值
	 * @param queryMode 查询方式
	 */
	public Criterion(String field, T value, QueryMode queryMode) {
		this.field = field;
		this.queryMode = queryMode;
//		if(queryMode == QueryMode.FUZZY){
//			this.value = '%' + value.toString() + '%';
//		}
//		else{
			this.value = value;
//		}
	}

	/**
	 * 范围查询约束
	 * @param field 字段
	 * @param min 下限（若无则为null）
	 * @param max 上限（若无则为null）
	 */
	public Criterion(String field, Comparable<T> min, Comparable<T> max) {
		this.field = field;
		this.min = min;
		this.max = max;
		this.queryMode = QueryMode.RANGE;
	}

	/**
	 * 并集查询约束
	 * @param criterion1 约束1
	 * @param criterion2 约束2
	 */
	public Criterion(Criterion criterion1, Criterion criterion2){
		this.criterion1 = criterion1;
		this.criterion2 = criterion2;
		this.queryMode = QueryMode.OR;
	}
}
