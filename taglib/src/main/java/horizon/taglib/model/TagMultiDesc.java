package horizon.taglib.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
@Entity
@DiscriminatorValue("multiple")
public class TagMultiDesc extends TagDesc{
	/**
	 * <Label, description>
	 */
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(name = "multi_tag_label_to_description")
	@MapKeyColumn(name = "label")
	@Column(name = "description")
	private Map<String, String> descriptions;

	public TagMultiDesc() {
	}

	@SuppressWarnings("unused")
	public TagMultiDesc(Map<String, String> descriptions) {
		this.descriptions = descriptions;
	}
}
