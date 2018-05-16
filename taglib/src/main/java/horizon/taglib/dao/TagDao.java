package horizon.taglib.dao;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.Tag;

import java.util.List;

/**
 * 标注操作持久化对象接口
 * <br>
 * created on 2018/03/20
 *
 * @author 巽
 **/
public interface TagDao {
	/**
	 * 增加Tag
	 *
	 * @param tag 待添加的Tag（没有id）
	 * @return SUCCESS：添加成功
	 */
	ResultMessage add(Tag tag);

	/**
	 * 删除Tag
	 *
	 * @param id 待删除的Tag的id
	 * @return SUCCESS：删除成功<br>
	 * NOT_EXIST：待删除的Tag不存在
	 */
	ResultMessage delete(Long id);

	/**
	 * 更新Tag
	 *
	 * @param tag 待更新的Tag
	 * @return SUCCESS：更新成功<br>
	 * NOT_EXIST：待更新的Tag不存在
	 */
	ResultMessage update(Tag tag);

	/**
	 * 根据图片名称拿到所有关于它的Tag
	 *
	 * @param name 图片名称
	 * @return 该图片的所有Tag的List，若无则返回空List
	 */
	List<Tag> getAllTagByImageName(String name);

	/**
	 * 根据图片名称和任务id拿到所有Tag
	 *
	 * @param fileName 图片名称
	 * @param taskId   任务id
	 * @return 所有查到的Tag的ArrayList， 若无则返回空的ArrayList
	 */
	List<Tag> getByImageNameAndTaskId(String fileName, Long taskId);

	/**
	 * 根据任务情况（众包工人视角）id和图片名称拿到所有Tag
	 *
	 * @param taskWorkerId 任务id
	 * @param fileName     图片名称
	 * @return 所有符合条件的Tag的列表
	 */
	List<Tag> getByTaskWorkerIdAndFileName(Long taskWorkerId, String fileName);

	/**
	 * 获取新Tag的id（不触发id自增，即获取的id并未分配给某个Tag）
	 *
	 * @return 新id
	 */
	Long getNewId();

	/**
	 * 通过id得到tag
	 *
	 * @param id Tag的id
	 * @return Tag对象，查无此Tag则返回null
	 */
	Tag getById(Long id);
}