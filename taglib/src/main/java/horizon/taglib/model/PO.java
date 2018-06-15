package horizon.taglib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class PO {
	/**
	 * 持久化对象id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
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
