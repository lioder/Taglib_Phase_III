package horizon.taglib.service;

import horizon.taglib.dto.PageDTO;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.TaskState;
import horizon.taglib.model.Tag;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.TaskWorker;
import horizon.taglib.model.User;

import java.util.List;

/**
 * 用户服务后端接口
 * <br>
 * created on 2018/03/15
 *
 * @author 巽
 **/
public interface UserService {
    /**通过用户id查找用户
     * @param id
     * @return
     */
    public User findUserById(long id);

    /**
     * 注册
     * @param user
     * @return 添加成功：SUCCESS；用户邮箱已经存在，添加失败：EMAIL_EXIST;用户电话号码已经存在，添加失败
     */
    public ResultMessage register(User user);

    /**
     * 登录
     * @param name
     * @param password
     * @return 登录成功：返回SUCCESS,成功登录的用户信息;登录失败：FAILED;用户不存在：NOT_EXIST
     */
    public List<Object> login(String name, String password);

    /**
     * 根据状态查找用户任务
     * @param userId 用户编号
     * @param taskState 用户状态
     * @param size 每页有的条目数
     * @param currentPage 当前页数
     * @return
     */
    public PageDTO<TaskWorker> findTaskWorkerByState(Long userId, TaskState taskState, Integer size, Integer currentPage);

    /**
     * 用户提交、更新任务
     * @param taskWorker
     * @return
     */
    public ResultMessage submitTask(TaskWorker taskWorker,List<Tag> tags);

    /**
     *用户签到
     * @param userId
     * @return 签到成功，返回SUCCESS；已经签到，返回ALREADY_ATTENDANT；
     */
    public ResultMessage attend(Long userId);

    /**
     * 删除任务
     * @param taskWorkerId
     * @return
     */
    public ResultMessage deleteTask(Long taskWorkerId);

    /**
     *用户充值
     * @param userId
     * @param amount 最多一位小数，若超过一位小数四舍五入
     * @return
     */
    public ResultMessage recharge(Long userId, double amount);

    /**
     * 通过编号查找
     * @param id
     * @return
     */
    public TaskPublisher findTaskPublisherById(long id);

    /**
     * 得到符合要求的Tag
     * @param taskWorkerId
     * @return
     */
    public List<Tag> getFitTags(long taskWorkerId);

    /**
     * 寻找该用户未接受的任务
     * 当keywords不为空时
     * sortBy为时间，isSec为正序时，按最新发布任务排序
     * @param keywords
     * @param sortBy
     * @param isSec
     * @param size
     * @param currentPage
     * @param userId
     * @return
     */
    public PageDTO<TaskPublisher> searchTask(String keywords , String sortBy , Boolean isSec , Integer size , Integer currentPage , Long userId);

    /**
     * 通过id查找工人任务
     * @param id
     * @return
     */
    public TaskWorker findTaskWorkerById(long id);

    /**
     * 用户接受任务
     * @param taskWorker
     * @return
     */
    public TaskWorker acceptTask(TaskWorker taskWorker);

    /**更新好评率
     * @param taskWorkerId
     * @param rating
     * @return
     */
    public ResultMessage updateRating(long taskWorkerId,Integer rating);


//    /**
//     * 得到新的工人任务Id
//     * @return
//     */
//    public Long getTaskWorkerId();

    /**
     * 根据工人Id得到该工人所有完成任务的准时率
     * @param userId
     * @return
     */
    public ResultMessage updatePunctualityRate(long userId);

    /**
     * 更改头像
     * @param userId
     * @param avatar
     * @return
     */
    public ResultMessage updateAvatar(long userId,String avatar);



}
