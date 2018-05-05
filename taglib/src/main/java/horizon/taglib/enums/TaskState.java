package horizon.taglib.enums;

import lombok.Getter;

/**
 * no_description
 * <br>
 * created on 2018/04/03
 *
 * @author 巽
 **/
@Getter
public enum TaskState {
	SUBMITTED("已提交"), PASS("审核通过"), REJECT("审核不通过"), GIVE_UP("放弃"), PROCESSING("进行中"), DONE("已完成"), OVERTIME("超时");

	TaskState(String value){
		this.value = value;
	}

	String value;
}
