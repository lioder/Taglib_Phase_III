package horizon.taglib.enums;

import lombok.Getter;

/**
 * 用户类型
 * <br>
 * created on 2018/03/17
 *
 * @author 巽
 **/
@Getter
public enum UserType {
	WORKER("众包工人", 0), REQUESTOR("众包发起者", 1), ADMIN("管理员", 2);

	private String name;

	private Integer code;

	UserType(String name, Integer code){
		this.name = name;
		this.code = code;
	}
}
