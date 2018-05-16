package horizon.taglib.service.recommend;

import horizon.taglib.utils.SparkUtil;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.util.List;

@Component
public class UserCFRecommend {
    private SparkUtil sparkUtil;
    JavaSparkContext context;
    private JavaRDD<Rating> ratings;
    private MatrixFactorizationModel model;

    @Autowired
    public UserCFRecommend(SparkUtil sparkUtil) {
        this.sparkUtil = sparkUtil;
        context = sparkUtil.getSparkContext();
        String path = "./taglib/database/myRatings.csv";
        JavaRDD<String> ratingData = context.textFile(path);
        ratings = ratingData.map(s -> {
            String[] sarray = s.split(",");
            return new Rating(Integer.parseInt(sarray[0]),
                    Integer.parseInt(sarray[1]),
                    Double.parseDouble(sarray[2]));
        });

        int rank = 10; // 秩
        int numIterations = 10; // 迭代次数
        model = ALS.train(JavaRDD.toRDD(ratings), rank, numIterations, 0.01);
    }

    public List<Integer> getRecommendItems(Integer userId, Integer size, List<Integer> fitTaskPublisherIds) {

        // 我没做过的任务
        JavaRDD<Integer> fitTaskPublisherIdsRDD = context.parallelize(fitTaskPublisherIds);

        // 构造可以推荐的任务元组数组
        JavaPairRDD<Integer, Integer> recommendList = JavaPairRDD.fromJavaRDD(fitTaskPublisherIdsRDD.map(taskPublisher -> {
            return new Tuple2<Integer, Integer>(userId, taskPublisher);
        }));
//        System.out.println(model.predict(20, 24));
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
