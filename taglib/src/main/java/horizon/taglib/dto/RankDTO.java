package horizon.taglib.dto;

import horizon.taglib.model.User;
import lombok.Data;

import java.util.List;

@Data
public class RankDTO {

    /**
     * 排行榜数据
     */
    List<User> rankList;

    /**
     * 我的排名
     */
    Integer myRank;

    /**
     * 关于我的信息
     */
    User myInfo;

    public RankDTO(List<User> rankList, Integer myRank, User myInfo) {
        this.rankList = rankList;
        this.myRank = myRank;
        this.myInfo = myInfo;
    }
}
