package horizon.taglib.service;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.AlipayOrder;
import horizon.taglib.model.Transaction;

public interface AlipayService {

    public Transaction initPay(String orderNo);

    public AlipayOrder finishOrder(String orderNo);

    public AlipayOrder createOrder(AlipayOrder order);

    public AlipayOrder findOrderByOrderNo(String orderNo);
}
