package horizon.taglib.aspect;

import horizon.taglib.dao.LogDao;
import horizon.taglib.enums.OperationType;
import horizon.taglib.model.Log;
import horizon.taglib.model.PO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
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

	@Around(value = "execution(* horizon.taglib.dao.*.save(Object)) && args(po)", argNames = "pjp,po")
	private Object AfterAdd(ProceedingJoinPoint pjp, PO po) {
		try {
			boolean isAdd = (po.getId() == null || po.getId() == 0L);
			Object ret = pjp.proceed();
			if (ret != null && !(ret instanceof Log)) {
				if (isAdd) {
					logDao.save(new Log(LocalDateTime.now(), OperationType.ADD, po.getClass().getSimpleName(), po.toString()));
				} else {
					logDao.save(new Log(LocalDateTime.now(), OperationType.UPDATE, po.getClass().getSimpleName(), po.toString()));
				}
			}
			return ret;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return null;
	}

	@AfterReturning(
			value = "execution(horizon.taglib.enums.ResultMessage horizon.taglib.dao.*Dao.delete(Long)) && args(id)",
			argNames = "id")
	private void AfterDelete(Long id) {
		logDao.save(new Log(LocalDateTime.now(), OperationType.DELETE, null, String.valueOf(id)));
	}
}
