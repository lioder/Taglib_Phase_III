package horizon.taglib.service.impl;

import horizon.taglib.dao.AlipayOrderDao;
import horizon.taglib.dao.UserDao;
import horizon.taglib.enums.OrderState;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.exception.OrderException;
import horizon.taglib.model.AlipayOrder;
import horizon.taglib.model.User;
import horizon.taglib.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
public class AlipayServiceImpl implements AlipayService{

    @Autowired
    AlipayOrderDao alipayOrderDao;

    @Autowired
    UserDao userDao;

    @Override
    public AlipayOrder createOrder(AlipayOrder order) {
        order.setOrderState(OrderState.INITIAL);
        Date now = new Date();
//        Date expire = new Date(now.getTime() + 15 * 60 * 1000);
//        order.setExpireTime(expire);
        order.setCreateTime(now);
        order.setModifyTime(now);
        order.setOrderNo(orderNoGenerator());
        return alipayOrderDao.saveAndFlush(order);
    }

    private String orderNoGenerator() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + (new Random().nextInt(9000) + 1000);
    }

    @Override
    public AlipayOrder initPay(String orderNo) {
        AlipayOrder order = alipayOrderDao.findByOrderNo(orderNo);
        if (order == null) {
            throw new OrderException(OrderException.ORDER_NOT_FOUND, OrderException.ORDER_NOT_FOUND_MSG);
        }
//        if (order.getRemainSeconds() < 0) {
//            throw new OrderException(OrderException.INVALID_ORDER, OrderException.INVALID_ORDER_MSG);
//        }
        if (order.getOrderState() == OrderState.PAID) {
            throw new OrderException(OrderException.REPEAT_PAYMENT, OrderException.REPEAT_PAYMENT_MSG);
        }
        return order;
    }

    @Override
    public AlipayOrder finishOrder(String orderNo) {
        AlipayOrder payOrder = alipayOrderDao.findByOrderNo(orderNo);
        payOrder.setOrderState(OrderState.PAID);
        payOrder.setPayTime(new Date());
        alipayOrderDao.save(payOrder);

        // 此处修改用户积分
        User user = userDao.findOne(payOrder.getUserId());
        BigDecimal bd=new BigDecimal(user.getPoints() + payOrder.getAmount() * 10).setScale(0, BigDecimal.ROUND_HALF_UP);
        user.setPoints(Long.parseLong(bd.toString()));
        userDao.save(user);
        return payOrder;
    }

}
