package horizon.taglib.enums;

import lombok.Getter;

/**
 * 标注结果
 * <br>
 * created on 2018/06/08
 *
 * @author 巽
 **/
@Getter
public enum TagResult {
	CORRECT("正确"), WRONG("错误"), MISS("遗漏");

	private String value;

	TagResult(String value){
		this.value=value;
	}

	public String toString(){
		return value;
	}
}
