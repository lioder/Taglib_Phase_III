package horizon.taglib.controller;

import horizon.taglib.dto.PageDTO;
import horizon.taglib.enums.*;
import horizon.taglib.model.*;
import horizon.taglib.service.TaskService;
import horizon.taglib.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class TaskController {

    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService){ this.taskService = taskService;}

    /**
     * 得到所有自己发布的task
     * @return ResultVO
     */
    @GetMapping(value = "/tasks/list")
    public ResultVO findTaskPublisherByState(@RequestParam("userId") Long userId,
                                             @RequestParam("state") TaskState taskState,
                                             @RequestParam("size") Integer size,
                                             @RequestParam("page") Integer currentPage){
        PageDTO bean = taskService.findTaskPublisherByState(userId, taskState, size, currentPage);
        if(bean.getPageData()!=null){
            List<TaskPublisher> taskPublishers = bean.getPageData();
            List<TaskInfoVO> taskInfoVOs = new ArrayList<>();
            for(TaskPublisher temp: taskPublishers){
                if(temp.getImages()!=null) {
                    String picName = temp.getImages().get(0);
                    Integer picNum = temp.getImages().size();
                TaskInfoVO taskInfoVO = new TaskInfoVO(temp.getId(), temp.getTitle(), temp.getDescription(), picName, picNum, temp.getTaskType().getCode(), temp.getTopics(),
                        temp.getPrice(), temp.getStartDate(), temp.getEndDate(), temp.getRating());
                    taskInfoVOs.add(taskInfoVO);
                }
                //拿到的task数据集为空
                else {
                    return new ResultVO<>(ResultMessage.FAILED.getCode(), ResultMessage.FAILED.getValue(), null);
                }
            }
            PageVO page = new PageVO(bean.getCurrentPage(), bean.getSize(), bean.getTotalSize(), taskInfoVOs);
            return new ResultVO<>(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), page);
        }
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), new PageVO<>());
    }

    /**
     * 根据task的id得到对应的taskPublisher
     * @param id
     * @return ResultVO
     */
    @GetMapping(value = "/tasks/{id}")
    public ResultVO<TaskPublisherVO> getTaskPublisherByID(@PathVariable("id") Long id){
        TaskPublisher taskPublisher = taskService.getTaskPublisherById(id);
        TaskPublisherVO taskPublisherVO = taskPublisherToTaskPublisherVO(taskPublisher);
        return new ResultVO<>(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), taskPublisherVO);
    }

//    /**
//     * 新建和更新标签
//     * @param id
//     * @param questions
//     */
//    @PostMapping(value = "/tasks/{id}/tags")
//    public String addTags(@PathVariable("id") Long id, @RequestBody List<QuestionVO> questions){
//        if(questions!=null){
//            for(QuestionVO temp: questions){
//                if(temp.getTags()!=null){
//                    for(TagVO tagVO: temp.getTags()){
//                        if(tagVO.getId() == 0) {
//                            ResultMessage message = taskService.addTag(tagVOToTag(tagVO, id, temp.getFilename()));
//                            if (message == ResultMessage.FAILED) {
//                                return "add tag failed";
//                            }
//                        }
//                        else{
//                            ResultMessage message = taskService.updateTag(tagVOToTag(tagVO, id, temp.getFilename()));
//                            if(message == ResultMessage.FAILED){
//                                return "update tag failed";
//                            }
//                        }
//                    }
//                }
//            }
//            return "success";
//        }
//        else{
//            return "fail";
//        }
//    }

