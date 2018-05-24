package horizon.taglib.service.recommend;

import Jama.Matrix;
import horizon.taglib.utils.SparkUtil;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.Serializable;
import scala.Tuple2;

import java.beans.Transient;
import java.util.*;

@Component
public class ItemCFRecommend implements java.io.Serializable {

    private SparkUtil sparkUtil;
    transient private JavaSparkContext javaSparkContext;
    private List<Long> users;
    private List<Long> taskPublishers;
    private List<List<Double>> cosines;


    @Autowired
    public ItemCFRecommend(SparkUtil sparkUtil) {
        this.sparkUtil = sparkUtil;
        javaSparkContext = sparkUtil.getSparkContext();
        users = new ArrayList<>();
        taskPublishers = new ArrayList<>();
        cosines = new ArrayList<>();
        init();
    }

//    public static void main(String[] args){
//        ItemCFRecommend itemCFRecommend = new ItemCFRecommend();
//        SparkConf conf = new SparkConf().setAppName("Taglib");
//        JavaSparkContext sparkContext = new JavaSparkContext(conf);
//        sparkContext.setLogLevel("WARN");
//        JavaRDD<String> data = sparkContext.textFile(path);
//        init();
//    }

    public void init() {
//        SparkUtil sparkUtil = new SparkUtil();
//        JavaSparkContext javaSparkContext = sparkUtil.getSparkContext();
//        SparkConf conf = new SparkConf().setAppName("Taglib");
//        JavaSparkContext sparkContext = new JavaSparkContext(conf);
//        sparkContext.setLogLevel("WARN");
//        JavaRDD<String> data = javaSparkContext.textFile(path);
//        data = javaSparkContext.textFile(path);
        JavaRDD<Row> rowJavaRDD = sparkUtil.readMySQLTable("task_worker").toJavaRDD();
        JavaRDD<Rating> data = rowJavaRDD.map(row -> {
            int uid = new Long(row.getLong(8)).intValue();
            int tid = new Long(row.getLong(6)).intValue();
            double rating = (double) row.getInt(3);
            System.out.println(uid + "   " + tid + "   " + rating);
            return new Rating(uid, tid, rating);
        });

        if(data.isEmpty()){
            return;
        }

        final List<Long> users = data.map(rating -> {
            return (long) rating.user();
        }).repartition(1).distinct().collect();

        final List<Long> taskPublishers = data.map(rating -> {
            return (long) rating.product();
        }).repartition(1).distinct().collect();
        Collections.sort(taskPublishers);

        // Creating RDD. It is of the form <userId,<taskPublisherId,rating>> userId is key
        JavaPairRDD<Long, Tuple2<Long, Double>> ratings = data.mapToPair(rating -> {
            Long userId = (long) rating.user();
            Long taskPublisherId = (long) rating.product();
            Double ratingValue = rating.rating();
            return new Tuple2<>(userId, new Tuple2<>(taskPublisherId, ratingValue));
        }).repartition(1).sortByKey();

        JavaPairRDD<Long, List<Double>> utility = ratings.mapPartitionsToPair(
                new PairFlatMapFunction<Iterator<Tuple2<Long, Tuple2<Long, Double>>>, Long, List<Double>>() {
                    @Override
                    public Iterable<Tuple2<Long, List<Double>>> call(Iterator<Tuple2<Long, Tuple2<Long, Double>>> tuple2Iterator) {
                        List<Double> userRatings;
                        List<Tuple2<Long, List<Double>>> utilmatrix = new ArrayList<>();
                        int count = 0;
                        Tuple2<Long, Tuple2<Long, Double>> t = new Tuple2<>(null, null);

                        while (tuple2Iterator.hasNext()) {
                            userRatings = new ArrayList<>();
                            for (int i = 0; i < taskPublishers.size(); i++) {
                                userRatings.add((double) 0);
                            }
                            if (count == 0) {
                                t = tuple2Iterator.next();
                            }
                            Long userId = t._1;
                            Long taskPublisherId = t._2._1;
                            Double rating = t._2._2;
                            int index = taskPublishers.indexOf(taskPublisherId);
                            userRatings.set(index, rating);

                            while (tuple2Iterator.hasNext()) {
                                t = tuple2Iterator.next();
                                if (t._1 == userId) {
                                    taskPublisherId = t._2._1;
                                    rating = t._2._2;
                                    index = taskPublishers.indexOf(taskPublisherId);
                                    userRatings.set(index, rating);
                                } else {
                                    break;
                                }
                            }
                            Tuple2<Long, List<Double>> t1 = new Tuple2<>(userId, userRatings);

                            utilmatrix.add(t1);
                            count++;
                        }
                        if (t._1 != utilmatrix.get(utilmatrix.size() - 1)._1) {
                            userRatings = new ArrayList<>();
                            for (int i = 0; i < taskPublishers.size(); i++) {
                                userRatings.add((double) 0);
                            }
                            Long userId = t._1;
                            Long taskPublisherId = t._2._1;
                            Double rating = t._2._2;
                            int index = taskPublishers.indexOf(taskPublisherId);
                            userRatings.set(index, rating);
                            Tuple2<Long, List<Double>> t1 = new Tuple2<>(userId, userRatings);

                            utilmatrix.add(t1);
                        }

                        return utilmatrix;
                    }
                }
        );
        // Uncomment the following line, if you want to see userid,ratings for each item he rated.
//        utility.saveAsTextFile("utility");
//        ratings.saveAsTextFile("ratings");

        // Finding item item cosine similarity between every pair of items
        JavaRDD<List<Double>> cosinematrix = utility.mapPartitions(new FlatMapFunction<Iterator<Tuple2<Long, List<Double>>>, List<Double>>() {
            @Override
            public Iterable<List<Double>> call(Iterator<Tuple2<Long, List<Double>>> tuple2Iterator) {
                int i = 0;

                List<Double> cosimilarlist;
                List<List<Double>> cosinesimilarity = new ArrayList<>();
                Matrix utils1 = new Matrix(users.size(), taskPublishers.size());
                Matrix cosine = new Matrix(taskPublishers.size(), taskPublishers.size());
                double[][] y = null, y1 = null;
                while (tuple2Iterator.hasNext()) {
                    Tuple2<Long, List<Double>> temp1 = tuple2Iterator.next();
                    List<Double> lis = temp1._2;
                    for (int j = 0; j < lis.size(); j++) {
                        utils1.set(i, j, lis.get(j));
                    }
                    i++;
                }
                double[] d = new double[users.size()];
                double[] e = new double[users.size()];
                for (int k = 0; k < taskPublishers.size(); k++) {
                    for (int j = 0; j < users.size(); j++) {
                        d[j] = utils1.get(j, k);
                    }
                    for (int q = k + 1; q < taskPublishers.size(); q++) {
                        double num = 0;
                        double d2 = 0;
                        double e2 = 0;
                        for (int g = 0; g < users.size(); g++) {
                            e[g] = utils1.get(g, q);
                        }
                        for (int w = 0; w < users.size(); w++) {
                            num = num + d[w] * e[w];
                            d2 = d2 + d[w] * d[w];
                            e2 = e2 + e[w] * e[w];
                        }
                        double result;
                        if (d2 == 0 || e2 == 0) {
                            result = 0.0;
                        } else {
                            result = num / (Math.sqrt(e2) * Math.sqrt(d2));
                        }
                        cosine.set(k, q, result);
                        cosine.set(q, k, result);
                    }
                }
                utils1 = null;
                y1 = cosine.getArray();
                for (int p = 0; p < taskPublishers.size(); p++) {
                    cosimilarlist = new ArrayList<>();
                    for (int f = 0; f < taskPublishers.size(); f++) {
                        cosimilarlist.add(y1[p][f]);
                    }
                    cosinesimilarity.add(cosimilarlist);
                }
                cosine = null;

                return cosinesimilarity;
            }
        });

//        cosinematrix.saveAsTextFile("cosine");

        final List<List<Double>> cosines = cosinematrix.collect();

//        JavaPairRDD<Long, List<Tuple2<Long, Double>>> prediction = utility.mapPartitionsToPair(
//                new PairFlatMapFunction<Iterator<Tuple2<Long, List<Double>>>, Long, List<Tuple2<Long, Double>>>() {
//                    @Override
//                    public Iterable<Tuple2<Long, List<Tuple2<Long, Double>>>> call(Iterator<Tuple2<Long, List<Double>>> tuple2Iterator){
//                        List<Tuple2<Long, List<Tuple2<Long, Double>>>> weightedAvgPrediction = new ArrayList<>();
//                        while(tuple2Iterator.hasNext()){
//                            Tuple2<Long, List<Double>> userRating = tuple2Iterator.next();
//                            Long userId = userRating._1;
//                            List<Double> ratingList = userRating._2;
//                            List<List<Double>> finalTasks = new ArrayList<>();
//                            List<Double> ratingPredict = new ArrayList<>();
//
//                            for(int i=0;i<ratingList.size();i++){
//                                Long taskPublisherId = taskPublishers.get(i);
//                                List<Double> similarTasksDist = cosines.get(i);
//                                List<Double> temp = cosines.get(i);
//
//                                double weightedSum = 0.0;
//                                double numSum = 0.0;
//                                double deNumSum = 0.0;
//
//                                Collections.sort(temp, Collections.reverseOrder());
//
//                                for(int limit=0;limit<200 && limit<taskPublishers.size(); limit++){
//                                    Double userRatedTask = ratingList.get(similarTasksDist.indexOf(temp.get(limit)));
//                                    numSum = numSum + userRatedTask*temp.get(limit);
//                                    deNumSum = deNumSum + temp.get(limit);
//                                }
//                                if(deNumSum!=0){
//                                    weightedSum = numSum/deNumSum;
//                                }
//                                ratingPredict = new ArrayList<>();
//                                ratingPredict.add((double) taskPublisherId);
//                                ratingPredict.add(weightedSum);
//                                finalTasks.add(ratingPredict);
//                            }
//
//                            Collections.sort(finalTasks, new Comparator<List<Double>>() {
//                                @Override
//                                public int compare(List<Double> o1, List<Double> o2) {
//                                    if(o1.get(1)<o2.get(1))
//                                        return 1;
//                                    else if(o1.get(1)>o2.get(1))
//                                        return -1;
//                                    else
//                                        return 0;
//                                }
//                            });
//
//                            List<Tuple2<Long, Double>> temp2 = new ArrayList<>();
//                            for(int j=0;j<finalTasks.size();j++){
//                                temp2.add(new Tuple2<>(finalTasks.get(j).get(0).longValue(), finalTasks.get(j).get(1)));
//                            }
//
//                            weightedAvgPrediction.add(new Tuple2<>(userId, temp2));
//
//                        }
//                        return weightedAvgPrediction;
//                    }
//                }
//        );
//
//        prediction.saveAsTextFile("prediction");
//        System.out.println(prediction.collect().get(0));

        this.users = users;
        this.taskPublishers = taskPublishers;
        this.cosines = cosines;

    }

    public List<Long> getRecommendItems(Long taskPublisherId) {
        init();
        int index = taskPublishers.indexOf(taskPublisherId);
        List<Long> recommendItems = new ArrayList<>();
        if (index != -1) {
            List<Double> similarity = cosines.get(index);
            List<Double> temp = new ArrayList<>();
            temp.addAll(similarity);
            Collections.sort(temp, Collections.reverseOrder());
            for (int i = 0; i < temp.size(); i++) {
                if (temp.get(i) != 0) {
                    recommendItems.add(taskPublishers.get(similarity.indexOf(temp.get(i))));
                }
//                System.out.println(similarity.indexOf(temp.get(i)));
            }
        }
        return recommendItems;
    }
}
