package horizon.taglib.config;

import horizon.taglib.dao.BaseRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * BaseRepository工厂实例
 * <br>
 * created on 2018/05/16
 *
 * @author 巽
 **/
@SuppressWarnings("unused")
public class BaseRepositoryFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable>
		extends JpaRepositoryFactoryBean<R, T, I> {
	/**
	 * 继承的默认构造函数
	 */
	public BaseRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
	}

	@SuppressWarnings("rawtypes")
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
		return new BaseRepositoryFactory(em);
	}

	private static class BaseRepositoryFactory<T, I extends Serializable>
			extends JpaRepositoryFactory {

		private final EntityManager em;

		private BaseRepositoryFactory(EntityManager em) {
			super(em);
			this.em = em;
		}

		@SuppressWarnings("unchecked")
		protected Object getTargetRepository(RepositoryMetadata metadata) {
			return new BaseRepositoryImpl<>(
					(Class<T>) metadata.getDomainType(), em);
		}

		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return BaseRepositoryImpl.class;
		}
	}
}
