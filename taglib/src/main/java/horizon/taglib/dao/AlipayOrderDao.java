package horizon.taglib.dao;

import horizon.taglib.model.AlipayOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlipayOrderDao extends JpaRepository<AlipayOrder, Long> {

    AlipayOrder findByOrderNo(String orderNo);

    List<AlipayOrder> findByUserId(Long userId);

}
