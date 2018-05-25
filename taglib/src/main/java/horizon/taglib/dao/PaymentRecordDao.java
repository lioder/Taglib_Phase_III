package horizon.taglib.dao;

import horizon.taglib.model.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRecordDao extends JpaRepository<PaymentRecord, Long> {

    PaymentRecord findByTransactionId(String transactionId);

}
