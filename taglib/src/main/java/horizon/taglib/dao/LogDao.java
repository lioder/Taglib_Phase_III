package horizon.taglib.dao;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.Log;
import horizon.taglib.utils.Criterion;

import java.util.List;

/**
 * 日志操作持久化对象接口
 * <br>
 * created on 2018/04/25
 *
 * @author 巽
 **/
public interface LogDao {
	/**
	 * 得到所有日志
	 *
	 * @return 所有日志记录的集合，若无则返回空表
	 */
	List<Log> getAll();

	/**
	 * 增加Log
	 *
	 * @param log 待添加的Log（没有id）
	 * @return SUCCESS：添加成功
	 */
	ResultMessage add(Log log);

	/**
	 * 多重条件查询
	 *
	 * @param criteria 所有条件的集合
	 * @return 查询到的对象的集合
	 */
	List<Log> multiQuery(List<Criterion> criteria);
}
