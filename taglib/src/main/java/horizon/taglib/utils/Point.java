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
	private int x, y;

	public Point() {
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
