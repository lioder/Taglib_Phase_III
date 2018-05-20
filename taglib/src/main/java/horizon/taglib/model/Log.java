package horizon.taglib.model;

import horizon.taglib.enums.OperationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 日志持久化对象
 * <br>
 * created on 2018/04/25
 *
 * @author 巽
 **/
@Setter
@Getter
@Entity
public class Log extends PO implements Serializable {
	/**
	 * 记录时间
	 */
	private LocalDateTime dateAndTime;
	/**
	 * 操作类型
	 */
	private OperationType operationType;
	/**
	 * 操作对象类型，通过 po.getClass().getSimpleName() 或 PO.class.getSimpleName() 得到（po或PO换成具体的实体对象或类）
	 */
	private String operationObjectType;
	/**
	 * 对象详情
	 */
	private String details;

	@SuppressWarnings("unused")
	public Log() {
	}

	public Log(LocalDateTime dateAndTime, OperationType operationType, String operationObjectType, String details) {
		this.dateAndTime = dateAndTime;
		this.operationType = operationType;
		this.operationObjectType = operationObjectType;
		this.details = details;
	}

	@Override
	public String toString() {
		return "Log{" +
				"id=" + this.getId() +
				", dateAndTime='" + dateAndTime + '\'' +
				", operationType=" + operationType +
				", operationObjectType=" + operationObjectType +
				", details='" + details + '\'' +
				'}';
	}
}
