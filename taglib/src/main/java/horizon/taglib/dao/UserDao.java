package horizon.taglib.dao;

import horizon.taglib.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户操作持久化对象接口
 * <br>
 * created on 2018/03/15
 *
 * @author 巽
 **/
public interface UserDao extends JpaRepository<User, Long>, BaseRepository<User, Long> {
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
}
