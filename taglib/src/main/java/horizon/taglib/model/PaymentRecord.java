package horizon.taglib.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@Entity
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;

    private String orderNo;

    private Double amount;

    private String returnUrl;

    private Date gmtModified;

    private Date gmtCreated;
}
