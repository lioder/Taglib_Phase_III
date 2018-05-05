package horizon.taglib.model;

import horizon.taglib.enums.TagType;
import horizon.taglib.utils.Point;
import lombok.Getter;
import lombok.Setter;

/**
 * 矩形标注
 * <br>
 * created on 2018/03/17
 *
 * @author 巽
 **/
@Setter
@Getter
public class RecTag extends Tag{
	/**
	 * 左上点
	 */
	private Point start;
	/**
	 * 右下点
	 */
	private Point end;

	@SuppressWarnings("unused")
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
