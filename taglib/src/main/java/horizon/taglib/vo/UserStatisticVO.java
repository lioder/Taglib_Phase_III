package horizon.taglib.vo;

import lombok.Data;

@Data
public class UserStatisticVO {

    /**
     * 平均等级
     */
    private double level;

    /**
     * 平均经验
     */
    private double exp;

    /**
     * 平均积分
     */
    private double point;

    /**
     * 平均准确度
     */
    private double accuracyRate;

    /**
     * 平均准时度
     */
    private double punctualityRate;

    public UserStatisticVO() {
    }

    public UserStatisticVO(double level, double exp, double point, double accuracyRate, double punctualityRate) {
        this.level = level;
        this.exp = exp;
        this.point = point;
        this.accuracyRate = accuracyRate;
        this.punctualityRate = punctualityRate;
    }
}
