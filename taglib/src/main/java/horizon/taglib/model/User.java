package horizon.taglib.model;

import horizon.taglib.enums.Level;
import horizon.taglib.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户持久化对象
 * <br>
 * created on 2018/03/15
 *
 * @author 巽
 **/
@Setter
@Getter
@Entity
public class User extends PO implements Serializable {
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 手机号
	 */
	private String phoneNumber;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 用户类型
	 */
	private UserType userType;
	/**
	 * 用户的任务清单，对于Publisher而言为所有自己发布的任务id，对于Worker而言为所有已接受的任务id，未接受的任务id不在其中<br>
	 * List&lt;Task(Publisher/Worker)Id&gt;
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "user_task_worker")
	@Column(name = "taskId")
	private List<Long> myTasks;
	/**
	 * 积分
	 */
	private Long points;
	/**
	 * 头像文件名
	 */
	private String avatar;
	/**
	 * 等级
	 */
	private Level level;
	/**
	 * 经验值
	 */
	private Long exp;
	/**
	 * 准确率
	 */
	private Double accuracyRate;
	/**
	 * 准时率
	 */
	private Double punctualityRate;
	/**
	 * 客户满意度
	 */
	private Double satisfactionRate;
	/**
	 * 今天是否已签到（每日北京零点重置）
	 */
	private Boolean isAttendant;
	/**
	 * 话题集及其贡献因子
	 * <topic, factor>
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "user_topic_factor")
	@MapKeyColumn(name = "topic")
	@Column(name = "factor")
	private Map<String, Double> topics;

	public User() {
	}

	public User(String username, String password, String phoneNumber, String email, UserType userType) {
		this.username = username;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.userType = userType;
		this.myTasks = new ArrayList<>();
		this.points = 0L;
		this.avatar = "default_avatar.png";
		this.level = Level.LEVEL_ONE;
		this.exp = 0L;
		this.setAccuracyRate(0D);
		this.setPunctualityRate(0D);
		this.setSatisfactionRate(0D);
		this.isAttendant = false;
		topics = new HashMap<>();
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + this.getId() +
				", username='" + username + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", email='" + email + '\'' +
				'}';
	}
}
