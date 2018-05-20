package horizon.taglib.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.stereotype.Component;
import scala.Serializable;

import java.util.List;

@Component
public class SparkUtil implements java.io.Serializable{
    transient JavaSparkContext sparkContext;

    public SparkUtil() {
        SparkConf conf = new SparkConf().setAppName("Taglib").setMaster("local");
        conf.setJars(new String[]{"./taglib/target/Taglib-0.0.1-SNAPSHOT.jar"});
        sparkContext = new JavaSparkContext(conf);
        sparkContext.setLogLevel("WARN");
    }

    public JavaSparkContext getSparkContext() {
        return sparkContext;
    }
}
