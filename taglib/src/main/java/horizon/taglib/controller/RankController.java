package horizon.taglib.controller;

import horizon.taglib.dto.RankDTO;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.User;
import horizon.taglib.service.RankService;
import horizon.taglib.vo.RankVO;
import horizon.taglib.vo.ResultVO;
import horizon.taglib.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rank")
public class RankController {

    @Autowired
    private RankService rankService;

    /**
     * 得到工人积分排行榜
     * @param userId
     * @return
     */
    @GetMapping(value = "/workerPoint")
    public ResultVO getWorkerPointRank(@RequestParam("userId") Long userId){
        RankDTO dto = rankService.getWorkerPointRank(userId);
        RankVO rankVO = rankDTOToRankVO(dto, userId);
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), rankVO);
    }

    /**
     * 得到工人经验排行榜
     * @param userId
     * @return
     */
    @GetMapping(value = "/workerExp")
    public ResultVO getWorkerExpRank(@RequestParam("userId") Long userId){
        RankDTO dto = rankService.getWorkerExpRank(userId);
        RankVO rankVO = rankDTOToRankVO(dto, userId);
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), rankVO);
    }

    /**
     * 得到工人准确度排行榜
     * @param userId
     * @return
     */
    @GetMapping(value = "/workerAccuracyRate")
    public ResultVO getWorkerAccuracyRateRank(@RequestParam("userId") Long userId){
        RankDTO dto = rankService.getWorkerAccuracyRateRank(userId);
        RankVO rankVO = rankDTOToRankVO(dto, userId);
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), rankVO);
    }

    /**
     * 得到工人满意度排行榜
     * @param userId
     * @return
     */
    @GetMapping(value = "/workerSatisRate")
    public ResultVO getWorkerSatisfactionRateRank(@RequestParam("userId") Long userId){
        RankDTO dto = rankService.getWorkerSatisfactionRank(userId);
        RankVO rankVO = rankDTOToRankVO(dto, userId);
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), rankVO);
    }

    /**
     * 得到发起者积分排行榜
     * @param userId
     * @return
     */
    @GetMapping(value = "/pubPoint")
    public ResultVO getPublisherPointRank(@RequestParam("userId") Long userId){
        RankDTO dto = rankService.getPublisherPointRank(userId);
        RankVO rankVO = rankDTOToRankVO(dto, userId);
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), rankVO);
    }

    /**
     * 得到发起者经验排行榜
     * @param userId
     * @return
     */
    @GetMapping(value = "/pubExp")
    public ResultVO getPublisherExpRank(@RequestParam("userId") Long userId){
        RankDTO dto = rankService.getPublisherExpRank(userId);
        RankVO rankVO = rankDTOToRankVO(dto, userId);
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), rankVO);
    }

    /**
     * 得到发起者发布任务数排行榜
     * @param userId
     * @return
     */
    @GetMapping(value = "/pubTask")
    public ResultVO getPublisherTaskRank(@RequestParam("userId") Long userId){
        RankDTO dto = rankService.getPublisherTaskRank(userId);
        RankVO rankVO = rankDTOToRankVO(dto, userId);
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), rankVO);
    }

    private static RankVO rankDTOToRankVO(RankDTO rankDTO, Long userId){
        List<User> userList = rankDTO.getRankList();
        List<UserVO> userVOS = new ArrayList<>();
        if(userList!=null){
            for(User temp: userList) {
                userVOS.add(userToUserVO(temp));
            }
        }

        RankVO rankVO = new RankVO(userVOS, 0, null);
        if(userId != null && userId != 0) {
            rankVO = new RankVO(userVOS, rankDTO.getMyRank(), userToUserVO(rankDTO.getMyInfo()));
        }

        return rankVO;
    }

    private static UserVO userToUserVO(User user){
        UserVO userVO = new UserVO(user.getId(), user.getUsername(), user.getPassword(), user.getPhoneNumber(), user.getEmail(), user.getUserType().getCode(), user.getPoints(),
                user.getAvatar(), user.getLevel(), user.getExp(), user.getAccuracyRate(), user.getPunctualityRate(), user.getSatisfactionRate(), user.getIsAttendant(), user.getApplyState());
        userVO.setTaskNum(user.getMyTasks().size());
        return userVO;
    }
}
