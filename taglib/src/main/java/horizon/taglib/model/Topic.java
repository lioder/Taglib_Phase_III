package horizon.taglib.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 话题
 * <br>
 * created on 2018/05/16
 *
 * @author 巽
 **/
@Setter
@Getter
@Entity
@SuppressWarnings("unused")
public class Topic extends PO implements Serializable {
	private String description;

	public Topic() {
	}
}
