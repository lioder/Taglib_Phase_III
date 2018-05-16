package horizon.taglib.dao;

import horizon.taglib.utils.Criterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * 持久层父接口，用来放一些公用接口
 * <br>
 * created on 2018/05/16
 *
 * @author 巽
 **/
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
	/**
	 * 多重条件查询
	 *
	 * @param criteria 所有条件的集合
	 * @return 查询到的对象的集合
	 */
	List<T> multiQuery(List<Criterion> criteria);
}
