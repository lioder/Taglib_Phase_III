package horizon.taglib.enums;

import lombok.Getter;

@Getter
public enum ResultMessage {
    SUCCESS("成功", 0),FAILED("失败", 1),NETWORK_ERROR("网络错误", 3),NOT_EXIST("不存在", 4),EXIST("存在", 5),EAMIL_EXIST("邮箱已存在",6),PHONE_EXIST("手机已存在",7),ALREADY_ATTENDANT("已登录",8);

    public String value;

    private Integer code;

    ResultMessage(String value, Integer code){
        this.value=value;
        this.code = code;
    }

    public String toString(){
        return value;
    }
}
