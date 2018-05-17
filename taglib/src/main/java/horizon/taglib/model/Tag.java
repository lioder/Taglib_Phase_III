package horizon.taglib.model;

import horizon.taglib.enums.TagType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 一条数据的一个标注
 * <br>
 * created on 2018/03/17
 *
 * @author 巽
 **/
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_discriminator", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("super")
public class Tag extends PO implements Serializable {
	/**
	 * 所属任务（发起者视角）id
	 */
	private Long taskPublisherId;
	/**
	 * 所属任务（众包工人视角）id
	 */
	private Long taskWorkerId;
	/**
	 * 数据文件名
	 */
	private String fileName;
	/**
	 * 用户id
	 */
	private Long userId;
	/**
	 * 注
	 */
	@OneToOne(cascade = {CascadeType.ALL})
	private TagDesc description;
	/**
	 * 颜色
	 */
	private String color;
	/**
	 * 标签类型
	 */
	private TagType tagType;

	public Tag() {
	}

	public Tag(Long taskPublisherId, Long taskWorkerId, String fileName, Long userId, TagDesc description, String color, TagType tagType) {
		this.taskPublisherId = taskPublisherId;
		this.taskWorkerId = taskWorkerId;
		this.fileName = fileName;
		this.userId = userId;
		this.description = description;
		this.color = color;
		this.tagType = tagType;
	}

	public Tag(Long tagId, Long taskPublisherId, Long taskWorkerId, String fileName, Long userId, TagDesc description, String color, TagType tagType) {
		super(tagId);
		this.taskPublisherId = taskPublisherId;
		this.taskWorkerId = taskWorkerId;
		this.fileName = fileName;
		this.userId = userId;
		this.description = description;
		this.color = color;
		this.tagType = tagType;
	}

	public Tag(Long id, Long taskPublisherId, Long taskWorkerId, String fileName, Long userId, String color) {
		super.setId(id);
		this.taskPublisherId = taskPublisherId;
		this.taskWorkerId = taskWorkerId;
		this.fileName = fileName;
		this.userId = userId;
		this.color = color;
	}

	@Override
	public String toString() {
		return "Tag{" +
				"id=" + this.getId() +
				", taskPublisherId=" + taskPublisherId +
				", taskWorkerId=" + taskWorkerId +
				", fileName='" + fileName + '\'' +
				", userId=" + userId +
				", tagType=" + tagType +
				'}';
	}
}
