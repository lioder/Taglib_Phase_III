package horizon.taglib.dao;

import horizon.taglib.model.CenterTag;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 标准标注操作持久化对象接口
 * <br>
 * created on 2018/06/09
 *
 * @author 巽
 **/
public interface CenterTagDao extends JpaRepository<CenterTag, Long>, BaseRepository<CenterTag, Long> {
}
