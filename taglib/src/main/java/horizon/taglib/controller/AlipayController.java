package horizon.taglib.controller;

import com.alipay.api.AlipayApiException;
import horizon.taglib.exception.GatewayException;
import horizon.taglib.gateway.AlipayGateway;
import horizon.taglib.model.AlipayOrder;
import horizon.taglib.service.AlipayService;
import horizon.taglib.model.Transaction;
import horizon.taglib.vo.AlipayOrderVO;
import horizon.taglib.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/alipay")
public class AlipayController {

    @Autowired
    private AlipayGateway alipayGateway;

    @Autowired
    private AlipayService alipayService;

    @PostMapping("/orders")
    public AlipayOrderVO createOrder(@RequestBody AlipayOrderVO payOrder) {
        AlipayOrder alipayOrder = new AlipayOrder(payOrder.getUserId(), payOrder.getAmount());
        alipayOrder = alipayService.createOrder(alipayOrder);
        payOrder = new AlipayOrderVO(alipayOrder.getOrderNo(), alipayOrder.getUserId(), alipayOrder.getAmount(), alipayOrder.getOrderState(), alipayOrder.getPayTime(),
                alipayOrder.getExpireTime(), alipayOrder.getCreateTime(), alipayOrder.getModifyTime());
        return payOrder;
    }

    @GetMapping("/{orderNo}")
    public String payRequest(@PathVariable("orderNo") String orderNo,
                               @RequestParam(value = "from", defaultValue = "pc") String from) {

        Transaction transaction = alipayService.initPay(orderNo);
        String modelContent = "";
        if (transaction.getAmount() < 0.01) {  //支付金额为 0 时，直接支付成功
            alipayService.finishOrder(transaction.getOrderNo());
            return "success";
        }
        try {
            modelContent = alipayGateway.createPcPay(transaction);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new GatewayException(GatewayException.CREATE_PAYMENT_ERROR, GatewayException.CREATE_PAYMENT_ERROR_MSG);
        }
        return modelContent;
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/detail/{orderNo}")
    public ResultVO finishOrder(@PathVariable String orderNo){
        AlipayOrder order = alipayService.finishOrder(orderNo);
        return new ResultVO<>(0, "success", order);
    }

}