//    /**
//     * 删除tag
//     * @param tagId
//     * @return
//     */
//    @DeleteMapping(value = "/tasks/{id}")
//    public String deleteTag(@RequestParam(value ="tagId", required = true) Long tagId){
//        ResultMessage re = taskService.deleteTag(tagId);
//        if(re == ResultMessage.SUCCESS){
//            return "delete success";
//        }
//        else{
//            return "delete failed";
//        }
//    }

    /**
     * 上传task
     * @param taskPublisherVO
     * @return
     */
    @PostMapping(value = "/tasks/new")
    public ResultVO<TaskInfoVO> uploadTask(@RequestBody TaskPublisherVO taskPublisherVO){
        TaskPublisher taskPublisher = new TaskPublisher(taskPublisherVO.getUserId(), taskPublisherVO.getTitle(), taskPublisherVO.getDescription(),
                getTaskType(taskPublisherVO.getTaskType()), taskPublisherVO.getImages(), taskPublisherVO.getLabels(), taskPublisherVO.getTopics(),
                taskPublisherVO.getPrice() , taskPublisherVO.getNumPerPic(), taskPublisherVO.getStartDate(), taskPublisherVO.getEndDate(), taskPublisherVO.getOptions());
        List<Object> list = taskService.addTask(taskPublisher);
        if(list!=null) {
            ResultMessage re = (ResultMessage) list.get(0);
            TaskInfoVO taskInfoVO = null;
            if (re == ResultMessage.SUCCESS) {
                taskInfoVO = new TaskInfoVO((Long)list.get(1), taskPublisher.getTitle(), taskPublisher.getDescription(), taskPublisher.getImages().get(0),
                        taskPublisher.getImages().size(), taskPublisher.getTaskType().getCode(), taskPublisher.getTopics(), taskPublisher.getPrice(), taskPublisher.getStartDate(),
                        taskPublisher.getEndDate(), taskPublisher.getRating());
            }
            return new ResultVO<>(re.getCode(), re.getValue(), taskInfoVO);
        }
        return new ResultVO<>(ResultMessage.FAILED.getCode(), ResultMessage.FAILED.getValue(), null);
    }

    @GetMapping(value = "/tasks/{taskPublisherId}/worker-rank")
    public ResultVO getRecordByTaskPublisherId(@PathVariable Long taskPublisherId) {
        Map<Long, Double> userRecord = taskService.getAllTaskRecordsByTaskPublisherId(taskPublisherId);
        if (userRecord != null) {
            return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), userRecord);
        }
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), new LinkedHashMap<>());
    }

//    /**
//     * 得到新的taskPublisherId
//     * @return
//     */
//    @GetMapping(value = "/tasks/new")
//    public ResultVO newTask(){
//        Long newTaskId = taskService.getNewTaskId();
//        if(newTaskId != null){
//            return new ResultVO(ResultMessage.SUCCESS.getCode(), "success", newTaskId);
//        }
//        else{
//            return new ResultVO(ResultMessage.FAILED.getCode(), "failed", null);
//        }
//    }

//    /**
//     * 根据关键字搜索用户未接过的任务
//     * @param keyword
//     * @param sortBy
//     * @param isSec
//     * @param size
//     * @param currentPage
//     * @return
//     */
//    @GetMapping(value = "/tasks/search")
//    public ResultVO searchTask(@RequestParam(value = "keyword") String keyword,
//                               @RequestParam(value = "sortBy") String sortBy,
//                               @RequestParam(value = "isSec") Boolean isSec,
//                               @RequestParam(value = "size") Integer size,
//                               @RequestParam(value = "page") Integer currentPage,
//                               @RequestParam(value = "userId") Long userId){
//        return null;
//    }

    public TaskPublisherVO taskPublisherToTaskPublisherVO(TaskPublisher taskPublisher){
        TaskPublisherVO taskPublisherVO = new TaskPublisherVO(taskPublisher.getId(), taskPublisher.getUserId(), taskPublisher.getTitle(),
                taskPublisher.getDescription(), taskPublisher.getTaskType().getCode() , taskPublisher.getImages(), taskPublisher.getLabels(), taskPublisher.getTopics(),
                taskPublisher.getPrice(), taskPublisher.getNumberPerPicture(), taskPublisher.getStartDate(), taskPublisher.getEndDate(), taskPublisher.getTaskState(),
                taskPublisher.getRating(), taskPublisher.getHotCount(), taskPublisher.getHotRank(), taskPublisher.getOptions());
        return taskPublisherVO;
    }

    private TaskPublisher taskPublisherVOToTaskPublisher(TaskPublisherVO taskPublisherVO){
        TaskType taskType = getTaskType(taskPublisherVO.getTaskType());
        TaskPublisher taskPublisher = new TaskPublisher(taskPublisherVO.getId(), taskPublisherVO.getUserId(), taskPublisherVO.getTitle(), taskPublisherVO.getDescription(),
                taskType, taskPublisherVO.getTaskState(), taskPublisherVO.getImages(), taskPublisherVO.getLabels(), taskPublisherVO.getTopics(), taskPublisherVO.getPrice(),
                taskPublisherVO.getNumPerPic(), taskPublisherVO.getStartDate(), taskPublisherVO.getEndDate(), taskPublisherVO.getRating(), taskPublisherVO.getHotCount(),
                taskPublisherVO.getHotRank());
        return taskPublisher;
    }

    private TaskType getTaskType(Integer code){
        TaskType taskType = null;
        if(code == TaskType.SORT.getCode()){
            taskType = TaskType.SORT;
        }
        else if(code == TaskType.BOX.getCode()){
            taskType = TaskType.BOX;
        }
        else{
            taskType = TaskType.REGION;
        }
        return taskType;
    }
}
