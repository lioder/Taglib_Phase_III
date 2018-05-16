package horizon.taglib.dao;

import horizon.taglib.utils.Criterion;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

/**
 * 持久层父接口实现
 * <br>
 * created on 2018/05/16
 *
 * @author 巽
 **/
@NoRepositoryBean
@SuppressWarnings("unused")
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {
	/**
	 * 解决Spring报错的构造函数
	 */
	public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
		super(entityInformation, em);
	}

	/**
	 * 给Factory用的构造函数
	 */
	public BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
	}

	/**
	 * 多重条件查询
	 *
	 * @param criteria 所有条件的集合
	 * @return 查询到的对象的集合
	 */
	@Override
	public List<T> multiQuery(List<Criterion> criteria) {
		System.out.println("Not implement yet!");
		return null;
	}
}
