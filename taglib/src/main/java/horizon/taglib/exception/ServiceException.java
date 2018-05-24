package horizon.taglib.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceException extends RuntimeException {

    private int code;

    public ServiceException() {}

    public ServiceException(int code) {
        this.code = code;
    }

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

}
