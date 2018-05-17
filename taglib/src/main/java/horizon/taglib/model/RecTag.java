package horizon.taglib.model;

import horizon.taglib.enums.TagType;
import horizon.taglib.utils.Point;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 矩形标注
 * <br>
 * created on 2018/03/17
 *
 * @author 巽
 **/
@Setter
@Getter
@Entity
@DiscriminatorValue(value = "rec")
@SuppressWarnings("unused")
public class RecTag extends Tag{
	/**
	 * 左上点
	 */
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name="x", column=@Column(name="start_x", table="tag")),
			@AttributeOverride(name="y", column=@Column(name="start_y", table="tag"))
	})
	private Point start;
	/**
	 * 右下点
	 */
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name="x", column=@Column(name="end_x", table="tag")),
			@AttributeOverride(name="y", column=@Column(name="end_y", table="tag"))
	})
	private Point end;

	public RecTag(){}

	public RecTag(Long taskPublisherId, Long taskWorkerId, String fileName, Long userId, TagDesc description, String color, TagType tagType, Point start, Point end) {
		super(taskPublisherId, taskWorkerId, fileName, userId, description, color, tagType);
		this.start = start;
		this.end = end;
	}

	public RecTag(Long tagId, Long taskPublisherId, Long taskWorkerId, String fileName, Long userId, TagDesc description, String color, TagType tagType, Point start, Point end) {
		super(tagId, taskPublisherId, taskWorkerId, fileName, userId, description, color, tagType);
		this.start = start;
		this.end = end;
	}

}
