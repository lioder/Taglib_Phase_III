package horizon.taglib.service;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.AlipayOrder;

import java.util.List;

public interface AlipayService {

    /**
     * 根据系统账单创建支付宝支付数据
     * @param orderNo
     * @return
     */
    public AlipayOrder initPay(String orderNo);

    /**
     * 完成一份系统账单
     * @param orderNo
     * @return
     */
    public AlipayOrder finishOrder(String orderNo);

    /**
     * 创建一份系统账单
     * @param order
     * @return
     */
    public AlipayOrder createOrder(AlipayOrder order);

    /**
     * 根据用户Id查找系统账单
     * @param userId
     * @return
     */
    public List<AlipayOrder> findAlipayOrdersByUserId(Long userId);

}
