package horizon.taglib.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class Transaction {

    //支付参数Id
    private String transactionId;

    //订单号
    private String orderNo;

    //金额
    private Double amount;

}
