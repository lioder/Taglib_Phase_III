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
@Table(name = "log")
public class Log extends PO implements Serializable {
	/**
	 * 记录时间
	 */
	private LocalDateTime dateAndTime;
	/**
	 * 操作员Id
	 */
	private Long userId;
	/**
	 * 操作类型
	 */
	private OperationType operationType;
	/**
	 * 操作对象类型
	 */
	private Class operationObjectType;
	/**
	 * 对象详情
	 */
	private String details;

	@SuppressWarnings("unused")
	public Log() {
	}

	public Log(LocalDateTime dateAndTime, OperationType operationType, Class operationObjectType, String details) {
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
				", userId=" + userId +
				", operationType=" + operationType +
				", operationObjectType=" + operationObjectType +
				", details='" + details + '\'' +
				'}';
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return super.getId();
	}
}
