package horizon.taglib.service;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.AlipayOrder;

import java.util.List;

public interface AlipayService {

    public AlipayOrder initPay(String orderNo);

    public AlipayOrder finishOrder(String orderNo);

    public AlipayOrder createOrder(AlipayOrder order);

    public List<AlipayOrder> findAlipayOrdersByUserId(Long userId);

}
