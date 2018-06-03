package horizon.taglib.controller;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.TaskState;
import horizon.taglib.model.Tag;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.TaskWorker;
import horizon.taglib.model.User;
import horizon.taglib.service.AdminService;
import horizon.taglib.service.StatisticsService;
import horizon.taglib.service.UserService;
import horizon.taglib.vo.ResultVO;
import horizon.taglib.vo.UserStatisticVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Result;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping(value = "/statistics")
public class StatisticsController {

    @Autowired
    StatisticsService statisticsService;

    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

    /**
     * taskPublisher任务类别数据
     * @return
     */
    @GetMapping(value = "/taskPublisher/taskType")
    public ResultVO getTaskTypeStatistics(){
        //Map<String, Long>
        List<TaskPublisher> taskPublisherList = statisticsService.getAllTaskPublishers();
        Map<String, Long> res = new HashMap<>();

        if(taskPublisherList!=null){
            for(TaskPublisher temp: taskPublisherList){
                List<String> types = temp.getTopics();
                if(types!=null){
                    for(String type: types){
                        if(res.containsKey(type)){
                            res.replace(type, res.get(type)+1 );
                        }
                        else{
                            res.put(type, new Long(1));
                        }
                    }
                }
            }
        }

        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), res);
    }

    /**
     * taskPublisher任务状态数据
     * @return
     */
    @GetMapping(value = "/taskPublisher/state")
    public ResultVO getTaskPublisherStateStatistics(){
        //Map<String, Long>
        List<TaskPublisher> taskPublisherList = statisticsService.getAllTaskPublishers();
        Map<String, Long> res = new LinkedHashMap<>();

        res.put(TaskState.SUBMITTED.getValue(), new Long(0));
        res.put(TaskState.REJECT.getValue(), new Long(0));
        res.put(TaskState.PROCESSING.getValue(), new Long(0));
        res.put(TaskState.DONE.getValue(), new Long(0));
        res.put(TaskState.OVERTIME.getValue(), new Long(0));

        if(taskPublisherList!=null){
            for(TaskPublisher temp: taskPublisherList){
                String state = temp.getTaskState().getValue();
                res.replace(state, res.get(state)+1);
            }
        }

        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), res);
    }

    /**
     * taskWorker任务状态数据
     * @return
     */
    @GetMapping(value = "/taskWorker/state")
    public ResultVO getTaskWorkerStateStatistics(){
        //Map<String, Long>
        List<TaskWorker> taskWorkerList = statisticsService.getAllTaskWorkers();
        Map<String, Long> res = new HashMap<>();

        res.put(TaskState.PROCESSING.getValue(), new Long(0));
        res.put(TaskState.SUBMITTED.getValue(), new Long(0));
        res.put(TaskState.PASS.getValue(), new Long(0));
        res.put(TaskState.REJECT.getValue(), new Long(0));
        res.put(TaskState.OVERTIME.getValue(), new Long(0));

        if(taskWorkerList!=null){
            for(TaskWorker temp:taskWorkerList){
                String state = temp.getTaskState().getValue();
                res.replace(state, res.get(state)+1);
            }
        }

        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), res);
    }

    /**
     * taskPublisher进度数据
     * @return
     */
    @GetMapping(value = "/taskPublisher/progress")
    public ResultVO getTaskPublisherProgress(@RequestParam("taskPublisherId") Long taskPublisherId){
        //Double
        TaskPublisher taskPublisher = adminService.getTaskPublisherById(taskPublisherId);
        List<TaskWorker> taskWorkerList = statisticsService.getAllTaskWorkers();
        Long count = new Long(0);
        double res = 0.0;

        if(taskWorkerList!=null){
            for(TaskWorker temp: taskWorkerList){
                if(temp.getTaskPublisherId() == taskPublisherId && temp.getTaskState() == TaskState.SUBMITTED){
                    count++;
                }
            }
        }

        if(taskPublisher.getNumberPerPicture()!=0){
            res = count*1.0/taskPublisher.getNumberPerPicture();
        }

        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), res);
    }

    /**
     * taskWorker进度数据
     * @return
     */
    @GetMapping(value = "/taskWorker/progress")
    public ResultVO getTaskWorkerProgress(@RequestParam("taskWorkerId") Long taskWorkerId){
        //Double
        TaskWorker taskWorker = userService.findTaskWorkerById(taskWorkerId);
        double res = 0.0;
        if(taskWorker.getTaskPublisherId()!=null){
            TaskPublisher taskPublisher = adminService.getTaskPublisherById(taskWorker.getTaskPublisherId());
            List<String> pics = taskPublisher.getImages();
            List<Tag> tags = userService.getFitTags(taskWorkerId);
            if(pics!=null){
                for(String temp: pics){
                    for(Tag tag: tags){
                        if(tag.getFileName().equals(temp)){
                            res = res + 1;
                            break;
                        }
                    }
                }

                res = res/pics.size();
            }
        }

        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), res);
    }

    /**
     * taskPublisher评分
     * @param taskPublisherId
     * @return
     */
    @GetMapping(value = "/taskPublisher/rating")
    public ResultVO getTaskPublisherRating(@RequestParam("taskPublisherId") Long taskPublisherId){
        //Map<Integer, Long>
        List<TaskWorker> taskWorkerList = statisticsService.getAllTaskWorkers();
        Map<Integer, Long> res = new HashMap<>();

        res.put(1, new Long(0));
        res.put(2, new Long(0));
        res.put(3, new Long(0));
        res.put(4, new Long(0));
        res.put(5, new Long(0));

        if(taskWorkerList!=null){
            for(TaskWorker temp: taskWorkerList){
                if(temp.getTaskPublisherId() == taskPublisherId){
                    if(temp.getRating()!=0) {
                        res.replace(temp.getRating(), res.get(temp.getRating()) + 1);
                    }
                }
            }
        }

        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), res);
    }

    /**
     * 参与taskPublisher的用户的信息
     * @param taskPublisherId
     * @return
     */
    @GetMapping(value = "/taskPublisher/workerInfo")
    public ResultVO getTaskWorkerInfo(@RequestParam("taskPublisherId") Long taskPublisherId){
        //新建VO 等级，经验，积分，准时度，准确度
        List<User> userList = statisticsService.allParticipateUsersById(taskPublisherId);
        UserStatisticVO userStatisticVO = new UserStatisticVO(0.0, 0.0, 0.0, 0.0, 0.0);

        if(userList!=null){
            double level = 0.0;
            double exp = 0.0;
            double point = 0.0;
            double accuracyRate = 0.0;
            double punctualityRate = 0.0;

            for(User temp: userList){
                level = level + temp.getLevel().getCode();
                exp = exp + temp.getExp();
                point = point + temp.getPoints();
                accuracyRate = accuracyRate + temp.getAccuracyRate();
                punctualityRate = punctualityRate + temp.getPunctualityRate();
            }

            if(userList.size()!=0){
                level = level/userList.size();
                exp = exp/userList.size();
                point = point/userList.size();
                accuracyRate = accuracyRate/userList.size();
                punctualityRate = punctualityRate/userList.size();
                userStatisticVO = new UserStatisticVO(level, exp, point, accuracyRate, punctualityRate);
            }
        }

        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), userStatisticVO);
    }

    /**
     * 用户接受的task的类型统计
     * @param userId
     * @return
     */
    @GetMapping(value = "/taskWorker/taskType")
    public ResultVO getUserTaskType(@RequestParam("userId") Long userId){
        //Map<String, Long>
        List<TaskWorker> taskWorkerList = statisticsService.getTaskWorkersByUserId(userId);
        Map<String, Long> res = new HashMap<>();

        if(taskWorkerList!=null){
            for(TaskWorker temp: taskWorkerList){
                TaskPublisher taskPublisher = adminService.getTaskPublisherById(temp.getTaskPublisherId());
                List<String> topics = taskPublisher.getTopics();

                if(topics!=null){
                    for(String topic: topics){
                        if(!res.containsKey(topic)){
                            res.put(topic, new Long(1));
                        }
                        else{
                            res.replace(topic, res.get(topic)+1);
                        }
                    }
                }
            }
        }

        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), res);
    }

    @GetMapping(value = "/user/tendency")
    public ResultVO getUserTendency(@RequestParam("startDate") String startDate,
                                    @RequestParam("endDate") String endDate,
                                    @RequestParam("interval") Integer interval){
        Map<String, Long> res = new LinkedHashMap<>();

        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, sdf);
        LocalDate end = LocalDate.parse(endDate, sdf);
//        LocalDate startCopy = LocalDate.parse(startDate, sdf);
//        if(start.plusDays(interval-1).isAfter(end)){
//            Long amount = statisticsService.countNewUserByDates(start, end);
//            res.put(startDate, amount);
//        }
//        else{
//
//        }
        while(start.isEqual(end) || start.isBefore(end)){
            Long amount = statisticsService.countNewUserByDates(start, start.plusDays(interval-1));
            res.put(start.format(sdf), amount);
            start = start.plusDays(interval);
        }
//        if(!start.isEqual(end)){
//            Long amount = statisticsService.countNewUserByDates(start, end);
//            res.put(start.format(sdf), amount);
//        }
        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), res);
    }
}
