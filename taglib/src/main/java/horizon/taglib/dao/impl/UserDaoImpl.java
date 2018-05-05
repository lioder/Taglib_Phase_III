package horizon.taglib.dao.impl;

import horizon.taglib.dao.DaoHelper;
import horizon.taglib.dao.UserDao;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.UserType;
import horizon.taglib.model.User;
import horizon.taglib.utils.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作用户持久化对象实现类
 * <br>
 * created on 2018/03/15
 *
 * @author 巽
 **/
@Repository
public class UserDaoImpl implements UserDao {
	private DaoHelper<User> daoHelper;

	@Autowired
	public UserDaoImpl(DaoHelper<User> daoHelper) {
		this.daoHelper = daoHelper;
		daoHelper.initClass(User.class);
		this.initAdmin();
	}

	private void initAdmin(){
		if(daoHelper.exactlyQuery("email", "wsywlioder@gmail.com") == null && daoHelper.exactlyQuery("phoneNumber", "13866666666") == null){
			User admin = new User("wsywLioder", "666666", "13866666666", "wsywLioder@gmail.com", UserType.ADMIN);
			daoHelper.add(admin);
		}
	}

	@Override
	public User findById(Long id) {
		return daoHelper.exactlyQuery("id", id);
	}

	/**
	 * 添加新User
	 *
	 * @param user 待添加的新User（没有id）
	 * @return SUCCESS：添加成功<br>
	 */
	@Override
	public ResultMessage add(User user) {
		return daoHelper.add(user);
	}

	/**
	 * 删除用户
	 *
	 * @param id 待删除的User
	 * @return SUCCESS：删除成功<br>
	 * NOT_EXIST：待删除的User不存在
	 */
	@Override
	public ResultMessage delete(Long id) {
		return daoHelper.delete(id);
	}

	/**
	 * 更新User信息
	 *
	 * @param user 更新后的User信息
	 * @return SUCCESS：更新成功<br>
	 * NOT_EXIST：待更新的User不存在
	 */
	@Override
	public ResultMessage update(User user) {
		return daoHelper.update(user);
	}

	/**
	 * 根据邮箱查询用户（唯一）
	 *
	 * @param email 用户的邮箱
	 * @return 查询到的用户，若无则为null
	 */
	@Override
	public User findByEmail(String email) {
		return daoHelper.exactlyQuery("email", email);
	}

	/**
	 * 根据电话号码查询用户（唯一）
	 *
	 * @param phoneNumber 用户的电话号码
	 * @return 查询到的用户，若无则为null
	 */
	@Override
	public User findByPhoneNumber(String phoneNumber) {
		return daoHelper.exactlyQuery("phoneNumber", phoneNumber);
	}

	/**
	 * 得到所有用户
	 *
	 * @return 包含所有用户的列表，若无则为空表
	 */
	@Override
	public List<User> getAllUser() {
		return daoHelper.multiQuery(new ArrayList<>());
	}

	/**
	 * 多重条件查询
	 *
	 * @param criteria 所有条件的集合
	 * @return 查询到的对象的集合
	 */
	@Override
	public List<User> multiQuery(List<Criterion> criteria) {
		return daoHelper.multiQuery(criteria);
	}

	/**
	 * 获取新User的id（不触发id自增，即获取的id并未分配给某一User）
	 *
	 * @return 新id
	 */
	@Override
	public Long getNewId() {
		return daoHelper.getNewId();
	}
}
