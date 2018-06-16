package horizon.taglib.service.impl;

import horizon.taglib.dao.*;
import horizon.taglib.dto.PageDTO;
import horizon.taglib.enums.*;
import horizon.taglib.model.*;
import horizon.taglib.service.AdminService;
import horizon.taglib.utils.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {

    private TaskPublisherDao taskPublisherDao;
    private UserDao userDao;
    /**
     * 查找待审核的专家
     * @return
     */
    private TaskWorkerDao taskWorkerDao;
    private TagDao tagDao;
    private TaskRecordDao taskRecordDao;

    @Autowired
    public AdminServiceImpl(TaskPublisherDao taskPublisherDao,UserDao userDao,TaskWorkerDao taskWorkerDao,TagDao tagDao, TaskRecordDao taskRecordDao){
        this.taskPublisherDao = taskPublisherDao;
        this.userDao = userDao;
        this.taskWorkerDao = taskWorkerDao;
        this.tagDao = tagDao;
        this.taskRecordDao = taskRecordDao;
    }

    @Override
    public PageDTO showWorkerList(String keywords , String sortBy , Boolean isSec , Integer size , Integer currentPage ){
        List<User> users = userDao.findAll();
        List<User> workers = new ArrayList<>();
        for(User user:users){
            if(user.getUserType()== UserType.WORKER){
                workers.add(user);
            }
        }

        List<User> res = searchByKeywords(keywords,workers);

        switch (sortBy){
            case "经验值":
                Sort.SortByExp sortByExp = new Sort.SortByExp();
                Collections.sort(res,sortByExp);
                break;
            case "积分":
                Sort.SortByPoints sortByPoints = new Sort.SortByPoints();
                Collections.sort(res,sortByPoints);
                break;
            case "准确度":
                Sort.SortByAccuracyRate sortByAccuracyRate = new Sort.SortByAccuracyRate();
                Collections.sort(res,sortByAccuracyRate);
                break;
            case "客户满意度":
                Sort.SortByWorkerSatisfaction sortByWorkerSatisfaction = new Sort.SortByWorkerSatisfaction();
                Collections.sort(res,sortByWorkerSatisfaction);
                break;
            case "全部":
                break;
            default:
                break;
        }

        if(isSec==false){
            Collections.reverse(res);
        }

        PageDTO<User> pageDTO = new PageDTO<>(currentPage,size, res.size());
        Integer dataIndex = pageDTO.getDataIndex();
        if(currentPage*size<res.size()){
            pageDTO.setPageData(res.subList(dataIndex - 1, dataIndex - 1 + size));
        }else{
            pageDTO.setPageData(res.subList(dataIndex-1,res.size()));
        }
        return pageDTO;
    }

    @Override
    public PageDTO showPublisherList(String keywords , String sortBy , Boolean isSec , Integer size , Integer currentPage ){
        List<User> users = userDao.findAll();
        List<User> publishers = new ArrayList<>();
        for(User user:users){
            if(user.getUserType()==UserType.REQUESTOR){
                publishers.add(user);
            }
        }

        List<User> res = searchByKeywords(keywords,publishers);

        switch (sortBy){
            case "积分":
                Sort.SortByPoints sortByPoints = new Sort.SortByPoints();
                Collections.sort(res,sortByPoints);
                break;
            case "经验值":
                Sort.SortByExp sortByExp = new Sort.SortByExp();
                Collections.sort(res,sortByExp);
                break;
            case "任务量":
                Sort.SortByTaskNum sortByTaskNum = new Sort.SortByTaskNum();
                Collections.sort(res,sortByTaskNum);
                break;
            case "全部":
                break;
            default:
                break;
        }

        if(isSec==false){
            Collections.reverse(res);
        }

        PageDTO<User> pageDTO = new PageDTO<>(currentPage,size,res.size());
        Integer dataIndex = pageDTO.getDataIndex();
        if(currentPage*size<res.size()){
            pageDTO.setPageData(res.subList(dataIndex - 1, dataIndex - 1 + size));
        }else{
            pageDTO.setPageData(res.subList(dataIndex-1,res.size()));
        }
        return pageDTO;
    }

    @Override
    public TaskPublisher getTaskPublisherById(long taskId){
        return taskPublisherDao.findOne(taskId);
    }

    @Override
    public ResultMessage updateTaskPublisher(TaskPublisher taskPublisher){
        TaskPublisher taskPublisher1 = taskPublisherDao.save(taskPublisher);
        if(taskPublisher1==null){
            return ResultMessage.NOT_EXIST;
        }else {
            return ResultMessage.SUCCESS;
        }
    }

    @Override
    public ResultMessage updateUser(User user) {
        User user1 = userDao.save(user);
        if(user1==null){
            return ResultMessage.NOT_EXIST;
        }else {
            return ResultMessage.SUCCESS;
        }
    }

    @Override
    public ResultMessage updateTaskWorker(TaskWorker taskWorker) {
        TaskWorker taskWorker1 = taskWorkerDao.save(taskWorker);
        if(taskWorker1==null){
            return ResultMessage.NOT_EXIST;
        }else {
            return ResultMessage.SUCCESS;
        }
    }

    @Override
    public long getExaminedTasksNumber(long userId){
        List<Long> userTaskIds = userDao.findOne(userId).getMyTasks();
        long count = 0;
        for(long taskId:userTaskIds){
            TaskState taskState = taskWorkerDao.findOne(taskId).getTaskState();
            if(taskState==TaskState.PASS||taskState==TaskState.REJECT){
                count++;
            }
        }
        return count;
    }

    @Override
    public long getFinishedTaskWorkersNumber(long taskPublisherId){
        List<TaskWorker> taskWorkers = taskWorkerDao.findAll();
        long count = 0;
        for(TaskWorker taskWorker:taskWorkers){
            if(taskWorker.getTaskPublisherId()==taskPublisherId&&taskWorker.getTaskState()==TaskState.PASS){
                count++;
            }
        }
        return count;
    }

    private List<User> searchByKeywords(String keywords,List<User> publishers){
        List<User> res = new ArrayList<>();
        if(!keywords.equals("")){
            ArrayList<Criterion> criteria = new ArrayList<>();
            criteria.add(
                    new Criterion(
                            new Criterion<>("username","%" + keywords + "%",QueryMode.FUZZY),
                            new Criterion(
                                    new Criterion<>("phoneNumber","%" + keywords + "%",QueryMode.FUZZY),
                                    new Criterion<>("email","%" + keywords + "%",QueryMode.FUZZY)
                            )
                    )
            );
            List<User> users1 = userDao.multiQuery(criteria);
            for(User user:users1){
                for(User user1:publishers){
                    if(user.getId()==user1.getId()){
                        res.add(user1);
                    }
                }
            }
        }else{
            for(User user:publishers){
                res.add(user);
            }
        }
        return res;
    }

    @Override
    public List<RecTag> getRecTagsByTaskPublisherId(long taskpublisherId){
        List<Tag> tags = tagDao.findByTaskPublisherIdAndTagType(taskpublisherId,TagType.RECT);
        List<RecTag> recTags = new ArrayList<>();
        for(Tag tag:tags){
            recTags.add((RecTag) tag);
        }
        return recTags;
    }

    @Override
    public ResultMessage recordCheckResult(Map<Long, Integer> userResult, Long taskPublisherId, Integer sum){
        TaskPublisher taskPublisher = taskPublisherDao.findOne(taskPublisherId);
        double rest = taskPublisher.getPrice();
        for (Map.Entry<Long, Integer> entry : userResult.entrySet()) {
            Long userId = entry.getKey();
            Integer correct = entry.getValue();
            Double price = new Double(0);

            User user = userDao.findOne(userId);
            Double accuracyRate = correct*1.0/sum;
            Double pointsPerPerson = taskPublisher.getPrice()*1.0/userResult.size();
            int taskCount = findWrongRecordCountByDate(LocalDate.now(), userId);
            if(taskCount > 2 && taskCount <= 4) {
                price = new Double(pointsPerPerson * accuracyRate /2);
            }
            else if(taskCount > 4){
                user.setProhibitTime(new Long(1));
                userDao.saveAndFlush(user);
            }
            else{
                price = new Double(pointsPerPerson * accuracyRate);
            }

            rest = rest - price;
            TaskRecord taskRecord = new TaskRecord(userId, taskPublisherId, LocalDate.now(), Math.round(price)*1.0, correct, sum);
            taskRecordDao.saveAndFlush(taskRecord);
        }

        Map<Long, Integer> result = sortMapByValue(userResult);
        if(result.size()>3){
            List<Map.Entry<Long, Integer>> entries = new ArrayList<>(result.entrySet());
            for(int i=0;i<3;i++){
                TaskRecord temp = taskRecordDao.findTaskRecordByUserIdAndTaskPublisherId(entries.get(i).getKey(), taskPublisherId);
                temp.setPrice(Math.round(temp.getPrice() + rest/3)*1.0);
                taskRecordDao.saveAndFlush(temp);
            }
        }
        return ResultMessage.SUCCESS;
    }

    private Map<Long, Integer> sortMapByValue(Map<Long, Integer> oriMap) {
        Map<Long, Integer> sortedMap = new LinkedHashMap<Long, Integer>();
        if (oriMap != null && !oriMap.isEmpty()) {
            List<Map.Entry<Long, Integer>> entryList = new ArrayList<Map.Entry<Long, Integer>>(oriMap.entrySet());
            Collections.sort(entryList, new Comparator<Map.Entry<Long, Integer>>() {
                public int compare(Map.Entry<Long, Integer> entry1,
                                   Map.Entry<Long, Integer> entry2) {
                    int value1 = entry1.getValue();
                    int value2 = entry2.getValue();
                    return value2 - value1;
                }
            });
            Iterator<Map.Entry<Long, Integer>> iter = entryList.iterator();
            Map.Entry<Long, Integer> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
        }
        return sortedMap;
    }


    @Override
    public Integer findWrongRecordCountByDate(LocalDate date, Long userId){
        List<TaskRecord> taskRecords = taskRecordDao.findByDateAndUserId(date, userId);
        int count = 0;
        if(taskRecords!=null){
            for(TaskRecord temp: taskRecords){
                if(temp.getCorrect()*1.0/temp.getSum() <= 0.4){
                    count++;
                }
            }
        }
        return count;
    }


    /**
     * 查找待审核的专家
     * @return
     */
    @Override
    public List<User> showApplyingProList(){
        List<User> users = userDao.findByApplyState(ApplyState.APPLYING);
        return users;
    }
}
