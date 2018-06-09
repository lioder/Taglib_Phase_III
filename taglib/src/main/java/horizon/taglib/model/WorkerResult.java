package horizon.taglib.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 工人标注任务结果
 * <br>
 * created on 2018/06/08
 *
 * @author 巽
 **/
@Setter
@Getter
@Entity
@Table(name = "worker_result")
public class WorkerResult implements Serializable {
	/**
	 * TaskWorker的id做主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long taskWorkerId;
	/**
	 * 正确的Tag的id（来自数据库）的集合
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "worker_result_correct")
	@Column(name = "correct_tag_id")
	private List<Long> correctTagIds;
	/**
	 * 错误的Tag的id（来自数据库）的集合
	 */
	@ElementCollection()
	@Fetch(FetchMode.SUBSELECT)
	@CollectionTable(name = "worker_result_wrong")
	@Column(name = "wrong_tag_id")
	private List<Long> wrongTagIds;
	/**
	 * 未标的Tag的id（来自JSON）的集合
	 */
	@ElementCollection()
	@Fetch(FetchMode.SUBSELECT)
	@CollectionTable(name = "worker_result_miss")
	@Column(name = "miss_tag_id")
	private List<Long> missTagIds;

	public WorkerResult(){}

	public WorkerResult(Long taskWorkerId, List<Long> correctTagIds, List<Long> wrongTagIds, List<Long> missTagIds) {
		this.taskWorkerId = taskWorkerId;
		this.correctTagIds = correctTagIds;
		this.wrongTagIds = wrongTagIds;
		this.missTagIds = missTagIds;
	}
}
