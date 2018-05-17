package horizon.taglib.model;

import horizon.taglib.enums.TagType;
import horizon.taglib.utils.Point;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;

/**
 * 不规则标注（画笔）
 * <br>
 * created on 2018/03/17
 *
 * @author 巽
 **/
@Setter
@Getter
@Entity
@DiscriminatorValue(value = "irregular")
@SuppressWarnings("unused")
public class IrregularTag extends Tag {
	private ArrayList<Point> points;

	public IrregularTag(){}

	public IrregularTag(Long taskPublisherId, Long taskWorkerId, String fileName, Long userId, TagDesc description, String color, TagType tagType, ArrayList<Point> points) {
		super(taskPublisherId, taskWorkerId, fileName, userId, description, color, tagType);
		this.points = points;
	}

	public IrregularTag(Long tagId, Long taskPublisherId, Long taskWorkerId, String fileName, Long userId, TagDesc description, String color, TagType tagType, ArrayList<Point> points) {
		super(tagId, taskPublisherId, taskWorkerId, fileName, userId, description, color, tagType);
		this.points = points;
	}
}