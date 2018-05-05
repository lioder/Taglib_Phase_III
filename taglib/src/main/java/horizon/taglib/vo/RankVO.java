package horizon.taglib.vo;

import lombok.Data;

import java.util.List;

@Data
public class RankVO {

    /**
     * 排行榜数据
     */
    List<UserVO> rankList;

    /**
     * 我的排名
     */
    Integer myRank;

    /**
     * 关于我的信息
     */
    UserVO myInfo;

    public RankVO(List<UserVO> rankList, Integer myRank, UserVO myInfo) {
        this.rankList = rankList;
        this.myRank = myRank;
        this.myInfo = myInfo;
    }
}
