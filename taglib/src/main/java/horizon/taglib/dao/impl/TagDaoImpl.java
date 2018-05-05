package horizon.taglib.dao.impl;

import horizon.taglib.dao.DaoHelper;
import horizon.taglib.dao.TagDao;
import horizon.taglib.enums.QueryMode;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.Tag;
import horizon.taglib.utils.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作标注持久化对象实现类
 * <br>
 * created on 2018/03/20
 *
 * @author 巽
 **/
@Repository
public class TagDaoImpl implements TagDao {
	private DaoHelper<Tag> daoHelper;

	@Autowired
	public TagDaoImpl(DaoHelper<Tag> daoHelper) {
		this.daoHelper = daoHelper;
		daoHelper.initClass(Tag.class);
	}

	/**
	 * 增加Tag
	 *
	 * @param tag 待添加的Tag
	 * @return SUCCESS：添加成功
	 */
	@Override
	public ResultMessage add(Tag tag) {
		return daoHelper.add(tag);
	}

	/**
	 * 删除Tag
	 *
	 * @param id 待删除的Tag的id
	 * @return SUCCESS：删除成功<br>
	 * NOT_EXIST：待删除的Tag不存在
	 */
	@Override
	public ResultMessage delete(Long id) {
		return daoHelper.delete(id);
	}

	/**
	 * 更新Tag
	 *
	 * @param tag 待更新的Tag
	 * @return SUCCESS：更新成功<br>
	 * NOT_EXIST：待更新的Tag不存在
	 */
	@Override
	public ResultMessage update(Tag tag) {
		return daoHelper.update(tag);
	}

	/**
	 * 根据图片名称拿到所有关于它的Tag
	 *
	 * @param name 图片名称
	 * @return 该图片的所有Tag的List，若无则返回空List
	 */
	@Override
	public List<Tag> getAllTagByImageName(String name) {
		return daoHelper.fullyQuery("fileName", name);
	}

	/**
	 * 根据图片名称和任务id拿到所有Tag
	 *
	 * @param fileName 图片名称
	 * @param taskId   任务id
	 * @return 所有查到的Tag的ArrayList， 若无则返回空的ArrayList
	 */
	@Override
	public List<Tag> getByImageNameAndTaskId(String fileName, Long taskId) {
		ArrayList<Criterion> criteria = new ArrayList<>();
		criteria.add(new Criterion<>("fileName", fileName, QueryMode.FULL));
		criteria.add(new Criterion<>("taskPublisherId", taskId, QueryMode.FULL));
		return daoHelper.multiQuery(criteria);
	}

	/**
	 * 根据任务情况（众包工人视角）id和图片名称拿到所有Tag
	 *
	 * @param taskWorkerId 任务id
	 * @param fileName     图片名称
	 * @return 所有符合条件的Tag的列表
	 */
	@Override
	public List<Tag> getByTaskWorkerIdAndFileName(Long taskWorkerId, String fileName) {
		ArrayList<Criterion> criteria = new ArrayList<>();
		criteria.add(new Criterion<>("taskWorkerId", taskWorkerId, QueryMode.FULL));
		criteria.add(new Criterion<>("fileName", fileName, QueryMode.FULL));
		return daoHelper.multiQuery(criteria);
	}

	/**
	 * 获取新Tag的id（不触发id自增，即获取的id并未分配给某个Tag）
	 *
	 * @return 新id
	 */
	@Override
	public Long getNewId() {
		return daoHelper.getNewId();
	}

	/**
	 * 通过id得到tag
	 *
	 * @param id Tag的id
	 * @return Tag对象，查无此Tag则返回null
	 */
	@Override
	public Tag getById(Long id) {
		return daoHelper.exactlyQuery("id", id);
	}
}
