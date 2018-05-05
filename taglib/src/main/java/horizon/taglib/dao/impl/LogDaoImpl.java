package horizon.taglib.dao.impl;

import horizon.taglib.dao.DaoHelper;
import horizon.taglib.dao.LogDao;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.Log;
import horizon.taglib.utils.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志持久化对象实现类
 * <br>
 * created on 2018/04/25
 *
 * @author 巽
 **/
@Repository
public class LogDaoImpl implements LogDao {
	private DaoHelper<Log> daoHelper;

	@Autowired
	public LogDaoImpl(DaoHelper<Log> daoHelper) {
		this.daoHelper = daoHelper;
		daoHelper.initClass(Log.class);
	}

	/**
	 * 得到所有日志
	 *
	 * @return 所有日志记录的集合，若无则返回空表
	 */
	@Override
	public List<Log> getAll() {
		return daoHelper.multiQuery(new ArrayList<>());
	}

	/**
	 * 增加Log
	 *
	 * @param log 待添加的Log（没有id）
	 * @return SUCCESS：添加成功
	 */
	@Override
	public ResultMessage add(Log log) {
		return daoHelper.add(log);
	}

	/**
	 * 多重条件查询
	 *
	 * @param criteria 所有条件的集合
	 * @return 查询到的对象的集合
	 */
	@Override
	public List<Log> multiQuery(List<Criterion> criteria) {
		return daoHelper.multiQuery(criteria);
	}

}
