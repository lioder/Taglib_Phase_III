package horizon.taglib.service.impl;

import horizon.taglib.dao.UserDao;
import horizon.taglib.dto.RankDTO;
import horizon.taglib.enums.UserType;
import horizon.taglib.model.User;
import horizon.taglib.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RankServiceImpl implements RankService {

    private UserDao userDao;

    @Autowired
    public RankServiceImpl(UserDao userDao){
        this.userDao = userDao;
    }

    @Override
    public RankDTO getWorkerPointRank(Long userId) {
            List<User> users = userDao.findAll();
            //按照积分高优先排序
            Sort.SortByPoints sortByPoints = new Sort.SortByPoints();
            List<User> users1 = getWorkers(users);
            Collections.sort(users1, sortByPoints);

            return getRankDTO(userId,users1);
    }

    @Override
    public RankDTO getWorkerExpRank(Long userId) {
        List<User> users = userDao.findAll();
        //按照经验高优先排序
        Sort.SortByExp sortByExp = new Sort.SortByExp();
        List<User> users1 = getWorkers(users);
        Collections.sort(users1,sortByExp);

        return getRankDTO(userId,users1);
    }

    @Override
    public RankDTO getWorkerAccuracyRateRank(Long userId) {
        List<User> users = userDao.findAll();
        //按照准确度高优先排序
        Sort.SortByAccuracyRate sortByAccuracyRate = new Sort.SortByAccuracyRate();
        List<User> users1 = getWorkers(users);
        Collections.sort(users1,sortByAccuracyRate);

        return getRankDTO(userId,users1);
    }

    @Override
    public RankDTO getWorkerSatisfactionRank(Long userId) {
       List<User> users = userDao.findAll();
       //按照客户满意度优先排序
        Sort.SortByWorkerSatisfaction sortByWorkerSatisfaction = new Sort.SortByWorkerSatisfaction();
        List<User> users1 = getWorkers(users);
        Collections.sort(users1,sortByWorkerSatisfaction);

        return getRankDTO(userId,users1);
    }

    @Override
    public RankDTO getPublisherPointRank(Long userId) {
        List<User> users = userDao.findAll();
        //按照发起者积分排序
        Sort.SortByPoints sortByPoints = new Sort.SortByPoints();
        List<User> users1 = getPublishers(users);
        Collections.sort(users1,sortByPoints);

        return getRankDTO(userId,users1);
    }

    @Override
    public RankDTO getPublisherExpRank(Long userId) {
        List<User> users = userDao.findAll();
        //按照发起者经验排序
        Sort.SortByExp sortByExp = new Sort.SortByExp();
        List<User> users1 = getPublishers(users);
        Collections.sort(users1,sortByExp);

        return getRankDTO(userId,users1);
    }

    @Override
    public RankDTO getPublisherTaskRank(Long userId) {
        List<User> users = userDao.findAll();
        //按照发起者发布任务数排序
        Sort.SortByTaskNum sortByTasksNum = new Sort.SortByTaskNum();
        List<User> users1 = getPublishers(users);
        Collections.sort(users1,sortByTasksNum);

        return getRankDTO(userId,users1);
    }

    //得到排名
    private Integer getRanking(List<User> users,User user){
        Integer ranking = 0;
        for(User user1 : users){
            ranking++;
            if(user1.getId()==user.getId()){
                break;
            }
        }
        return ranking;
    }

    //筛选众包工人
    private List<User> getWorkers(List<User> users){
        List<User> res = new ArrayList<>();
        for(User user : users){
            if(user.getUserType()==UserType.WORKER){
                res.add(user);
            }
        }
        return res;
    }

    //筛选众包发起者
    private List<User> getPublishers(List<User> users){
        List<User> res = new ArrayList<>();
        for(User user : users){
            if(user.getUserType()==UserType.REQUESTOR){
                res.add(user);
            }
        }
        return res;
    }

    //根据用户编号和排序后的用户得到相应RankDTO
    //如果userId等于0，用户排名和关于用户的信息都为null
    private RankDTO getRankDTO(Long userId,List<User> users){
        if(userId ==0){
            RankDTO rankDTO = new RankDTO(users,null,null);
            return rankDTO;
        }else {
            User user = userDao.findOne(userId);
            Integer ranking = getRanking(users, user);
            RankDTO rankDTO = new RankDTO(users, ranking, user);
            return rankDTO;
        }
    }
}
