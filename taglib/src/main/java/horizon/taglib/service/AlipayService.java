package horizon.taglib.service;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.AlipayOrder;

public interface AlipayService {

    public AlipayOrder initPay(String orderNo);

    public AlipayOrder finishOrder(String orderNo);

    public AlipayOrder createOrder(AlipayOrder order);

}
