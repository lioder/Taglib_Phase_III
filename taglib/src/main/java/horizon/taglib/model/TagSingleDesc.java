package horizon.taglib.model;

import horizon.taglib.enums.TagDescType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

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

	@SuppressWarnings("unused")
	public TagSingleDesc(String description) {
		this.description = description;
	}

	public TagSingleDesc(TagDescType tagDescType, String description) {
		super.setTagDescType(tagDescType);
		this.description = description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TagSingleDesc that = (TagSingleDesc) o;
		return Objects.equals(getDescription(), that.getDescription());
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getDescription());
	}
}
