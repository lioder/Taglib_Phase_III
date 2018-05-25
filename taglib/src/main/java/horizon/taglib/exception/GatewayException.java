package horizon.taglib.exception;

public class GatewayException extends ServiceException {

    /**
     * 支付请求失败
     */
    public static final int CREATE_PAYMENT_ERROR = 2001;
    public static final String CREATE_PAYMENT_ERROR_MSG = "支付创建失败";

    public GatewayException(int code, String message) {
        super(code, message);
    }

}
