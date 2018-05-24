package horizon.taglib.dao;

import horizon.taglib.model.AlipayOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlipayOrderDao extends JpaRepository<AlipayOrder, Long> {

    AlipayOrder findByOrderNo(String orderNo);

}
