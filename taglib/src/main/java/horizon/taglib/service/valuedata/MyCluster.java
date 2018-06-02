package horizon.taglib.service.valuedata;

import horizon.taglib.model.RecTag;
import lombok.Data;
import org.apache.spark.mllib.linalg.Vector;

import java.util.List;

@Data
public class MyCluster {

    int clusterNo;
    List<RecTag> recTags;
    String filename;
    Vector center;

    public MyCluster(int clusterNo, List<RecTag> recTags, String filename, Vector center) {
        this.clusterNo = clusterNo;
        this.recTags = recTags;
        this.filename = filename;
        this.center = center;
    }
}
