package horizon.taglib.dao;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.User;
import horizon.taglib.utils.Criterion;

import java.util.List;

/**
 * 用户操作持久化对象接口
 * <br>
 * created on 2018/03/15
 *
 * @author 巽
 **/
public interface UserDao {
	/**
	 * 通过User的id查找用户对象
	 *
	 * @param id User的id
	 * @return User对象，查无此人则返回null
	 */
	User findById(Long id);

	/**
	 * 添加新User
	 *
	 * @param user 待添加的新User（没有id）
	 * @return SUCCESS：添加成功<br>
	 */
	ResultMessage add(User user);

	/**
	 * 删除用户
	 *
	 * @param id 待删除的User
	 * @return SUCCESS：删除成功<br>
	 * NOT_EXIST：待删除的User不存在
	 */
	ResultMessage delete(Long id);

	/**
	 * 更新User信息
	 *
	 * @param user 更新后的User信息
	 * @return SUCCESS：更新成功<br>
	 * NOT_EXIST：待更新的User不存在
	 */
	ResultMessage update(User user);

	/**
	 * 根据邮箱查询用户（唯一）
	 *
	 * @param email 用户的邮箱
	 * @return 查询到的用户，若无则为null
	 */
	User findByEmail(String email);

	/**
	 * 根据电话号码查询用户（唯一）
	 *
	 * @param phoneNumber 用户的电话号码
	 * @return 查询到的用户，若无则为null
	 */
	User findByPhoneNumber(String phoneNumber);

	/**
	 * 得到所有用户
	 *
	 * @return 包含所有用户的列表，若无则为空表
	 */
	List<User> getAllUser();

	/**
	 * 多重条件查询
	 *
	 * @param criteria 所有条件的集合
	 * @return 查询到的对象的集合
	 */
	List<User> multiQuery(List<Criterion> criteria);

	/**
	 * 获取新User的id（不触发id自增，即获取的id并未分配给某一User）
	 *
	 * @return 新id
	 */
	Long getNewId();
}
