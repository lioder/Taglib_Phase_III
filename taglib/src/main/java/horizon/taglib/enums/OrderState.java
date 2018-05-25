package horizon.taglib.enums;

import lombok.Getter;

@Getter
public enum OrderState {
    INITIAL("未支付",0), PAID("已支付",1), EXPIRED("已过期",2);

    private String state;

    private Integer code;

    OrderState(String state, Integer code){
        this.state = state;
        this.code = code;
    }
}
