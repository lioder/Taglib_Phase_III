package horizon.taglib.vo;

import horizon.taglib.enums.OrderState;
import lombok.Data;

import java.util.Date;

@Data
public class AlipayOrderVO {

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

    //创建时间
    Date createTime;

    //修改时间
    Date modifyTime;

    public AlipayOrderVO() {
    }

    public AlipayOrderVO(String orderNo, Long userId, Double amount, OrderState orderState, Date payTime, Date createTime, Date modifyTime) {
        this.orderNo = orderNo;
        this.userId = userId;
        this.amount = amount;
        this.orderState = orderState;
        this.payTime = payTime;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
    }
}
