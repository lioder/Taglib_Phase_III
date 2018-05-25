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

    //支付交易号 支付宝支付订单号
    String tradeNo;

    //交易用户账号
    Long userId;

    //交易金额
    Double amount;

    //订单状态
    OrderState orderState;

    //付款时间
    Date payTime;

    //过期时间
    Date expireTime;

    //创建时间
    Date createTime;

    //修改时间
    Date modifyTime;

    //是否删除
    Boolean isDeleted;

    public int getRemainSeconds() {
        if (expireTime != null) {
            long millis = expireTime.getTime() - System.currentTimeMillis();
            return (int) millis / 1000;
        }
        return 0;
    }

    public AlipayOrder(){

    }
    public AlipayOrder(Long userId, Double amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
