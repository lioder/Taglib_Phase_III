package horizon.taglib.model;

import horizon.taglib.enums.OrderState;
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
public class AlipayOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    //支付订单号 平台订单号
    String orderNo;

    //交易用户账号
    Long userId;

    //交易金额
    Double amount;

    //订单状态
    OrderState orderState;

    //付款时间
    Date payTime;

    //过期时间
//    Date expireTime;

    //创建时间
    Date createTime;

    //修改时间
    Date modifyTime;

    public AlipayOrder(){

    }
    public AlipayOrder(Long userId, Double amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
