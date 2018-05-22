package horizon.taglib.enums;

import lombok.Getter;

@Getter
public enum InterestFactor {
    FAV(10), VIEW(1), ACCEPT(5);

    int factor;
    InterestFactor(int factor){
        this.factor = factor;
    }
}
