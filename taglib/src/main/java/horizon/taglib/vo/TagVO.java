package horizon.taglib.vo;

import horizon.taglib.utils.Point;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TagVO {
    Long id;

    /**
     * 标签类型
     */
    Integer tagType;

    /**
     * 铅笔笔记
     */
    List<Point> penPoints;

    /**
     * 矩形左上点
     */
    Point startPosition;

    /**
     * 结束点
     */
    Point endPosition;

    /**
     * 注释类型
     */
    Integer descType;

    /**
     * map注释
     */
    Map<String,String> mapDesc;

    /**
     * 短注释
     */
    String singleDesc;

    /**
     * 标记颜色
     */
    String color;

}
