package horizon.taglib.utils;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.*;

/**
 * DBSCAN算法实现
 * <br>
 * created on 2018/06/03
 *
 * @author 巽
 **/
public class DBSCAN {
	/**
	 * 通过DBSCAN算法得到点的聚类结果，通过k=4的KNN分布图自动推算eps，通过各点邻居数量期望自动推算minNumber
	 *
	 * @param ts  点的集合
	 * @param <T> 点的类型，必须为实现了Distanceable接口的类型
	 * @return 算法得到的每一簇的集合，即同一簇的点存放在同一集合中，这些集合又放在一个集合中来作为返回值
	 */
	public static <T extends Distanceable<T>> List<List<T>> cluster(List<T> ts) {
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
		for (int i = 0; i < kth_distance.length; i++) {
			weightedObservedPoints.add(i, kth_distance[i]);
		}
		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(4);    // 用4阶多项式拟合:f(x)=ax^4+bx^3+cx^2+dx+e
		double[] fitFuncParams = fitter.fit(weightedObservedPoints.toList());  // {e, d, c, b, a}
		double a = fitFuncParams[4], b = fitFuncParams[3], c = fitFuncParams[2], d = fitFuncParams[1], e = fitFuncParams[0];
		// 求降序排序下第一个拐点的x值
		double x;
		if (a == 0) {
			x = -c / (3 * b);
		} else {
			x = (-6 * b + Math.sqrt(36 * b * b - 96 * a * c)) / (24 * a);
		}
		if (x >= ts.size() || x < 0) {  // 不幸越界：简单估计，取2/3处的值
			x = kth_distance[ts.size() * 2 / 3];
		}
		x++;    // 玄学修正
//		System.out.println("x=" + x);
		double eps = a * x * x * x * x + b * x * x * x + c * x * x + d * x + e; // eps=f(x)
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
//			System.out.print(neighbours.size() + " ");
		}
//		System.out.println();
		int minNumber = nSum / ts.size() + 1;
//		System.out.println("eps=" + eps);
//		System.out.println("minNumber=" + minNumber);
//		for (double ret : fitFuncParams) {
//			System.out.print(ret + " ");
//		}
//		System.out.println();
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
		List<Point> points = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			Point pointRight = new Point(i, 1000 - i);
			Point pointLeft = new Point(-i, i - 1000);
			points.add(pointRight);
			points.add(pointLeft);
		}
		List<List<Point>> result = DBSCAN.cluster(points);
//		List<List<Point>> result = DBSCAN.cluster(points, 200, 5);
		System.out.println(result.size() + " clusters");
		for (List<Point> cluster : result) {
			System.out.println(cluster.size() + "points");
		}
	}
}
