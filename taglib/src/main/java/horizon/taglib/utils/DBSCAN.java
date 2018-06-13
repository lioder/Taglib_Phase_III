package horizon.taglib.utils;

import horizon.taglib.model.RecTag;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.springframework.util.StopWatch;

import java.sql.*;
import java.util.*;

/**
 * DBSCAN算法实现
 * <br>
 * created on 2018/06/03
 *
 * @author 巽
 **/
public class DBSCAN {
//	private static StopWatch stopWatch = new StopWatch("DBSCAN");

	/**
	 * 通过DBSCAN算法得到点的聚类结果，通过k=4的KNN分布图自动推算eps，通过各点邻居数量期望自动推算minNumber
	 *
	 * @param ts  点的集合
	 * @param <T> 点的类型，必须为实现了Distanceable接口的类型
	 * @return 算法得到的每一簇的集合，即同一簇的点存放在同一集合中，这些集合又放在一个集合中来作为返回值
	 */
	public static <T extends Distanceable<T>> List<List<T>> cluster(List<T> ts) {
//		stopWatch.start("create distance matrix");
		// 求距离矩阵
		double[][] distance = new double[ts.size()][ts.size()]; // 距离矩阵
		for (int r = 0; r < ts.size(); r++) {
			distance[r][r] = 0;
			for (int c = r + 1; c < ts.size(); c++) {
				Double dist = ts.get(r).distanceFrom(ts.get(c));
				if (dist == null) {
					distance[r][c] = Double.POSITIVE_INFINITY;
					distance[c][r] = Double.POSITIVE_INFINITY;
				} else {
					distance[r][c] = dist;
					distance[c][r] = dist;
				}
			}
		}

//		stopWatch.stop();
//		stopWatch.start("prepare points");
		// 用k=4的k-dist函数图像推算eps，采用多项式拟合函数
		double[] kth_distance = new double[ts.size()];
		WeightedObservedPoints weightedObservedPoints = new WeightedObservedPoints();
		for (int i = 0; i < ts.size(); i++) {
			double[] m = new double[ts.size()];
			System.arraycopy(distance[i], 0, m, 0, ts.size());
			Arrays.sort(m);
			if (m.length >= 4) {
				kth_distance[i] = m[4];    // k=4
			} else {
				kth_distance[i] = m[m.length - 1];
			}
		}
		Arrays.sort(kth_distance);

		int length = (int) (kth_distance.length * 0.9); // 去除最远端的离群点
		for (int i = 0; i < length; i++) {
			weightedObservedPoints.add(i, kth_distance[i]);

			System.out.print(String.format("%.3f", kth_distance[i]) + " ");
		}
		System.out.println();
//		stopWatch.stop();
//		stopWatch.start("fit");

		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(5);    // 用5阶多项式拟合:f(x)=ax^5+bx^4+cx^3+dx^2+ex+f
		double[] fitFuncParams = fitter.fit(weightedObservedPoints.toList());  // {f, e, d, c, b, a}

//		stopWatch.stop();
//		stopWatch.start("real cluster");

		double a = fitFuncParams[5], b = fitFuncParams[4], c = fitFuncParams[3],
				d = fitFuncParams[2], e = fitFuncParams[1], f = fitFuncParams[0];
		// 依经验选择x值
		double x = length * 0.7;

		System.out.println("x=" + x);

		double eps = a * x * x * x * x * x + b * x * x * x * x + c * x * x * x + d * x * x + e * x + f; // eps=f(x)
		if (eps <= 0) { // 过拟合：经验补救
			eps = 0.02;
		}

//		for (int i = 0; i < kth_distance.length; i++) {
//			System.out.print(String.format("%.3f", a * i * i * i * i * i + b * i * i * i * i + c * i * i * i + d * i * i + e * i + f) + " ");
//		}
//		System.out.println();
		for (int i = fitFuncParams.length - 1; i >= 0; i--) {
			System.out.print(String.format("%.9f", fitFuncParams[i]) + " ");
		}
		System.out.println();

		// 求邻居集合
		List<Set<Integer>> pNeighbours = new ArrayList<>();  // 各点的邻居集合（包括自己），即Set<index in ts>
		for (int i = 0; i < ts.size(); i++) {
			pNeighbours.add(new HashSet<>());
		}
		for (int r = 0; r < ts.size(); r++) {
			for (int co = r + 1; co < ts.size(); co++) {
				double dist = distance[r][co];
				if (dist <= eps) {
					pNeighbours.get(r).add(co);
					pNeighbours.get(co).add(r);
				}
			}
		}

		// 推算minNumber
		int nSum = 0;
		for (Set<Integer> neighbours : pNeighbours) {
			nSum += neighbours.size();

			System.out.print(neighbours.size() + " ");
		}

		System.out.println();

		int minNumber = nSum / ts.size() - 1;
		if (minNumber < 1) {
			minNumber = 1;
		}

//		for (double ret : fitFuncParams) {
//			System.out.print(ret + " ");
//		}
//		System.out.println();
		System.out.println("eps: " + eps + ", minNumber: " + minNumber);

		return cluster(ts, minNumber, pNeighbours);
	}

