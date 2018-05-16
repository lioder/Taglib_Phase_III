package horizon.taglib.controller;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.TaskWorker;
import horizon.taglib.service.AdminService;
import horizon.taglib.service.RecommendService;
import horizon.taglib.service.StatisticsService;
import horizon.taglib.vo.ResultVO;
import horizon.taglib.vo.TaskInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    StatisticsService statisticsService;

    @Autowired
    AdminService adminService;

    @Autowired
    RecommendService recommendService;

    /**
     * 个性化推荐任务
     * @return
     */
    @GetMapping(value = "/topic")
    public ResultVO getPersonalizedTask(@RequestParam("userId") Long userId){
        List<TaskWorker> taskWorkerList = statisticsService.getTaskWorkersByUserId(userId);
        List<String> topics = new ArrayList<>();

        if(taskWorkerList!=null){
            for(TaskWorker temp: taskWorkerList){
                TaskPublisher taskPublisher = adminService.getTaskPublisherById(temp.getTaskPublisherId());
                List<String> taskTopics = taskPublisher.getTopics();

                if(taskTopics!=null){
                    for(String topic: taskTopics){
                        if(!topics.contains(topic)){
                            topics.add(topic);
                        }
                    }
                }
            }
        }

        List<TaskPublisher> taskPublisherList = recommendService.getPersonalizedTasks(topics, userId);
        List<TaskInfoVO> taskInfoVOS = new ArrayList<>();

        if(taskPublisherList!=null){
            for(TaskPublisher temp: taskPublisherList){
                TaskInfoVO taskInfoVO = taskPublisherToTaskInfoVO(temp);
                taskInfoVOS.add(taskInfoVO);
            }
        }

        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), taskInfoVOS);
    }

    /**
     * 拿到最热的任务
     * @return
     */
    @GetMapping(value = "/hotTask")
    public ResultVO getHottestTasks(@RequestParam("userId") Long userId){
        List<TaskPublisher> taskPublisherList = recommendService.getHotestTasks(userId);
        List<TaskInfoVO> taskInfoVOList = new ArrayList<>();

        if(taskPublisherList!=null){
            for(TaskPublisher temp:taskPublisherList){
                TaskInfoVO vo = taskPublisherToTaskInfoVO(temp);
                taskInfoVOList.add(vo);
            }
        }

        return new ResultVO(ResultMessage.SUCCESS.getCode(), ResultMessage.SUCCESS.getValue(), taskInfoVOList);
    }

    /**
     * 基于用户对任务评分相似度的推荐
     * @param userId
     * @return
     */
    @GetMapping(value = "/user")
    public ResultVO getUserRatingSimilarityTasks(@RequestParam("userId") Long userId){
        return null;
    }

    /**
     * 基于任务相似度的推荐
     * @param taskPublisherId
     * @return
     */
    @GetMapping(value = "/item")
    public ResultVO getItemSimilarityTasks(@RequestParam("taskPublisherId") Long taskPublisherId){
        return null;
    }

    private static TaskInfoVO taskPublisherToTaskInfoVO(TaskPublisher temp){
        double price = temp.getPrice()/temp.getNumberPerPicture();
        TaskInfoVO taskInfoVO = new TaskInfoVO(temp.getId(), temp.getTitle(), temp.getDescription(), temp.getImages().get(0), temp.getImages().size(),
                temp.getTaskType().getCode(), temp.getTopics(), price, temp.getStartDate(), temp.getEndDate(), temp.getRating());
        return taskInfoVO;
    }

}
