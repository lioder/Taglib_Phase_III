package horizon.taglib.vo;

import lombok.Data;

import java.util.List;

@Data
public class QuestionVO {

    /**
     * 图片名称
     */
    String filename;

    /**
     * 对应该图的所有标签
     */
    List<TagVO> tags;

    public QuestionVO(String filename, List<TagVO> tags) {
        this.filename = filename;
        this.tags = tags;
    }

    public QuestionVO() {
    }
}
