package horizon.taglib.vo;

import lombok.Data;

import java.util.List;

@Data
public class TaskProVO {

    /**
     * 对应的taskPublisher
     */
    Long taskPublisherId;

    /**
     * 做标注的专家的userId
     */
    Long userId;

    /**
     * 图片及其对应的tag
     */
    List<QuestionVO> questions;


}
