package horizon.taglib.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.springframework.stereotype.Component;
import scala.Serializable;

import java.util.List;
import java.util.Properties;

@Component
public class SparkUtil implements java.io.Serializable{
    transient JavaSparkContext sparkContext;
    transient SQLContext sqlContext;
    private static final String url = "jdbc:mysql://localhost:3306/taglibdatabase";
    transient Properties properties;

    public SparkUtil() {
        SparkConf conf = new SparkConf().setAppName("Taglib").setMaster("local");
        conf.setJars(new String[]{"./taglib/target/Taglib-0.0.1-SNAPSHOT.jar"});
        sparkContext = new JavaSparkContext(conf);
        sparkContext.setLogLevel("WARN");

        sqlContext = new SQLContext(sparkContext);

        properties = new Properties();
        properties.put("user", "root");
        properties.put("password", "admin");
        properties.put("driver", "com.mysql.jdbc.Driver");
    }

    public JavaSparkContext getSparkContext() {
        return sparkContext;
    }

    public DataFrame readMySQLTable(String tableName){
        return sqlContext.read().jdbc(url, tableName, properties).select("*");
    }
}
