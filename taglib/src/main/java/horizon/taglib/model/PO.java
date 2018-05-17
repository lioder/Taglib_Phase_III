package horizon.taglib.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * 持久化对象父类
 * <br>
 * created on 2018/04/07
 *
 * @author 巽
 **/
@Setter
@Getter
@MappedSuperclass
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
		@JsonSubTypes.Type(value = IrregularTag.class, name = "IrregularTag"),
		@JsonSubTypes.Type(value = Log.class, name = "Log"),
		@JsonSubTypes.Type(value = RecTag.class, name = "RecTag"),
		@JsonSubTypes.Type(value = Tag.class, name = "Tag"),
		@JsonSubTypes.Type(value = TaskPublisher.class, name = "TaskPublisher"),
		@JsonSubTypes.Type(value = TaskWorker.class, name = "TaskWorker"),
		@JsonSubTypes.Type(value = User.class, name = "User")})
public class PO {
	/**
	 * 持久化对象id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	public PO(){}

	public PO(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PO po = (PO) o;
		return Objects.equals(id, po.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id);
	}

	@Override
	public String toString() {
		return "PO{" +
				"id=" + id +
				'}';
	}
}
