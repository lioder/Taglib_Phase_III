package horizon.taglib.vo;

import lombok.Data;

import java.util.List;

@Data
public class QuestionResultVO {

    /**
     * 图片名称
     */
    String filename;

    /**
     * 错误的tag
     */
    List<TagVO> wrong;

    /**
     * 正确的tag
     */
    List<TagVO> correct;

    /**
     * 未做的tag
     */
    List<TagVO> miss;

    public QuestionResultVO(String filename, List<TagVO> wrong, List<TagVO> correct, List<TagVO> miss) {
        this.filename = filename;
        this.wrong = wrong;
        this.correct = correct;
        this.miss = miss;
    }
}
