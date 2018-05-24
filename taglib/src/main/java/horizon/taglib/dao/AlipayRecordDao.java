package horizon.taglib.dao;

import horizon.taglib.model.AlipayRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlipayRecordDao extends JpaRepository<AlipayRecord, Long> {

    AlipayRecord findByOrderNo(String orderNo);

}
