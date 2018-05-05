package horizon.taglib.aspect;

import horizon.taglib.dao.LogDao;
import horizon.taglib.enums.OperationType;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.Log;
import horizon.taglib.model.PO;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 日志切面类
 * <br>
 * created on 2018/04/24
 *
 * @author 巽
 **/
@Component("LogAspect")
@Aspect
public class LogAspect {
	private final LogDao logDao;

	@Autowired
	public LogAspect(LogDao logDao) {
		this.logDao = logDao;
	}

	@AfterReturning(
			value = "execution(horizon.taglib.enums.ResultMessage horizon.taglib.dao.DaoHelper.add(..)) && args(po, ..)",
			returning = "resultMessage",
			argNames = "resultMessage,po")
	private void AfterAdd(ResultMessage resultMessage, PO po) {
		if (resultMessage == ResultMessage.SUCCESS && !(po instanceof Log)) {
			logDao.add(new Log(LocalDateTime.now(), OperationType.ADD, po.getClass(), po.toString()));
		}
	}

	@AfterReturning(
			value = "execution(horizon.taglib.enums.ResultMessage horizon.taglib.dao.DaoHelper.delete(..)) && args(id, ..)",
			returning = "resultMessage",
			argNames = "resultMessage,id")
	private void AfterDelete(ResultMessage resultMessage, Long id) {
		if (resultMessage == ResultMessage.SUCCESS) {
			logDao.add(new Log(LocalDateTime.now(), OperationType.DELETE, null, String.valueOf(id)));
		}
	}

	@AfterReturning(
			value = "execution(horizon.taglib.enums.ResultMessage horizon.taglib.dao.DaoHelper.update(..)) && args(po, ..)",
			returning = "resultMessage",
			argNames = "resultMessage,po")
	private void AfterUpdate(ResultMessage resultMessage, PO po) {
		if (resultMessage == ResultMessage.SUCCESS && !(po instanceof Log)) {
			logDao.add(new Log(LocalDateTime.now(), OperationType.UPDATE, po.getClass(), po.toString()));
		}
	}
}
