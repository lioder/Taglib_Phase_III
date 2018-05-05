package horizon.taglib.enums;

/**
 * 日志中每条记录的操作类型
 * <br>
 * created on 2018/04/25
 *
 * @author 巽
 **/
public enum OperationType {
	ADD("增加"),
	DELETE("删除"),
	UPDATE("修改"),
	EXAMINE("审批");

	String value;

	OperationType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
