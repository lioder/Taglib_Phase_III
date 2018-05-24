package horizon.taglib.controller;

import com.alipay.api.AlipayApiException;
import horizon.taglib.exception.GatewayException;
import horizon.taglib.gateway.AlipayGateway;
import horizon.taglib.model.AlipayOrder;
import horizon.taglib.service.AlipayService;
import horizon.taglib.model.Transaction;
import horizon.taglib.vo.AlipayOrderVO;
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
                             @RequestParam(value = "from", defaultValue = "pc") String from,
                             @RequestParam("return_url") String returnUrl,
                             Model model) {

        Transaction transaction = alipayService.initPay(orderNo, returnUrl);
        String modelContent = "";
        if (transaction.getAmount() < 0.01) {  //支付金额为 0 时，直接支付成功
            alipayService.finishOrder(transaction.getOrderNo());
            return "redirect:" + returnUrl;
        }
        try {
            if (from.toLowerCase().equals("wap")) {
                modelContent = alipayGateway.createWapPay(transaction);
            } else {
                modelContent = alipayGateway.createPcPay(transaction);
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new GatewayException(GatewayException.CREATE_PAYMENT_ERROR, GatewayException.CREATE_PAYMENT_ERROR_MSG);
        }
        model.addAttribute("content", modelContent);
        return "to_alipay";
    }

    /**
     *
     * @param request
     */
    @PostMapping("/notify")
    @ResponseBody
    public String handleNotify(HttpServletRequest request) {
        Map<String, String> params = getAlipayRequestMap(request);
        alipayGateway.handleNotify(params);
        return "success";   //注意，如果这里不返回success时，支付宝会发送多次通知
    }



    /**
     * 解析支付完成后的返回参数
     * @param request
     * @return
     */
    private Map<String, String> getAlipayRequestMap(HttpServletRequest request) {
        //获取支付宝的反馈信息
        Map<String,String> params = new HashMap<>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (String key : requestParams.keySet()) {
            String[] values = requestParams.get(key);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            /*//乱码解决
            try {
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                continue;
            }*/
            params.put(key, valueStr);
        }

        return params;
    }
}
