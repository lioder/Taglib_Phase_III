package horizon.taglib.utils;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
@SuppressWarnings("unused")
public class CenterTag {
    /** 左上角x坐标*/
    double start_x;
    /** 左上角y坐标*/
    double start_y;
    /** 右下角x坐标*/
    double end_x;
    /** 右下角y坐标*/
    double end_y;
    /** 描述*/
    String desc;

    public CenterTag(){
    }

    public CenterTag(double start_x, double start_y, double end_x, double end_y, String desc) {
        this.start_x = start_x;
        this.start_y = start_y;
        this.end_x = end_x;
        this.end_y = end_y;
        this.desc = desc;
    }

}
