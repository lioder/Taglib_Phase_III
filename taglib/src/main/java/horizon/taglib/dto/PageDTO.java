package horizon.taglib.dto;

import lombok.Data;

import java.util.List;
@Data
public class PageDTO<T> {
    //已知
    /**
     * 每页有的条目数
     */
    private Integer size;
    /**
     * 当前页数，从1开始计数
     */
    private Integer currentPage;
    /**
     * 总的条目数，从1开始计数
     */
    private Integer totalSize;

    //需要计算
    /**
     * 总页数
     */
    private Integer totalPage;
    /**
     * 每页的数据
     */
    private List<T> pageData;
    /**
     * 当前开始取的数据，即当前页数数据的第一条
     */
    private Integer dataIndex;

    public PageDTO(Integer currentPage, Integer size, Integer totalSize){
        this.currentPage = currentPage;
        this.size = size;
        this.totalSize = totalSize;

        //计算totalPage 总页数
        if(totalSize%size==0){
            this.totalPage = totalSize/size;
        }else{
            this.totalPage = totalSize/size + 1;
        }

        //计算dataIndex
        this.dataIndex = (currentPage - 1)*size + 1;

        this.pageData = null;
    }
}
