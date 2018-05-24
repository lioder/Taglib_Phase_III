package horizon.taglib.exception;

public class OrderException extends ServiceException {

    /**
     * 重复支付请求
     */
    public static final int REPEAT_PAYMENT = 1001;
    public static final String REPEAT_PAYMENT_MSG = "订单已支付";
    /**
     * 订单不存在
     */
    public static final int ORDER_NOT_FOUND = 1002;
    public static final String ORDER_NOT_FOUND_MSG = "订单不存在";
    /**
     * 无效的订单
     */
    public static final int INVALID_ORDER = 1003;
    public static final String INVALID_ORDER_MSG = "订单已失效";

    public OrderException(int code, String message) {
        super(code, message);
    }

}
