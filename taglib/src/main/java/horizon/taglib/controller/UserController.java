package horizon.taglib.controller;

import horizon.taglib.dto.PageDTO;
import horizon.taglib.enums.*;
import horizon.taglib.model.*;
import horizon.taglib.service.AdminService;
import horizon.taglib.service.RecommendService;
import horizon.taglib.service.UserService;
import horizon.taglib.utils.Point;
import horizon.taglib.vo.*;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private RecommendService recommendService;

    @RequestMapping(value = "/loginByToken", method = RequestMethod.GET)
    public ResultVO getUserId(@RequestParam(value = "name", required = true) String name,
                          @RequestParam(value = "password", required = true) String password) {
        List<Object> list = userService.login(name, password);
        if(list!=null){
            ResultMessage re = (ResultMessage) list.get(0);
            if(re == ResultMessage.SUCCESS){
                if(list.size()>1){
                    User user = (User) list.get(1);
                    UserVO userVO = new UserVO(user.getId(), user.getUsername(), user.getPassword(), user.getPhoneNumber(), user.getEmail(), user.getUserType().getCode(), user.getPoints(),
                            user.getAvatar(), user.getLevel(), user.getExp(), user.getAccuracyRate(), user.getPunctualityRate(), user.getSatisfactionRate(), user.getIsAttendant());
                    return new ResultVO(re.getCode(), "login success", userVO);
                }
                else{
                    return new ResultVO(ResultMessage.FAILED.getCode(), "未读取到用户信息", null);
                }
            }
            else if(re == ResultMessage.NOT_EXIST){
                return new ResultVO(re.getCode(), "用户不存在", null);
            }
            else{
                return new ResultVO(re.getCode(), "用户名与密码不匹配", null);
            }
        }
        return new ResultVO(ResultMessage.FAILED.getCode(), "系统出现错误", null);
    }

    /**
     * 申请账号
     * @param userSignVO
     * @return
     */
    @PostMapping(value = "/sign")
    public ResultVO register(@RequestBody UserSignVO userSignVO){
        UserType userType;
        if(userSignVO.getUserType() == UserType.WORKER.getCode()){
            userType = UserType.WORKER;
        }
        else if(userSignVO.getUserType() == UserType.REQUESTOR.getCode()){
            userType = UserType.REQUESTOR;
        }
        else{
            userType = UserType.ADMIN;
        }

        User user = new User(userSignVO.getUsername(), userSignVO.getPassword(), userSignVO.getPhone(), userSignVO.getEmail(), userType);
        ResultMessage re = userService.register(user);
        if(re == ResultMessage.SUCCESS){
            List<Object> list = userService.login(userSignVO.getPhone(), userSignVO.getPassword());
            Long userId = ((User)list.get(1)).getId();

            recommendService.addUserInterestFactor(userId, userSignVO.getTopics(), InterestFactor.FAV);
            return new ResultVO(re.getCode(), "register success", null);
        }
        else if(re == ResultMessage.EAMIL_EXIST || re == ResultMessage.PHONE_EXIST){
            return new ResultVO(re.getCode(), re.getValue(), null);
        }
        else{
            return new ResultVO(ResultMessage.FAILED.getCode(), "未知系统错误", null);
        }
    }

    /**
     * 通过Id查找用户详细信息
     * @param userId
     * @return
     */
    @GetMapping(value = "/info/{userId}")
    public ResultVO findUserById(@PathVariable("userId") Long userId){
        User user = userService.findUserById(userId);
        if(user!=null){
            UserVO userVO = new UserVO(userId, user.getUsername(), user.getPassword(), user.getPhoneNumber(), user.getEmail(), user.getUserType().getCode(), user.getPoints(),
                    user.getAvatar(), user.getLevel(), user.getExp(), user.getAccuracyRate(), user.getPunctualityRate(), user.getSatisfactionRate(), user.getIsAttendant());
            return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), userVO);
        }
        return new ResultVO(ResultMessage.FAILED.getCode(), ResultMessage.FAILED.getValue(), null);
    }

    /**
     * 根据关键字搜索用户未接过的任务
     * @param keyword
     * @param sortBy
     * @param isSec
     * @param size
     * @param currentPage
     * @return
     */
    @GetMapping(value = "/tasks/search")
    public ResultVO searchTask(@RequestParam(value = "keyword") String keyword,
                               @RequestParam(value = "sortBy") String sortBy,
                               @RequestParam(value = "isSec") Boolean isSec,
                               @RequestParam(value = "size") Integer size,
                               @RequestParam(value = "page") Integer currentPage,
                               @RequestParam(value = "userId") Long userId,
                               @RequestParam(value = "topics") List<String> topics){
        List<TaskInfoVO> taskInfoVOS = new ArrayList<>();
        PageDTO bean = userService.searchTask(keyword, sortBy, isSec, size, currentPage, userId, topics);
        if(bean.getPageData()!=null){
            List<TaskPublisher> tasks = bean.getPageData();
            for(TaskPublisher temp: tasks){
                double price = temp.getPrice()/temp.getNumberPerPicture();
                TaskInfoVO taskInfoVO = new TaskInfoVO(temp.getId(), temp.getTitle(), temp.getDescription(), temp.getImages().get(0), temp.getImages().size(),
                        temp.getTaskType().getCode(), temp.getTopics(), price, temp.getStartDate(), temp.getEndDate(), temp.getRating());
                taskInfoVOS.add(taskInfoVO);
            }
            PageVO pageVO = new PageVO(bean.getCurrentPage(), bean.getSize(), bean.getTotalSize(), sortBy, isSec, taskInfoVOS);
            return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), pageVO);
        }
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), new PageVO());
    }

    /**
     * 根据状态查找用户任务
     * @param userId
     * @param taskState
     * @param size
     * @param currentPage
     * @return
     */
    @GetMapping(value = "/{userId}/tasks")
    public ResultVO findTaskWorkerByState(@PathVariable("userId") Long userId,
                                    @RequestParam("state") TaskState taskState,
                                    @RequestParam("size") Integer size,
                                    @RequestParam("page") Integer currentPage){
        PageDTO bean = userService.findTaskWorkerByState(userId, taskState, size, currentPage);
        if(bean.getPageData()!=null){
            List<TaskWorker> taskWorkers = bean.getPageData();
            List<TaskInfoVO> taskInfoVOS = new ArrayList<>();
            for(TaskWorker temp: taskWorkers){
                TaskWorkerVO vo = taskWorkerPOtoVO(temp);
                double rating = userService.findTaskPublisherById(vo.getTaskId()).getRating();
                TaskInfoVO taskInfoVO = new TaskInfoVO(vo.getId(), vo.getTitle(), vo.getDescription(), vo.getImages().get(0).getFilename(), vo.getImages().size(),
                        vo.getTaskType(), vo.getTopics(), vo.getPrice(), vo.getStartDate(), vo.getEndDate(), rating);
                taskInfoVOS.add(taskInfoVO);
            }
            PageVO pageVO = new PageVO(bean.getCurrentPage(), bean.getSize(), bean.getTotalSize(), taskInfoVOS);
            return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), pageVO);
        }
        return new ResultVO(ResultMessage.FAILED.getCode(), ResultMessage.FAILED.getValue(), null);
    }

    /**
     * 通过taskWorkerId查找对应的TaskWorker
     * @param taskId
     * @return
     */
    @GetMapping(value = "/{taskId}")
    public ResultVO findTaskWorkerById(@PathVariable("taskId") Long taskId){
        TaskWorker taskWorker = userService.findTaskWorkerById(taskId);
        TaskWorkerVO taskWorkerVO = taskWorkerPOtoVO(taskWorker);
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), taskWorkerVO);
    }

    /**
     * 用户接受新任务
     * @return
     */
    @GetMapping(value = "/new/{taskPublisherId}")
    public ResultVO addNewTask(@PathVariable("taskPublisherId") Long taskPublisherId,
                               @RequestParam("userId") Long userId){
        TaskPublisher taskPublisher = userService.findTaskPublisherById(taskPublisherId);
        Double price = taskPublisher.getPrice()/taskPublisher.getNumberPerPicture();
        TaskWorker taskWorker = new TaskWorker(taskPublisherId, userId, price, LocalDate.now().toString());
        TaskWorker re = userService.acceptTask(taskWorker);
        if(re != null) {
            TaskWorkerVO vo = taskWorkerPOtoVO(re);
            recommendService.addUserInterestFactor(userId, vo.getTopics(), InterestFactor.ACCEPT);
            return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), vo);
        }
        return new ResultVO(ResultMessage.FAILED.getCode(), "超过上限或不可接受任务", null);
    }

    /**
     * 用户提交、更新任务
     * @param taskWorkerVO
     * @return
     */
    @PostMapping(value = "/tasks")
    public ResultVO submitTask(@RequestBody TaskWorkerVO taskWorkerVO){
        TaskWorker taskWorker = taskWorkerVOtoPO(taskWorkerVO);
        List<Tag> tags = new ArrayList<>();
        if(taskWorkerVO.getImages()!=null) {
            for (QuestionVO temp : taskWorkerVO.getImages()) {
                if(temp.getTags()!=null) {
                    for (TagVO tagVO : temp.getTags()) {
                        tags.add(tagVOToTag(tagVO, taskWorker.getTaskPublisherId(), temp.getFilename(), taskWorkerVO.getUserId(), taskWorkerVO.getId()));
                    }
                }
            }
            ResultMessage re = userService.submitTask(taskWorker, tags);
            return new ResultVO(re.getCode(), re.getValue(), null);
        }
        return new ResultVO(ResultMessage.FAILED.getCode(), ResultMessage.FAILED.getValue(), null);
    }

    /**
     * 用户删除任务
     * @param taskWorkerId
     * @return
     */
    @DeleteMapping(value = "/tasks/{taskId}")
    public ResultVO deleteTask(@PathVariable("taskId") Long taskWorkerId){
        ResultMessage re = userService.deleteTask(taskWorkerId);
        return new ResultVO(re.getCode(), re.getValue(), null);
    }

    /**
     * 用户签到
     * @param userId
     * @return
     */
    @GetMapping(value = "/{userId}/attend")
    public ResultVO attend(@PathVariable("userId") Long userId){
        ResultMessage re = userService.attend(userId);
        return new ResultVO(re.getCode(), re.getValue(), null);
    }

    /**
     * 用户充值
     * @param userId
     * @param amount
     * @return
     */
    @PostMapping(value = "/{userId}/recharge")
    public ResultVO recharge(@PathVariable("userId") Long userId, @RequestParam Double amount){
        System.out.println(amount);
        ResultMessage re = userService.recharge(userId, amount);
        return new ResultVO(re.getCode(), re.getValue(), null);
    }

    /**
     * 用户评价
     * @param taskWorkerId
     * @param rating
     * @return
     */
    @PostMapping(value = "/rating")
    public ResultVO taskRate(@RequestParam("taskWorkerId") Long taskWorkerId,
                             @RequestParam("rating") Integer rating){
        TaskWorker taskWorker = userService.findTaskWorkerById(taskWorkerId);
        taskWorker.setRating(rating);
        ResultMessage re = adminService.updateTaskWorker(taskWorker);
        if(re == ResultMessage.SUCCESS){
            ResultMessage resultMessage = userService.updateRating(taskWorkerId, rating);
            return new ResultVO(resultMessage.getCode(), resultMessage.getValue(), null);
        }
        return new ResultVO(re.getCode(), re.getValue(), null);
    }

    @GetMapping(value = "/{userId}/activity")
    public ResultVO getUserActivity(@PathVariable Long userId, @RequestParam Integer year){
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        List<Activity> activities = userService.findActivities(userId, startDate, endDate);
        if (activities == null) {
            return new ResultVO<>(1, "failed", null);
        } else {
            Map<Integer, Map<Integer, Integer>> activityMap = new HashMap(12);
            for (int i = 1; i <= 12; i++){
                activityMap.put(i, new HashMap<>());
            }
            activities.forEach(activity -> {
                LocalDate date = activity.getDate();
                activityMap.get(date.getMonthValue()).put(date.getDayOfMonth(), activity.getCount());
            });

            return new ResultVO<>(0, "success", activityMap);
        }
    }

    /**
     * 查找用户任务结果
     * @param userId
     * @return
     */
    @GetMapping(value = "/taskRecord/{userId}")
    public ResultVO getTaskRecordByUserId(@PathVariable Long userId){
        List<TaskRecord> taskRecords = userService.findTaskRecordByUserId(userId);
        List<TaskRecordVO> taskRecordVOS = new ArrayList<>();
        if(taskRecords != null){
            for(TaskRecord temp: taskRecords){
                TaskRecordVO vo = new TaskRecordVO(temp.getUserId(), temp.getTaskPublisherId(), temp.getDate(), temp.getPrice(), temp.getCorrect(), temp.getSum());
                taskRecordVOS.add(vo);
            }
        }
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), taskRecordVOS);
    }

    /**
     * 拿到任务奖励
     * @param recordId
     * @return
     */
    @GetMapping(value = "/{recordId}")
    public ResultVO getTaskReward(@PathVariable Long recordId){
        TaskRecord taskRecord = userService.findTaskRecordById(recordId);
        if(taskRecord != null){
            if(!taskRecord.getHaveSeen()) {
                User user = userService.findUserById(taskRecord.getUserId());
                user.setPoints(taskRecord.getPrice().longValue() + user.getPoints());
                user.setExp(taskRecord.getCorrect() + user.getExp());
                adminService.updateUser(user);

                taskRecord.setHaveSeen(true);
                userService.updateTaskRecord(taskRecord);
            }
            return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), null);
        }
        return new ResultVO(ResultMessage.FAILED.getCode(), ResultMessage.FAILED.getValue(), null);
    }

    private static TaskWorker taskWorkerVOtoPO(TaskWorkerVO taskWorkerVO){
        TaskWorker taskWorker = new TaskWorker(taskWorkerVO.getId(), taskWorkerVO.getTaskId(), taskWorkerVO.getUserId(), taskWorkerVO.getPrice(), taskWorkerVO.getStartDate(),
                null, taskWorkerVO.getRating());
        taskWorker.setTaskState(taskWorkerVO.getTaskState());
        return taskWorker;
    }

    private TaskWorkerVO taskWorkerPOtoVO(TaskWorker taskWorker){
        TaskPublisher taskPublisher = userService.findTaskPublisherById(taskWorker.getTaskPublisherId());
        List<QuestionVO> questions = new ArrayList<>();
        List<Tag> tags = userService.getFitTags(taskWorker.getId());
        if(taskPublisher.getImages()!=null) {
            for (String temp : taskPublisher.getImages()) {
                QuestionVO questionVO = new QuestionVO();
                List<TagVO> tagVOs = new ArrayList<>();
                questionVO.setFilename(temp);
                for(int i=0; i<tags.size(); i++){
                    if (tags.get(i)== null){
                        System.out.println("tags i null");
                    }
                    if (temp == null){
                        System.out.println("temp null");
                    }
                    if (tags == null){
                        System.out.println("tags null");
                    }
                    if(tags.get(i).getFileName().equals(temp)){
                        tagVOs.add(tagToTagVO(tags.get(i)));
                        tags.remove(i);
                        i--;
                    }
                }
                questionVO.setTags(tagVOs);
//                if (tags != null && tags.size() != 0) {
//                    List<TagVO> tagVOs = new ArrayList<>();
//                    for (Tag tagTmp : tags) {
//                        tagVOs.add(tagToTagVO(tagTmp));
//                    }
//                    questionVO.setTags(tagVOs);
//                }
                questions.add(questionVO);
            }
            TaskWorkerVO taskWorkerVO = new TaskWorkerVO(taskWorker.getId(), taskWorker.getTaskPublisherId(), taskWorker.getUserId(), taskPublisher.getTitle(),
                    taskPublisher.getDescription(), taskPublisher.getTaskType().getCode(), taskWorker.getPrice(), taskWorker.getTaskState(), questions,
                    taskPublisher.getLabels(), taskPublisher.getTopics(), taskWorker.getStartDate(), taskPublisher.getEndDate(), taskWorker.getRating());
            return taskWorkerVO;
        }
        return null;
    }

    private static TagVO tagToTagVO(Tag tag){
        TagVO tagVO = new TagVO();
        tagVO.setId(tag.getId());
        tagVO.setColor(tag.getColor());

        tagVO.setTagType(tag.getTagType().getCode());
        if(tag.getTagType()== TagType.IRREGULAR){
            IrregularTag irregularTag = (IrregularTag)tag;
            tagVO.setPenPoints(irregularTag.getPoints());
        }
        else if(tag.getTagType()==TagType.RECT){
            RecTag recTag = (RecTag)tag;
            tagVO.setStartPosition(recTag.getStart());
            tagVO.setEndPosition(recTag.getEnd());
        }

        TagDesc tagDesc = tag.getDescription();
        tagVO.setDescType(tagDesc.getTagDescType().getCode());
        if(tagDesc.getTagDescType()== TagDescType.Multi){
            TagMultiDesc tagMultiDesc = (TagMultiDesc)tagDesc;
            tagVO.setMapDesc(tagMultiDesc.getDescriptions());
        }
        else{
            TagSingleDesc tagSingleDesc = (TagSingleDesc)tagDesc;
            tagVO.setSingleDesc(tagSingleDesc.getDescription());
        }

        return tagVO;
    }

    private static Tag tagVOToTag(TagVO tagVO, Long taskId, String filename, Long userId, Long taskWorkerId){
        Tag tag = new Tag(tagVO.getId(), taskId, taskWorkerId, filename, userId, tagVO.getColor());

        if(tagVO.getDescType().equals(TagDescType.SINGLE.getCode())){
            TagSingleDesc tagSingleDesc = new TagSingleDesc();
            tagSingleDesc.setTagDescType(TagDescType.SINGLE);
            tagSingleDesc.setDescription(tagVO.getSingleDesc());

            tag.setDescription(tagSingleDesc);
        }
        else{
            TagMultiDesc tagMultiDesc = new TagMultiDesc();
            tagMultiDesc.setTagDescType(TagDescType.Multi);
            tagMultiDesc.setDescriptions(tagVO.getMapDesc());

            tag.setDescription(tagMultiDesc);
        }

        if(tagVO.getTagType().equals(TagType.RECT.getCode())){
            RecTag recTag = new RecTag(tag.getId(), tag.getTaskPublisherId(), tag.getTaskWorkerId(), tag.getFileName(), tag.getUserId(), tag.getDescription(), tag.getColor(), TagType.RECT, tagVO.getStartPosition(), tagVO.getEndPosition());
            return recTag;
        }
        else if(tagVO.getTagType().equals(TagType.IRREGULAR.getCode())){
            IrregularTag irregularTag = new IrregularTag(tag.getId(), tag.getTaskPublisherId(), tag.getTaskWorkerId(), tag.getFileName(), tag.getUserId(), tag.getDescription(), tag.getColor(), TagType.IRREGULAR, (ArrayList<Point>) tagVO.getPenPoints());
            return irregularTag;
        }
        else{
            tag.setTagType(TagType.SIMPLE);
            return tag;
        }
    }
}
