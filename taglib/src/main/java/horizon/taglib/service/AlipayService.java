package horizon.taglib.service;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.AlipayOrder;
import horizon.taglib.model.Transaction;

public interface AlipayService {

    public Transaction initPay(String orderNo, String returnUrl);

    public ResultMessage finishOrder(String orderNo);

    public AlipayOrder createOrder(AlipayOrder order);
}
