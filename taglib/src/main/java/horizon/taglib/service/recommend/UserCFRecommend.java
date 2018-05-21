package horizon.taglib.service.recommend;

import horizon.taglib.utils.SparkUtil;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.Tuple2;
import scala.util.parsing.combinator.testing.Str;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserCFRecommend {
    private SparkUtil sparkUtil;
    transient JavaSparkContext context;
    private JavaRDD<Rating> ratings;
    private MatrixFactorizationModel model;

    private final static int rank = 10;
    private final static int numIterations = 10;
    private final static String path = "./taglib/database/myRatings.csv";

    @Autowired
    public UserCFRecommend(SparkUtil sparkUtil) {
        this.sparkUtil = sparkUtil;
        context = sparkUtil.getSparkContext();
    }

    public List<Integer> getRecommendItems(Integer userId, Integer size, List<Integer> fitTaskPublisherIds) {
        JavaRDD<Row> rowJavaRDD = sparkUtil.readMySQLTable("task_worker").toJavaRDD();
        JavaRDD<Rating> ratings = rowJavaRDD.map(row -> {
            int uid = new Long(row.getLong(8)).intValue();
            int tid = new Long(row.getLong(6)).intValue();
            double rating = (double)row.getInt(3);
            return new Rating(uid,tid,rating);
        });

        // 为了给新用户推荐任务，要加一行
        List<Rating> ratingList = ratings.collect();
        ratingList.add(new Rating(userId, 0, 0));
        ratings = context.parallelize(ratingList);

        model = ALS.train(JavaRDD.toRDD(ratings), rank, numIterations, 0.01);

        // 我没做过的任务
        JavaRDD<Integer> fitTaskPublisherIdsRDD = context.parallelize(fitTaskPublisherIds);

        // 构造可以推荐的任务元组数组
        JavaPairRDD<Integer, Integer> recommendList = JavaPairRDD.fromJavaRDD(fitTaskPublisherIdsRDD.map(taskPublisher -> {
            return new Tuple2<Integer, Integer>(userId, taskPublisher);
        }));
        // 请模型预测用户对这些任务的喜好程度，取{size}个
        List<Integer> list = model.predict(recommendList).sortBy(rating -> {
            System.out.println(rating.rating());
            return rating.rating();
        }, false, 1).map(rating -> {
            return rating.product();
        }).take(size);

        // 将预测的任务ID返回
        return list;
    }
}
