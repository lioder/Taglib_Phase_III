package horizon.taglib.utils;

import lombok.Data;

import javax.persistence.Embeddable;

/**
 * 点
 * <br>
 * created on 2018/03/17
 *
 * @author 巽
 **/
@Data
@Embeddable
@SuppressWarnings("unused")
public class Point implements Distanceable<Point> {
	private double x, y;

	public Point() {
	}

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * 得到与另一对象的欧式距离
	 *
	 * @param other 另一对象
	 * @return 距离
	 */
	@Override
	public Double distanceFrom(Point other) {
		if (other == null) {
			return null;
		}
		return Math.sqrt((this.getX() - other.getX()) * (this.getX() - other.getX())
				+ (this.getY() - other.getY()) * (this.getY() - other.getY()));
	}
}
