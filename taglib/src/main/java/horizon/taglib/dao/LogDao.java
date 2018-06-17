package horizon.taglib.dao;

import horizon.taglib.enums.OperationType;
import horizon.taglib.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 日志操作持久化对象接口
 * <br>
 * created on 2018/04/25
 *
 * @author 巽
 **/
public interface LogDao extends JpaRepository<Log, Long>, BaseRepository<Log, Long> {
	/**
	 * 得到该操作类型和该操作对象类型的所有日志
	 *
	 * @param operationType 操作类型
	 * @param operationObjectType 操作对象类型
	 * @return 所有日志的列表
	 */
	List<Log> findByOperationTypeAndOperationObjectType(OperationType operationType, String operationObjectType);

	/**
	 * 得到该操作类型和该操作对象类型的详情中含有该关键字的所有日志
	 *
	 * @param operationType 操作类型
	 * @param operationObjectType 操作对象类型
	 * @param details 详情
	 * @return 所有日志的列表
	 */
	List<Log> findByOperationTypeAndOperationObjectTypeAndDetailsContaining(OperationType operationType,
	                                                                        String operationObjectType, String details);
}
