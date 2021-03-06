package horizon.taglib.controller;

import horizon.taglib.dto.PageDTO;
import horizon.taglib.enums.ApplyState;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.TaskState;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.TaskWorker;
import horizon.taglib.model.User;
import horizon.taglib.service.AdminService;
import horizon.taglib.service.TaskService;
import horizon.taglib.service.UserService;
import horizon.taglib.vo.PageVO;
import horizon.taglib.vo.ResultVO;
import horizon.taglib.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;

    /**
     * 查看所有工人用户列表
     * @param size
     * @param currentPage
     * @param keyword
     * @param sortBy
     * @param isSec
     * @return
     */
    @GetMapping(value = "/list/worker")
    public ResultVO showWorkerList(@RequestParam("size") Integer size,
                                 @RequestParam("page") Integer currentPage,
                                 @RequestParam("keyword") String keyword,
                                 @RequestParam("sortBy") String sortBy,
                                 @RequestParam("isSec") Boolean isSec){
        PageDTO dto = adminService.showWorkerList(keyword, sortBy, isSec, size, currentPage);
        List<User> userList = dto.getPageData();
        List<UserVO> userVOS = new ArrayList<>();
        if(userList!=null){
            for(User temp: userList){
                UserVO vo = new UserVO(temp.getId(), temp.getUsername(), temp.getPassword(), temp.getPhoneNumber(), temp.getEmail(), temp.getUserType().getCode(),
                        temp.getPoints(), temp.getAvatar(), temp.getLevel(), temp.getExp(), temp.getAccuracyRate(), temp.getPunctualityRate(), temp.getSatisfactionRate(),
                        temp.getIsAttendant(), temp.getApplyState());
                userVOS.add(vo);
            }
        }
        PageVO<UserVO> page = new PageVO<UserVO>(dto.getCurrentPage(), dto.getSize(), dto.getTotalSize(), sortBy, isSec, userVOS);
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), page);
    }

    /**
     * 查看所有发起者用户列表
     * @param size
     * @param currentPage
     * @param keyword
     * @param sortBy
     * @param isSec
     * @return
     */
    @GetMapping(value = "/list/publisher")
    public ResultVO showPublisherList(@RequestParam("size") Integer size,
                                      @RequestParam("page") Integer currentPage,
                                      @RequestParam("keyword") String keyword,
                                      @RequestParam("sortBy") String sortBy,
                                      @RequestParam("isSec") Boolean isSec){
        PageDTO dto = adminService.showPublisherList(keyword, sortBy, isSec, size, currentPage);
        List<User> userList = dto.getPageData();
        List<UserVO> userVOS = new ArrayList<>();
        if(userList!=null){
            for(User temp: userList){
                UserVO vo = new UserVO(temp.getId(), temp.getUsername(), temp.getPassword(), temp.getPhoneNumber(), temp.getEmail(), temp.getUserType().getCode(),
                        temp.getPoints(), temp.getAvatar(), temp.getLevel(), temp.getExp(), temp.getAccuracyRate(), temp.getPunctualityRate(), temp.getSatisfactionRate(),
                        temp.getIsAttendant(), temp.getApplyState());
                userVOS.add(vo);
            }
        }
        PageVO<UserVO> page = new PageVO<UserVO>(dto.getCurrentPage(), dto.getSize(), dto.getTotalSize(), sortBy, isSec, userVOS);
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), page);
    }

    /**
     * 依据状态的任务列表
     * @param taskState（状态为发起的或者是提交的）
     * @return
     */
    @GetMapping(value = "/tasks")
    public ResultVO showTaskList(@RequestParam("state") TaskState taskState,
                                 @RequestParam("size") Integer size,
                                 @RequestParam("page") Integer currentPage){
        return null;
    }

