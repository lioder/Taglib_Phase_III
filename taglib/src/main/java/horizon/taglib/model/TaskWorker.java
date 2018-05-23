package horizon.taglib.model;

import horizon.taglib.enums.TaskState;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务（众包工人视角），即众包工人接的各任务的情况
 * <br>
 * created on 2018/04/04
 *
 * @author 巽
 **/
@Setter
@Getter
@Entity
@Table(name = "task_worker")
public class TaskWorker extends PO implements Serializable{
	/**
	 * 所属任务（发起者视角）id
	 */
	private Long taskPublisherId;
	/**
	 * 所属用户id
	 */
	private Long userId;
	/**
	 * 用户任务总收入
	 */
	private Double price;
	/**
	 * 用户接任务的状态
	 */
	private TaskState taskState;
	/**
	 * 接受时间
	 */
	private String startDate;
	/**
	 * 完成时间，未完成时为null
	 */
	private String endDate;
	/**
	 * 用户为该任务做的标注<br>
	 * List&lt;TagId&gt;
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "task_worker_tag")
	@Column(name = "tagId")
	private List<Long> tags;
	/**
	 * 用户评价
	 */
	private Integer rating;

	@SuppressWarnings("unused")
	public TaskWorker(){}

	/**
	 * add用
	 */
	public TaskWorker(Long taskPublisherId, Long userId, Double price, String startDate) {
		this.taskPublisherId = taskPublisherId;
		this.userId = userId;
		this.price = price;
		this.startDate = startDate;
		this.taskState = TaskState.PROCESSING;
		this.endDate = null;
		this.tags = new ArrayList<>();
		this.rating = 0;
	}

	/**
	 * update用
	 */
	public TaskWorker(Long id, Long taskPublisherId, Long userId, Double price, String startDate, List<Long> tags, Integer rating) {
		super(id);
		this.taskPublisherId = taskPublisherId;
		this.userId = userId;
		this.price = price;
		this.startDate = startDate;
		this.taskState = TaskState.PROCESSING;
		this.endDate = null;
		this.tags = tags;
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "TaskWorker{" +
				"id=" + this.getId() +
				", taskPublisherId=" + taskPublisherId +
				", userId=" + userId +
				", taskState=" + taskState +
				", startDate='" + startDate + '\'' +
				", endDate='" + endDate + '\'' +
				'}';
	}
}
