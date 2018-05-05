package horizon.taglib.enums;

import lombok.Getter;

@Getter
public enum TagDescType {
    SINGLE(0), Multi(1);

    Integer code;

    TagDescType(Integer code) {
        this.code = code;
    }
}
