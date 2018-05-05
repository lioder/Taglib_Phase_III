package horizon.taglib.enums;

import lombok.Getter;

/**
 * 一级升二级:100exp
 * 二级升三级:200exp
 * 三级升四级:500exp
 * 四级升五级:1000exp
 * 五级升六级:2000exp
 * 六级升七级:5000exp
 */

@Getter
public enum Level {
    LEVEL_ONE("塑料",1),LEVEL_TWO("玄铁",2),LEVEL_THREE("青铜",3),LEVEL_FOUR("白银",4),LEVEL_FIVE("黄金",5),
    LEVEL_SIX("铂金",6),LEVEL_SEVEN("钻石",7);

    private String value;

    private Integer code;

    Level(String value, Integer code){
        this.value=value;
        this.code = code;
    }

    public String toString(){
        return value;
    }
}