	/**
	 * 通过DBSCAN算法得到点的聚类结果
	 *
	 * @param ts        点的集合
	 * @param eps       直达范围
	 * @param minNumber 核心点直达范围内最少点数
	 * @param <T>       点的类型，必须为实现了Distanceable接口的类型
	 * @return 算法得到的每一簇的集合，即同一簇的点存放在同一集合中，这些集合又放在一个集合中来作为返回值
	 */
	public static <T extends Distanceable<T>> List<List<T>> cluster(List<T> ts, double eps, int minNumber) {
		List<Set<Integer>> pNeighbours = new ArrayList<>();  // 各点的邻居集合（包括自己），即Set<index in ts>
		for (int i = 0; i < ts.size(); i++) {
			pNeighbours.add(new HashSet<>());
		}
		// 求邻居集合
		for (int r = 0; r < ts.size(); r++) {
			for (int c = r + 1; c < ts.size(); c++) {
				Double dist = ts.get(r).distanceFrom(ts.get(c));
				if (dist != null && dist <= eps) {
					pNeighbours.get(r).add(c);
					pNeighbours.get(c).add(r);
				}
			}
		}
		return cluster(ts, minNumber, pNeighbours);
	}

	private static <T extends Distanceable<T>> List<List<T>> cluster(List<T> ts, int minNumber, List<Set<Integer>> pNeighbours) {
		List<List<T>> ret = new ArrayList<>();
		boolean[] isVisited = new boolean[ts.size()]; // 点是否已被扫描
		for (int r = 0; r < ts.size(); r++) {
			if (!isVisited[r]) {
				isVisited[r] = true;
				if (pNeighbours.get(r).size() >= minNumber) {
					List<T> cluster = new ArrayList<>();    // 创建新簇
					// expand cluster
					cluster.add(ts.get(r));
					List<Integer> neighbourIndexes = new ArrayList<>(pNeighbours.get(r));
					for (int i = 0; i < neighbourIndexes.size(); i++) {
						int pIndex = neighbourIndexes.get(i);
						if (!isVisited[pIndex]) {
							isVisited[pIndex] = true;
							if (pNeighbours.get(pIndex).size() >= minNumber) {
								neighbourIndexes.addAll(pNeighbours.get(pIndex));  // join
							}
							cluster.add(ts.get(pIndex));
						}
					}
					ret.add(cluster);
				}
			}
		}
		return ret;
	}

	public static void main(String[] args) {
//		List<Point> points = new ArrayList<>();
//		for (int i = 0; i < 1000; i++) {
//			Point pointRight = new Point(i, 1000 - i);
//			Point pointLeft = new Point(-i, i - 1000);
//			points.add(pointRight);
//			points.add(pointLeft);
//		}
//		List<List<Point>> result = DBSCAN.cluster(points);
////		List<List<Point>> result = DBSCAN.cluster(points, 200, 5);
//		System.out.println(result.size() + " clusters");
//		for (List<Point> cluster : result) {
//			System.out.println(cluster.size() + "points");
//		}

		String url = "jdbc:mysql://localhost:3306/taglibdatabase";
		String username = "root";
		String password = "admin";
		String sql = "SELECT * FROM tag WHERE task_publisher_id = 2";
//		stopWatch.start("query");

		try (Connection conn = DriverManager.getConnection(url, username, password);
		     PreparedStatement ps = conn.prepareStatement(sql)) {
			conn.setAutoCommit(false);
			ps.addBatch();
			ResultSet ret = ps.executeQuery();

//			stopWatch.stop();
//			stopWatch.start("add Tag");

			List<RecTag> tags = new ArrayList<>();
			while (ret.next()) {
				RecTag recTag = new RecTag();
				recTag.setId(ret.getLong("id"));
				recTag.setFileName(ret.getString("file_name"));
				recTag.setTaskWorkerId(ret.getLong("task_worker_id"));
				recTag.setStart(new Point(ret.getDouble("start_x"), ret.getDouble("start_y")));
				recTag.setEnd(new Point(ret.getDouble("end_x"), ret.getDouble("end_y")));
				tags.add(recTag);
//				System.out.println("id=" + recTag.getId()
//						+ ", fileName=" + recTag.getFileName()
//						+ ", taskWorkerId=" + recTag.getTaskWorkerId());
			}
			System.out.println("Tag count: " + tags.size());

//			stopWatch.stop();

			List<List<RecTag>> result = DBSCAN.cluster(tags);
//			List<List<RecTag>> result = DBSCAN.cluster(tags, 0.032062082708797665, 3);
//			List<List<RecTag>> result = DBSCAN.cluster(tags, 0.05, 3);

//			stopWatch.stop();

			System.out.println(result.size() + " clusters");
			int cnt = 0;
			for (List<RecTag> cluster : result) {
				cnt += cluster.size();
				System.out.print(cluster.size() + ": ");
				for (RecTag recTag : cluster) {
					System.out.print(recTag.getId() + " ");
				}
				System.out.println();
			}
			System.out.println("count " + cnt + " points");
//			System.out.println(stopWatch.prettyPrint());
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
