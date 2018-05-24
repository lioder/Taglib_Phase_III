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
import horizon.taglib.model.Transaction;
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
     * @param transaction  支付信息
     * @return  返回封装请求的form
     * @throws AlipayApiException
     */
    public String createPcPay(Transaction transaction) throws AlipayApiException {
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(transaction.getReturnUrl());    //回调地址
        alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());    //通知地址

        // 封装支付请求信息
        AlipayTradePagePayModel pagePayModel = new AlipayTradePagePayModel();
        pagePayModel.setTotalAmount(transaction.getAmount().toString());
        pagePayModel.setOutTradeNo(transaction.getOrderNo().toString());
        pagePayModel.setSubject("test-subject");
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
    public String createWapPay(Transaction transaction) throws AlipayApiException {
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        alipayRequest.setReturnUrl(transaction.getReturnUrl());    //回调地址
        alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());    //通知地址

        // 封装支付请求信息
        AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
        model.setTotalAmount(transaction.getAmount().toString());
        model.setOutTradeNo(transaction.getOrderNo().toString());
        model.setSubject("test-subject");
        model.setBody("test-body");
        model.setProductCode("QUICK_WAP_PAY");

        alipayRequest.setBizModel(model);

        //请求
        return alipayClient.pageExecute(alipayRequest).getBody();
    }

    /**
     * 验证并处理支付完成的回调  notify     对于异步返回的信息，我们需要手动验证信息的真实性
     * @param params 返回的参数
     * @return
     */
    public void handleNotify(Map<String, String> params) {
        String orderIdString = params.get("out_trade_no");   //支付订单号  平台订单号
        String tradeId = params.get("trade_no");       //支付交易号   支付宝支付订单号
        String state = params.get("trade_status");   //支付状态

        if(orderIdString==null||orderIdString.isEmpty()||tradeId==null||tradeId.isEmpty()||state==null||state.isEmpty()){
            return;
        }

        //验证签名信息是否合法
        boolean isValid = checkSignature(params);

        if (isValid && "TRADE_SUCCESS".equals(state)) {
           // trySavePaymentAlipay(tradeNo, orderNo);
        }
    }

    /**
     * 验证回调信息是否合法
     * @param params 回调参数
     * @return 信息是否合法
     */
    private boolean checkSignature(Map params) {
        boolean signVerified = false; //调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), AlipayConfig.CHARSET, AlipayConfig.SIGN_TYPE);
        } catch (AlipayApiException e) {
            return false;
        }
        return signVerified;
    }

    /**
     * 记录支付信息    使用同步方法来防止多次回调产生重复记录
     * @param tradeNo  支付宝交易号
     * @param orderNo  平台订单号
     */
    private synchronized void trySavePaymentAlipay(String tradeNo, String orderNo) {
//        AlipayRecord alipayRecord = alipayRecordRepository.findByOrderNo(orderNo);
//        if (alipayRecord == null) {
//            alipayRecord = new AlipayRecord();
//            alipayRecord.setOrderNo(orderNo);
//            alipayRecord.setTradeNo(tradeNo);
//            alipayRecordRepository.save(alipayRecord);
//            paymentService.finishOrder(orderNo);
//        }
    }
}
