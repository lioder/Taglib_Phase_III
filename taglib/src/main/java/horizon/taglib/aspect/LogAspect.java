package horizon.taglib.aspect;

import horizon.taglib.dao.LogDao;
import horizon.taglib.enums.OperationType;
import horizon.taglib.model.Log;
import horizon.taglib.model.PO;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.vo.TaskProVO;
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
	private Object AroundAddOrUpdate(ProceedingJoinPoint pjp, PO po) {
		try {
			boolean isAdd = (po.getId() == null || po.getId() == 0L);
			Object ret = pjp.proceed();
			if (ret != null && !(ret instanceof Log)) {
				if (isAdd) {
					logDao.save(new Log(LocalDateTime.now(), OperationType.ADD, po.getClass().getSimpleName(), getDetails(po)));
				} else {
					logDao.save(new Log(LocalDateTime.now(), OperationType.UPDATE, po.getClass().getSimpleName(), getDetails(po)));
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

	@AfterReturning(
			value = "execution(horizon.taglib.vo.ResultVO horizon.taglib.controller.AdminController.checkTask(Long, Boolean))" +
					" && args(taskPublisherId, checkResult)",
			argNames = "taskPublisherId, checkResult")
	private void AfterCheckTask(Long taskPublisherId, Boolean checkResult) {
//		System.out.println("监听到管理员审批任务：id=" + taskPublisherId + ", isPass=" + checkResult);
		logDao.save(new Log(LocalDateTime.now(), OperationType.ADMIN_EXAMINE, TaskPublisher.class.getSimpleName(),
				"TaskPublisher{id=" + taskPublisherId + ", isPass=" + checkResult + "}"));
	}

	@AfterReturning(
			value = "execution(horizon.taglib.vo.ResultVO" +
					" horizon.taglib.controller.UserController.submitProTag(horizon.taglib.vo.TaskProVO))" +
					" && args(taskProVO)",
			argNames = "taskProVO")
	private void AfterSubmitProTag(TaskProVO taskProVO) {
//		System.out.println("监听到专家提交专家标注任务：" + taskProVO.toString());
		logDao.save(new Log(LocalDateTime.now(), OperationType.ADD, TaskProVO.class.getSimpleName(), getDetails(taskProVO)));
	}

	@AfterReturning(
			value = "execution(horizon.taglib.enums.ResultMessage horizon.taglib.service.valuedata.UserAccuracy.adjustUserAccuracy(long))" +
					" && args(taskPublisherId)",
			argNames = "taskPublisherId")
	private void AfterAdjustUserAccuracy(long taskPublisherId) {
//		System.out.println("监听到自动审核任务：id=" + taskPublisherId);
		logDao.save(new Log(LocalDateTime.now(), OperationType.AUTO_EXAMINE, TaskPublisher.class.getSimpleName(),
				"TaskPublisher{id=" + taskPublisherId + "}"));
	}

	private String getDetails(Object o) {
		String details = o.toString();
		if (details.length() > 255) {
			details = details.substring(0, 255);
		}
		return details;
	}
}
