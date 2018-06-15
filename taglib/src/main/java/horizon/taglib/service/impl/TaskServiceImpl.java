package horizon.taglib.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import horizon.taglib.dao.*;
import horizon.taglib.dto.PageDTO;
import horizon.taglib.enums.QueryMode;
import horizon.taglib.enums.ResultMessage;
import horizon.taglib.enums.TaskState;
import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.TaskRecord;
import horizon.taglib.model.User;
import horizon.taglib.service.TaskService;
import horizon.taglib.model.CenterTag;
import horizon.taglib.service.valuedata.MyCluster;
import horizon.taglib.utils.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
public class TaskServiceImpl implements TaskService {
    private final static String FILE_SEPARATOR = System.getProperty("file.separator");
    private final static String DIR = "taglib" + FILE_SEPARATOR + "database" + FILE_SEPARATOR + "tag-data";
    private TaskPublisherDao taskPublisherDao;
    private TagDao tagDao;
    private UserDao userDao;
    private TaskRecordDao taskRecordDao;
	private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public TaskServiceImpl(TaskPublisherDao taskPublisherDao, TagDao tagDao, UserDao userDao,
                           TaskRecordDao taskRecordDao) {
        this.taskPublisherDao = taskPublisherDao;
        this.tagDao = tagDao;
        this.userDao = userDao;
        this.taskRecordDao = taskRecordDao;
	    File dirs = new File(DIR);
        if (!dirs.exists() || !dirs.isDirectory()) {
            boolean isFailed = dirs.mkdirs();
            if (isFailed) {
                System.out.println("Make dirs failed in TaskServiceImpl! ");
            }
        }
    }

    @Override
    public List<TaskPublisher> getAllTasks() {
        return taskPublisherDao.findAll();
    }

    @Override
    public TaskPublisher getTaskPublisherById(long id) {
        return taskPublisherDao.findOne(id);
    }


    @Override
    public ResultMessage deleteTag(long tagId) {
        tagDao.delete(tagDao.findOne(tagId));
        return ResultMessage.SUCCESS;
    }

    @Override
    public List<Object> addTask(TaskPublisher taskPublisher) {
        List<Object> list = new ArrayList<>();
        TaskPublisher taskPublisher1 = taskPublisherDao.save(taskPublisher);
        ResultMessage resultMessage = ResultMessage.FAILED;
        if (taskPublisher1 != null) {
            resultMessage = ResultMessage.SUCCESS;
        }
        list.add(resultMessage);

        User user = userDao.findOne(taskPublisher.getUserId());
        Long id = null;
        if (taskPublisher1 != null) {
            id = taskPublisher1.getId();
        }
        List<Long> list1 = user.getMyTasks();
        list1.add(id);
        user.setMyTasks(list1);
        userDao.save(user);

        if (resultMessage == ResultMessage.SUCCESS) {
            list.add(taskPublisher1.getId());
        }
        return list;
    }

//    @Override
//    public long getNewTaskId(){
//        return taskPublisherDao.getNewId();
//    }

    @Override
    public PageDTO<TaskPublisher> findTaskPublisherByState(long userId, TaskState taskState, Integer size, Integer currentPage) {
        if (userId == 0) {
            ArrayList<Criterion> criteria = new ArrayList<>();
            criteria.add(new Criterion<>("taskState", taskState, QueryMode.FULL));
            List<TaskPublisher> taskPublishers = taskPublisherDao.multiQuery(criteria);
            return getPageDTO(taskPublishers, size, currentPage);

        } else {
            List<TaskPublisher> taskPublishers = taskPublisherDao.findByUserIdAndTaskState(userId, taskState);
            return getPageDTO(taskPublishers, size, currentPage);
        }
    }

    @Override
    public ResultMessage updateImageList(Long taskId, List<String> imageInZipList, String zipName) {

        TaskPublisher taskPublisher = taskPublisherDao.findOne(taskId);
        double price = taskPublisher.getPrice();

        List<String> imageList = taskPublisher.getImages();
        price = price / imageList.size();
        imageList.remove(imageList.indexOf(zipName));
        imageList.addAll(imageInZipList);
        taskPublisher.setImages(imageList);
        taskPublisher.setPrice(price * imageList.size());
        taskPublisherDao.save(taskPublisher);
        return ResultMessage.SUCCESS;
    }

    @Override
    public Map<Long,Double> getAllTaskRecordsByTaskPublisherId(Long taskPublisherId){
        List<TaskRecord> taskRecords = taskRecordDao.findByTaskPublisherId(taskPublisherId);
        Map<Long,Double> map = new HashMap<>();
        for(TaskRecord taskRecord : taskRecords){
            map.put(taskRecord.getUserId(),(double)taskRecord.getCorrect()/(double)taskRecord.getSum());
        }
        List<Map.Entry<Long,Double>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<Long,Double>>(){
            //降序
            @Override
            public int compare(Map.Entry<Long,Double> o1, Map.Entry<Long,Double> o2){
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        Map<Long,Double> newMap = new LinkedHashMap<>();
        for(Map.Entry<Long,Double> mapping:list){
            newMap.put(mapping.getKey(),mapping.getValue());
        }
        return newMap;
    }

    /**
     * 将标准标注保存为JSON文件，供发布者下载
     *
     * @param taskPublisherId 任务（发布者视角）的id
     * @param clusters 标准标注的列表
     * @return  SUCCESS：保存成功<br>
     *          FAILED：保存失败
     */
    @Override
    public ResultMessage saveCenterTagAsJSON(Long taskPublisherId, List<MyCluster> clusters) {
        List<CenterTag> toWrite = new ArrayList<>();
        for (MyCluster myCluster : clusters) {
            String filename = myCluster.getFilename();
            toWrite.add(new CenterTag(taskPublisherId, filename,
                    myCluster.getCenter().apply(0), myCluster.getCenter().apply(1),
                    myCluster.getCenter().apply(2), myCluster.getCenter().apply(3), myCluster.getLabel()));
        }
        return write(taskPublisherId, toWrite);
    }

    private PageDTO<TaskPublisher> getPageDTO(List<TaskPublisher> taskPublishers, Integer size, Integer currentPage) {
        PageDTO<TaskPublisher> taskPublisherPageDTO = new PageDTO<>(currentPage, size, taskPublishers.size());
        Integer dataIndex = taskPublisherPageDTO.getDataIndex();
        if (currentPage * size < taskPublishers.size()) {
            taskPublisherPageDTO.setPageData(taskPublishers.subList(dataIndex - 1, dataIndex - 1 + size));
        } else {
            taskPublisherPageDTO.setPageData(taskPublishers.subList(dataIndex - 1, taskPublishers.size()));
        }
        return taskPublisherPageDTO;
    }

    private ResultMessage write(Long taskPublisherId, List<CenterTag> toWrite) {
        File file = new File(DIR + FILE_SEPARATOR + taskPublisherId + ".json");
        try (FileWriter fileWriter = new FileWriter(file, false);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {
            objectMapper.writerFor(new TypeReference<List<CenterTag>>() {
            }).writeValue(writer, toWrite);
            return ResultMessage.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultMessage.FAILED;
    }
}
