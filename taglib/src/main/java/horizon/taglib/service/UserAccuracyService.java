package horizon.taglib.service;

import horizon.taglib.enums.ResultMessage;

/**
 * 用户准确度与自动评估标注结果服务接口
 * <br>
 * created on 2018/06/13
 *
 * @author 巽
 **/
public interface UserAccuracyService {
	/**
	 * 自动评估标注结果，为各工人发放奖励并修改准确度等属性
	 *
	 * @param taskPublisherId 任务（发布者视角）的id
	 * @return 是否评估成功
	 */
	ResultMessage adjustUserAccuracy(long taskPublisherId);
}
