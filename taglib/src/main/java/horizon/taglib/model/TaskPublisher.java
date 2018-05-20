package horizon.taglib.model;

import horizon.taglib.enums.TaskState;
import horizon.taglib.enums.TaskType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务（发布者视角），包含图片集等信息
 * <br>
 * created on 2018/03/17
 *
 * @author 巽
 **/
@Setter
@Getter
@Entity
@Table(name = "task_publisher")
public class TaskPublisher extends PO implements Serializable {
	/**
	 * 发布者id
	 */
	private Long userId;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 任务类型
	 */
	private TaskType taskType;
	/**
	 * 任务状态
	 */
	private TaskState taskState;
	/**
	 * 数据的id的集合<br>
	 * List&lt;imageId&gt;
	 */
	private ArrayList<String> images;
	/**
	 * 分类任务类型的标签集合
	 */
	private ArrayList<String> labels;
	/**
	 * 任务话题
	 */
	private ArrayList<String> topics;
	/**
	 * 任务总价
	 */
	private Double price;
	/**
	 * 单张图片需要的标注人数
	 */
	private Long numberPerPicture;
	/**
	 * 创建日期
	 */
	private String startDate;
	/**
	 * （计划）结束日期
	 */
	private String endDate;
	/**
	 * 好评率
	 */
	private Double rating;
	/**
	 * 热度原始计数
	 */
	private Double hotCount;
	/**
	 * 热度值，[0, 5]
	 */
	private Double hotRank;

	public TaskPublisher() {
	}

	/**
	 * add用
	 */
	public TaskPublisher(Long userId, String title, String description, TaskType taskType, List<String> images, List<String> labels, List<String> topics, Double price, Long numberPerPicture, String startDate, String endDate) {
		this.userId = userId;
		this.title = title;
		this.description = description;
		this.taskType = taskType;
		this.images = new ArrayList<>();
		if(images!=null) this.images.addAll(images);
		this.labels = new ArrayList<>();
		if(labels!=null) this.labels.addAll(labels);
		this.topics = new ArrayList<>();
		if(topics!=null) this.topics.addAll(topics);
		this.price = price;
		this.numberPerPicture = numberPerPicture;
		this.startDate = startDate;
		this.endDate = endDate;
		this.taskState = TaskState.SUBMITTED;
		this.rating = 0D;
		this.hotCount = 0D;
		this.hotRank = 0D;
	}

	/**
	 * update用
	 */
	public TaskPublisher(Long id, Long userId, String title, String description, TaskType taskType, TaskState taskState, List<String> images, List<String> labels, List<String> topics, Double price, Long numberPerPicture, String startDate, String endDate, Double rating, Double hotCount, Double hotRank) {
		super(id);
		this.userId = userId;
		this.title = title;
		this.description = description;
		this.taskType = taskType;
		this.taskState = taskState;
		this.images = new ArrayList<>();
		if(images!=null) this.images.addAll(images);
		this.labels = new ArrayList<>();
		if(labels!=null) this.labels.addAll(labels);
		this.topics = new ArrayList<>();
		if(topics!=null) this.topics.addAll(topics);
		this.price = price;
		this.numberPerPicture = numberPerPicture;
		this.startDate = startDate;
		this.endDate = endDate;
		this.rating = rating;
		this.hotCount = hotCount;
		this.hotRank = hotRank;
	}

	@Override
	public String toString() {
		return "TaskPublisher{" +
				"id=" + this.getId() +
				", userId=" + userId +
				", title='" + title + '\'' +
				", startDate='" + startDate + '\'' +
				", endDate='" + endDate + '\'' +
				'}';
	}
}
