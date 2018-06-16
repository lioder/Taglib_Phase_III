package horizon.taglib.dao;

import horizon.taglib.enums.TagType;
import horizon.taglib.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 标注操作持久化对象接口
 * <br>
 * created on 2018/03/20
 *
 * @author 巽
 **/
public interface TagDao extends JpaRepository<Tag, Long>, BaseRepository<Tag, Long> {
	/**
	 * 通过taskPublisherId和tagType查找Tag
	 *
	 * @param taskPublisherId TaskPublisher的id
	 * @param tagType Tag类型
	 * @return 所有符合条件的Tag的List
	 */
	List<Tag> findByTaskPublisherIdAndTagType(Long taskPublisherId, TagType tagType);
	/**
	 * 通过taskPublisherId和tagType查找专家标的Tag
	 *
	 * @param taskPublisherId TaskPublisher的id
	 * @param tagType Tag类型
	 * @return 所有符合条件的Tag的List
	 */
	List<Tag> findByTaskPublisherIdAndTagTypeAndIsProfessionalTag(Long taskPublisherId, TagType tagType, Boolean isProfessionalTag);
}
