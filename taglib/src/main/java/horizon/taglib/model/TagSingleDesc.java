package horizon.taglib.model;

import horizon.taglib.enums.TagDescType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 标签描述
 * <br>
 * created on 2018/03/17
 *
 * @author 巽
 **/
@Setter
@Getter
@Entity
@DiscriminatorValue("single")
public class TagSingleDesc extends TagDesc{
	private String description;

	public TagSingleDesc(){}

	public TagSingleDesc(String description) {
		this.description = description;
	}

	public TagSingleDesc(TagDescType tagDescType, String description) {
		super.setTagDescType(tagDescType);
		this.description = description;
	}
}
