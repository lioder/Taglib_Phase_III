package horizon.taglib.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.stereotype.Component;

@Component
public class SparkUtil {
    JavaSparkContext sparkContext;

    public SparkUtil() {
        SparkConf conf = new SparkConf().setAppName("Taglib");
        sparkContext = new JavaSparkContext(conf);
        sparkContext.setLogLevel("WARN");
    }

    public JavaSparkContext getSparkContext() {
        return sparkContext;
    }
}
