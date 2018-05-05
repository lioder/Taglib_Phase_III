package horizon.taglib.enums;

import lombok.Getter;

/**
 * 任务类型
 * <br>
 * created on 2018/03/17
 *
 * @author 巽
 **/
@Getter
public enum TaskType {
	SORT("分类任务",0), BOX("标框任务",1), REGION("区域任务",2);

	private String name;

	private Integer code;

	TaskType(String name, Integer code){
		this.name = name;
		this.code = code;
	}
}
