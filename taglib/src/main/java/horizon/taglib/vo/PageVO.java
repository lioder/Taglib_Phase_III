package horizon.taglib.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> {
    /**
     * 当前页数
     */
    Integer currentPage;
    /**
     * 每页条目数
     */
    Integer size;
    /**
     * 总条目数
     */
    Integer totalItemNum;
    /**
     * 排序字段名称
     */
    String sortBy;
    /**
     * 顺序排序
     */
    Boolean isSec;
    /**
     * 返回数据
     */
    List<T> data;

    public PageVO() {
    }

    public PageVO(Integer currentPage, Integer size, Integer totalItemNum, String sortBy, Boolean isSec, List<T> data) {
        this.currentPage = currentPage;
        this.size = size;
        this.totalItemNum = totalItemNum;
        this.sortBy = sortBy;
        this.isSec = isSec;
        this.data = data;
    }

    public PageVO(Integer currentPage, Integer size, Integer totalItemNum, List<T> data) {
        this.currentPage = currentPage;
        this.size = size;
        this.totalItemNum = totalItemNum;
        this.data = data;
    }
}
