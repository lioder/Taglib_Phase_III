package horizon.taglib.utils;

/**
 * 可求距离的接口
 * <br>
 * created on 2018/06/04
 *
 * @author 巽
 **/
public interface Distanceable<T> {
	/**
	 * 得到与另一对象的距离
	 *
	 * @param other 另一对象
	 * @return 距离
	 */
	Double distanceFrom(T other);
}
