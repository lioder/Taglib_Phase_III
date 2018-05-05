package horizon.taglib.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import horizon.taglib.enums.TagDescType;
import lombok.Getter;
import lombok.Setter;

/**
 * 标注中的描述
 * <br>
 * created on 2018/03/17
 *
 * @author 巽
 **/
@Setter
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({@JsonSubTypes.Type(value = TagSingleDesc.class, name = "SingleDesc"),
		@JsonSubTypes.Type(value = TagMultiDesc.class, name = "MultiDesc")})
public class TagDesc {
	private Long tagId;

	private TagDescType tagDescType;

	@SuppressWarnings("all")
	public TagDesc(){}
}
