package horizon.taglib.utils;

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
	 * 通过DBSCAN算法得到点的聚类结果
	 *
	 * @param ts        点的集合
	 * @param eps       直达范围
	 * @param minNumber 核心点直达范围内最少点数
	 * @param <T>       点的类型，必须为实现了Distanceable接口的类型
	 * @return 算法得到的每一簇的集合，即同一簇的点存放在同一集合中，这些集合又放在一个集合中来作为返回值
	 */
	public static <T extends Distanceable<T>> List<List<T>> cluster(List<T> ts, double eps, int minNumber) {
		List<List<T>> ret = new ArrayList<>();
		double[][] distance = new double[ts.size()][ts.size()]; // 距离矩阵
		boolean[] isVisited = new boolean[ts.size()]; // 点是否已被扫描
		List<Set<Integer>> pNeighbours = new ArrayList<>();  // 各点的邻居集合（包括自己），即Set<index in ts>
		for (int i = 0; i < ts.size(); i++) {
			pNeighbours.add(new HashSet<>());
		}
		// 求距离矩阵和邻居集合
		for (int r = 0; r < ts.size(); r++) {
			distance[r][r] = 0;
			for (int c = r + 1; c < ts.size(); c++) {
				double dist = ts.get(r).distanceFrom(ts.get(c));
				distance[r][c] = dist;
				distance[c][r] = dist;
				if (dist <= eps) {
//					System.out.println(ts.get(r) + " " + ts.get(c) + " " + dist);
					pNeighbours.get(r).add(c);
					pNeighbours.get(c).add(r);
//					System.out.println(pNeighbours.get(r).size());
				}
			}
		}
		System.out.println(pNeighbours.size());
		for(Set<Integer> neighbours : pNeighbours){
			System.out.print(neighbours.size() + " ");
		}
		// 开始算法
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
			Point pointOutside = new Point(i, 1000 - i);
			Point pointInside = new Point(i * 0.1, 100 - i * 0.1);
			points.add(pointOutside);
			points.add(pointInside);
		}
		List<List<Point>> result = DBSCAN.cluster(points, 200, 5);
		System.out.println(result.size() + " clusters");
		for (List<Point> cluster : result) {
			for(Point point : cluster){
				System.out.print((point.getX()+point.getY() > 500) ? 1 : 0);
			}
			System.out.println();
		}
	}
}
