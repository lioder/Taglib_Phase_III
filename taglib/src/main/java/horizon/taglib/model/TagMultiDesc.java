package horizon.taglib.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 表单描述
 * <br>
 * created on 2018/03/17
 *
 * @author 巽
 **/
@Setter
@Getter
public class TagMultiDesc extends TagDesc{
	/**
	 *
	 */
	private Map<String, String> descriptions;

	public TagMultiDesc() {
	}

	@SuppressWarnings("unused")
	public TagMultiDesc(Map<String, String> descriptions) {
		this.descriptions = descriptions;
	}
}
