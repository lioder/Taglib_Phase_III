package horizon.taglib.model;

import horizon.taglib.enums.TagType;
import horizon.taglib.utils.Distanceable;
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
public class RecTag extends Tag implements Distanceable<RecTag> {
	/**
	 * 左上点
	 */
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "x", column = @Column(name = "start_x", table = "tag")),
			@AttributeOverride(name = "y", column = @Column(name = "start_y", table = "tag"))
	})
	private Point start;
	/**
	 * 右下点
	 */
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "x", column = @Column(name = "end_x", table = "tag")),
			@AttributeOverride(name = "y", column = @Column(name = "end_y", table = "tag"))
	})
	private Point end;

	public RecTag() {
	}

	public RecTag(Long taskPublisherId, Long taskWorkerId, String fileName, Long userId, TagDesc description,
	              String color, TagType tagType, Point start, Point end) {
		super(taskPublisherId, taskWorkerId, fileName, userId, description, color, tagType);
		this.start = start;
		this.end = end;
	}

	public RecTag(Long tagId, Long taskPublisherId, Long taskWorkerId, String fileName, Long userId,
	              TagDesc description, String color, TagType tagType, Point start, Point end) {
		super(tagId, taskPublisherId, taskWorkerId, fileName, userId, description, color, tagType);
		this.start = start;
		this.end = end;
	}

	/**
	 * 得到与另一对象的欧式距离
	 *
	 * @param other 另一对象
	 * @return 欧式距离
	 */
	@Override
	public Double distanceFrom(RecTag other) {
		if (this.getStart() == null || this.getEnd() == null || other.getStart() == null || other.getEnd() == null) {
			return null;
		}
		double sx1 = this.getStart().getX();
		double sx2 = other.getStart().getX();
		double sy1 = this.getStart().getY();
		double sy2 = other.getStart().getY();
		double ex1 = this.getEnd().getX();
		double ex2 = other.getEnd().getX();
		double ey1 = this.getEnd().getY();
		double ey2 = other.getEnd().getY();
		return Math.sqrt((sx1 - sx2) * (sx1 - sx2) + (sy1 - sy2) * (sy1 - sy2)
				+ (ex1 - ex2) * (ex1 - ex2) + (ey1 - ey2) * (ey1 - ey2));
	}
}
