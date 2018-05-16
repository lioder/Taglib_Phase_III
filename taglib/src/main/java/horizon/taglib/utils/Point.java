package horizon.taglib.utils;

import lombok.Data;

/**
 * 点
 * <br>
 * created on 2018/03/17
 *
 * @author 巽
 **/
@Data
public class Point {
	private double x, y;

	public Point() {
	}

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
}