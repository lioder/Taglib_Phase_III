package horizon.taglib.dao;

import horizon.taglib.utils.Criterion;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jpa.HibernateEntityManager;
import org.hibernate.type.StandardBasicTypes;
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
@SuppressWarnings({"unused", "unchecked"})
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {
	private EntityManager entityManager;
	private Class<T> type;
	/**
	 * 解决Spring报错的构造函数
	 */
	public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
		super(entityInformation, em);
		this.entityManager = em;
		this.type = entityInformation.getJavaType();
	}

	/**
	 * 给Factory用的构造函数
	 */
	public BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
		this.entityManager = em;
		this.type = domainClass;
	}

	/**
	 * 多重条件查询
	 *
	 * @param criteria 所有条件的集合
	 * @return 查询到的对象的集合
	 */
	@Override
	public List<T> multiQuery(List<Criterion> criteria) {
		Session session = ((HibernateEntityManager) entityManager).getSession();
		Criteria toQuery = session.createCriteria(type);
		for (Criterion criterion : criteria) {
			toQuery.add(buildCriterion(criterion));
		}
		toQuery.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return (List<T>) toQuery.list();
	}

	/**
	 * 完全匹配，多用于查询字段为某个值的所有T
	 *
	 * @param field 要查询的字段，字段类型为集合类型(extends Collection)时调用Collection.contains(value)进行匹配
	 * @param value 要匹配的值
	 * @return 查询到的所有TaskPublisher的集合
	 */
	@Override
	public List<T> fullyQuery(String field, Object value) {
		Session session = ((HibernateEntityManager) entityManager).getSession();
		Criteria toQuery = session.createCriteria(type);
		toQuery.add(Restrictions.eq(field, value));
		toQuery.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return (List<T>) toQuery.list();
	}

	private org.hibernate.criterion.Criterion buildCriterion(Criterion criterion) {
		switch (criterion.getQueryMode()) {
			case FULL:
				return Restrictions.eq(criterion.getField(), criterion.getValue());
			case FUZZY:
				if (criterion.getField().toLowerCase().equals("id")) {
					return Restrictions.sqlRestriction("CAST({alias}.id AS CHAR) like ?", criterion.getValue(),
							StandardBasicTypes.STRING);
				}
				else {
					return Restrictions.like(criterion.getField(), criterion.getValue());
				}
			case RANGE:
				return Restrictions.between(criterion.getField(), criterion.getMin(), criterion.getMax());
			case OR:
				return buildCriterion(criterion.getCriterion1(), criterion.getCriterion2());
			default:
				System.out.println("错误：出现了未识别的QueryMode！");
				return null;
		}
	}

	private org.hibernate.criterion.Criterion buildCriterion(Criterion criterion1, Criterion criterion2) {
		return Restrictions.or(buildCriterion(criterion1), buildCriterion(criterion2));
	}
}
