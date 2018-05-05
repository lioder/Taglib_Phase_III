package horizon.taglib.vo;

import lombok.Data;

@Data
public class ResultVO<T> {

    Integer code;
    String message;
    T data;

    public ResultVO(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