//    /**
//     * 查看所有待审核的taskPublisher
//     * @param size
//     * @param currentPage
//     * @return
//     */
//    @GetMapping(value = "/checkTask")
//    public ResultVO getAllSubmittedTaskPublisher(@RequestParam("size") Integer size,
//                                                 @RequestParam("page") Integer currentPage){
//
//        return null;
//    }
//
//    /**
//     * 查看所有待审核的taskWorker
//     * @param size
//     * @param currentPage
//     * @return
//     */
//    @GetMapping(value = "/checkTag")
//    public ResultVO getAllSubmittedTaskWorker(@RequestParam("size") Integer size,
//                                              @RequestParam("page") Integer currentPage){
//        return null;
//    }

    /**
     * 审核众包发起者发起的任务
     * @return
     */
    @PostMapping(value = "/checkTask/{taskPublisherId}")
    public ResultVO checkTask(@PathVariable("taskPublisherId") Long taskPublisherId,
                              @RequestParam("checkResult") Boolean checkResult){
        TaskPublisher taskPublisher = adminService.getTaskPublisherById(taskPublisherId);
        User user = userService.findUserById(taskPublisher.getUserId());
        if(user.getPoints() < taskPublisher.getPrice()){
            checkResult = false;
        }
        if(taskPublisher != null){
            if(checkResult) {
                // 审核通过后设置为PASS，等待任务被专家完成
                taskPublisher.setTaskState(TaskState.PASS);
                user.setPoints(user.getPoints() - taskPublisher.getPrice().longValue());
                ResultMessage re = adminService.updateUser(user);
            }
            else{
                taskPublisher.setTaskState(TaskState.REJECT);
            }
            ResultMessage re = adminService.updateTaskPublisher(taskPublisher);
            return new ResultVO(re.getCode(), re.getValue(), null);
        }
        return new ResultVO(ResultMessage.FAILED.getCode(), ResultMessage.FAILED.getValue(), null);
    }

    /**
     * 审核专家
     * @param userId
     * @param checkResult
     * @return
     */
    @GetMapping(value = "/check/pro/{userId}")
    public ResultVO checkProfession(@PathVariable Long userId,
                                    @RequestParam("checkResult") Boolean checkResult){
        if(checkResult){
            User user = userService.findUserById(userId);
            user.setApplyState(ApplyState.PASS);
            adminService.updateUser(user);
        } else {
            User user = userService.findUserById(userId);
            user.setApplyState(ApplyState.NOT_YET);
            adminService.updateUser(user);
        }
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), null);
    }

    @GetMapping(value = "/check/pro")
    public ResultVO showApplyingProList(){
        List<User> applyingPros = adminService.showApplyingProList();
        List<UserVO> userVOS = new ArrayList<>();
        applyingPros.forEach(temp -> {
            UserVO vo = new UserVO(temp.getId(), temp.getUsername(), temp.getPassword(), temp.getPhoneNumber(), temp.getEmail(), temp.getUserType().getCode(),
                    temp.getPoints(), temp.getAvatar(), temp.getLevel(), temp.getExp(), temp.getAccuracyRate(), temp.getPunctualityRate(), temp.getSatisfactionRate(),
                    temp.getIsAttendant(), temp.getApplyState());
            userVOS.add(vo);
        });
        return new ResultVO<>(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), userVOS);
    }
    /**
     * 审核标注结果
     * @param taskWorkerId
     * @param checkResult
     * @return
     */
//    @PostMapping(value = "/checkTag/{taskWorkerId}")
//    public ResultVO checkTag(@PathVariable("taskWorkerId") Long taskWorkerId,
//                             @RequestParam("checkResult") double checkResult){
//        TaskWorker taskWorker = userService.findTaskWorkerById(taskWorkerId);
//        User user = userService.findUserById(taskWorker.getUserId());
//        if(checkResult >= 0.6){
//            taskWorker.setTaskState(TaskState.PASS);
//            user.setPoints(user.getPoints() + taskWorker.getPrice().longValue() );
//            TaskPublisher taskPublisher = userService.findTaskPublisherById(taskWorker.getTaskPublisherId());
//            user.setExp(user.getExp() + taskPublisher.getImages().size()*1);
//
//            Long completeSize = adminService.getFinishedTaskWorkersNumber(taskWorker.getTaskPublisherId());
//            if(completeSize+1 >= taskPublisher.getNumberPerPicture()){
//                taskPublisher.setTaskState(TaskState.DONE);
//                ResultMessage re = adminService.updateTaskPublisher(taskPublisher);
//                if(re == ResultMessage.FAILED){
//                    return new ResultVO(ResultMessage.FAILED.getCode(), "taskPublisher更新失败", null);
//                }
//            }
//        }
//        else{
//            taskWorker.setTaskState(TaskState.REJECT);
//        }
//        Long size = adminService.getExaminedTasksNumber(taskWorker.getUserId());
//        Double accuracyRate = (user.getAccuracyRate()*(size-1)+checkResult)/ size;
//        user.setAccuracyRate(accuracyRate);
//        ResultMessage taskRe = adminService.updateTaskWorker(taskWorker);
//        ResultMessage userRe = adminService.updateUser(user);
//        if(taskRe == ResultMessage.SUCCESS && userRe == ResultMessage.SUCCESS){
//            return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), null);
//        }
//        else if(taskRe == ResultMessage.FAILED){
//            return new ResultVO(ResultMessage.FAILED.getCode(), "任务状态更新失败", null);
//        }
//        else{
//            return new ResultVO(ResultMessage.FAILED.getCode(), "用户信息更新失败", null);
//        }
//    }
}
