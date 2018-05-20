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
			value = "execution(* horizon.taglib.dao.*.save(..)) && args(po, ..)",
			returning = "added",
			argNames = "added,po")
	private void AfterAdd(PO added, PO po) {
		if (added != null && !(po instanceof Log)) {
			logDao.save(new Log(LocalDateTime.now(), OperationType.ADD, po.getClass().getSimpleName(), po.toString()));
		}
	}

	@AfterReturning(
			value = "execution(horizon.taglib.enums.ResultMessage horizon.taglib.dao.*Dao.delete(..)) && args(id, ..)",
			returning = "resultMessage",
			argNames = "resultMessage,id")
	private void AfterDelete(ResultMessage resultMessage, Long id) {
		if (resultMessage == ResultMessage.SUCCESS) {
			logDao.save(new Log(LocalDateTime.now(), OperationType.DELETE, null, String.valueOf(id)));
		}
	}

//	@AfterReturning(
//			value = "execution(horizon.taglib.enums.ResultMessage horizon.taglib.dao.*Dao.update(..)) && args(po, ..)",
//			returning = "added",
//			argNames = "added,po")
//	private void AfterUpdate(ResultMessage resultMessage, PO po) {
//		if (resultMessage == ResultMessage.SUCCESS && !(po instanceof Log)) {
//			logDao.save(new Log(LocalDateTime.now(), OperationType.UPDATE, po.getClass(), po.toString()));
//		}
//	}
}
