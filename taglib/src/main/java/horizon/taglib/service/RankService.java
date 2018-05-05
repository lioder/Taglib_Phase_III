package horizon.taglib.service;

import horizon.taglib.dto.RankDTO;

public interface RankService {

    /**
     * 得到工人积分排行榜
     * @param userId
     * @return
     */
    public RankDTO getWorkerPointRank(Long userId);

    /**
     * 得到工人经验排行榜
     * @param userId
     * @return
     */
    public RankDTO getWorkerExpRank(Long userId);

    /**
     * 得到工人准确度排行榜
     * @param userId
     * @return
     */
    public RankDTO getWorkerAccuracyRateRank(Long userId);

    /**
     * 得到工人满意度排行榜
     * @param userId
     * @return
     */
    public RankDTO getWorkerSatisfactionRank(Long userId);

    /**
     * 得到发起者积分排行榜
     * @param userId
     * @return
     */
    public RankDTO getPublisherPointRank(Long userId);

    /**
     * 得到发起者经验排行榜
     * @param userId
     * @return
     */
    public RankDTO getPublisherExpRank(Long userId);

    /**
     * 得到发起者发布任务数排行榜
     * @param userId
     * @return
     */
    public RankDTO getPublisherTaskRank(Long userId);
}
