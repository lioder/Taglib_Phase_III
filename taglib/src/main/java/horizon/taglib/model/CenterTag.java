package horizon.taglib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "center_tag")
@SuppressWarnings("unused")
public class CenterTag extends PO implements Serializable {
    /** 所属的TaskPublisher的id*/
    @JsonIgnore
    private Long taskPublisherId;
    /** 关联的文件（图片）名*/
    private String fileName;
    /** 左上角x坐标*/
    private double start_x;
    /** 左上角y坐标*/
    private double start_y;
    /** 右下角x坐标*/
    private double end_x;
    /** 右下角y坐标*/
    private double end_y;
    /** 描述*/
    @JsonProperty("label")
    private String description;

    public CenterTag(){
    }

    public CenterTag(Long taskPublisherId, String fileName, double start_x,double start_y,double end_x,double end_y,String description){
        this.taskPublisherId = taskPublisherId;
        this.fileName = fileName;
        this.start_x=start_x;
        this.start_y=start_y;
        this.end_x=end_x;
        this.end_y=end_y;
        this.description = description;
    }

}
