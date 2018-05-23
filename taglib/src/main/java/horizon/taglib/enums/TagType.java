package horizon.taglib.enums;

import lombok.Getter;

@Getter
public enum TagType {
    SIMPLE(0), RECT(1), IRREGULAR(2);

    Integer code;

    TagType(Integer code) {
        this.code = code;
    }
}
