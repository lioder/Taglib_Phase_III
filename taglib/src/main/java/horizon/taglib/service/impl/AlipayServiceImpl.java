package horizon.taglib.service.impl;

import horizon.taglib.dao.AlipayOrderDao;
import horizon.taglib.dao.AlipayRecordDao;
import horizon.taglib.dao.PaymentRecordDao;
import horizon.taglib.enums.OrderState;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.exception.OrderException;
import horizon.taglib.model.AlipayOrder;
import horizon.taglib.model.PaymentRecord;
import horizon.taglib.model.Transaction;
import horizon.taglib.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
public class AlipayServiceImpl implements AlipayService{

    @Autowired
    AlipayOrderDao alipayOrderDao;

    @Autowired
    PaymentRecordDao paymentRecordDao;

    @Override
    public AlipayOrder createOrder(AlipayOrder order) {
        order.setOrderState(OrderState.INITIAL);
        Date now = new Date();
        Date expire = new Date(now.getTime() + 15 * 60 * 1000);
        order.setExpireTime(expire);
        order.setCreateTime(now);
        order.setModifyTime(now);
        order.setOrderNo(orderNoGenerator());
        order.setIsDeleted(false);
        return alipayOrderDao.saveAndFlush(order);
    }

    private String orderNoGenerator() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + (new Random().nextInt(9000) + 1000);
    }

    @Override
    public Transaction initPay(String orderNo, String returnUrl) {
        AlipayOrder order = alipayOrderDao.findByOrderNo(orderNo);
        if (order == null) {
            throw new OrderException(OrderException.ORDER_NOT_FOUND, OrderException.ORDER_NOT_FOUND_MSG);
        }
        if (order.getRemainSeconds() < 0) {
            throw new OrderException(OrderException.INVALID_ORDER, OrderException.INVALID_ORDER_MSG);
        }
        if (order.getOrderState() == OrderState.PAID) {
            throw new OrderException(OrderException.REPEAT_PAYMENT, OrderException.REPEAT_PAYMENT_MSG);
        }
        PaymentRecord paymentRecord = createPaymentRecord(order, returnUrl);
        return createTransactionFromPaymentRecord(paymentRecord);
    }

    @Override
    public ResultMessage finishOrder(String orderNo) {
        AlipayOrder payOrder = alipayOrderDao.findByOrderNo(orderNo);
        payOrder.setOrderState(OrderState.PAID);
        payOrder.setPayTime(new Date());
        alipayOrderDao.save(payOrder);
        return ResultMessage.SUCCESS;
    }

    private PaymentRecord createPaymentRecord(AlipayOrder order, String returnUrl) {
        PaymentRecord paymentRecord = new PaymentRecord();
        Date now = new Date();
        paymentRecord.setAmount(order.getAmount());
        paymentRecord.setOrderNo(order.getOrderNo());
        paymentRecord.setReturnUrl(returnUrl);
        paymentRecord.setTransactionId(generateTrackId());
        paymentRecord.setGmtCreated(now);
        paymentRecord.setGmtModified(now);
        paymentRecordDao.save(paymentRecord);
        return paymentRecord;
    }

    private String generateTrackId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }


    private Transaction createTransactionFromPaymentRecord(PaymentRecord paymentRecord) {
        Transaction transaction= new Transaction();
        transaction.setOrderNo(paymentRecord.getOrderNo());
        transaction.setTransactionId(paymentRecord.getTransactionId());
        transaction.setReturnUrl(paymentRecord.getReturnUrl());
        transaction.setAmount(paymentRecord.getAmount());
        return transaction;
    }
}
