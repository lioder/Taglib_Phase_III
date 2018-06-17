package horizon.taglib.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserAccuracyVO {

    List<Double> user;

    List<Double> worker;

    List<Double> expert;

    public UserAccuracyVO() {
    }

    public UserAccuracyVO(List<Double> user, List<Double> worker, List<Double> expert) {
        this.user = user;
        this.worker = worker;
        this.expert = expert;
    }
}
