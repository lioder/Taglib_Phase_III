package horizon.taglib.gateway;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import horizon.taglib.config.AlipayConfig;
import horizon.taglib.model.AlipayOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class AlipayGateway {

    private AlipayClient alipayClient;

    @Autowired
    private AlipayConfig alipayConfig;

    @PostConstruct
    public void initAlipay() {
        //初始化 AlipayClient
        alipayClient = new DefaultAlipayClient(alipayConfig.getGatewayUrl(), alipayConfig.getAppId(), alipayConfig.getMerchantPrivateKey(), "json", AlipayConfig.CHARSET,
                        alipayConfig.getAlipayPublicKey(), AlipayConfig.SIGN_TYPE);
    }

    /**
     *  创建PC端支付请求
     * @param alipayOrder  支付信息
     * @return  返回封装请求的form
     * @throws AlipayApiException
     */
    public String createPcPay(AlipayOrder alipayOrder) throws AlipayApiException {
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());    //通知地址
        alipayRequest.setReturnUrl("http://xunner.top:8000/#/alipay"); //回调地址

        // 封装支付请求信息
        AlipayTradePagePayModel pagePayModel = new AlipayTradePagePayModel();
        pagePayModel.setTotalAmount(alipayOrder.getAmount().toString());
        pagePayModel.setOutTradeNo(alipayOrder.getOrderNo().toString());
        pagePayModel.setSubject("Taglib 账户充值");
        pagePayModel.setBody("test-body");
        pagePayModel.setProductCode("FAST_INSTANT_TRADE_PAY");

        alipayRequest.setBizModel(pagePayModel);

        //请求
        return alipayClient.pageExecute(alipayRequest).getBody();
    }

    /**
     * 创建wap端支付请求
     * @param transaction  支付信息
     * @return  返回封装请求的form
     * @throws AlipayApiException
     */
//    public String createWapPay(Transaction transaction) throws AlipayApiException {
//        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
//        alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());    //通知地址
//
//        // 封装支付请求信息
//        AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
//        model.setTotalAmount(transaction.getAmount().toString());
//        model.setOutTradeNo(transaction.getOrderNo().toString());
//        model.setSubject("test-subject");
//        model.setBody("test-body");
//        model.setProductCode("QUICK_WAP_PAY");
//
//        alipayRequest.setBizModel(model);
//
//        //请求
//        return alipayClient.pageExecute(alipayRequest).getBody();
//    }
}
