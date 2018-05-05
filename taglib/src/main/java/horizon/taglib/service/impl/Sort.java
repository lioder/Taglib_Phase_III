package horizon.taglib.service.impl;

import horizon.taglib.model.TaskPublisher;
import horizon.taglib.model.User;

import java.util.Comparator;
import java.util.Set;

public class Sort {

    //根据准确度排序
    static class SortByAccuracyRate implements Comparator {
        public int compare(Object o1, Object o2) {
            User u1 = (User) o1;
            User u2 = (User) o2;
            return u2.getAccuracyRate().compareTo(u1.getAccuracyRate());
        }
    }

    //根据经验排序
    static class SortByExp implements Comparator {
        public int compare(Object o1, Object o2) {
            User u1 = (User) o1;
            User u2 = (User) o2;
            return u2.getExp().compareTo(u1.getExp());
        }
    }

    //根据积分排序
    static class SortByPoints implements Comparator{
        public int compare(Object o1,Object o2){
            User u1 = (User) o1;
            User u2 = (User) o2;
            return u2.getPoints().compareTo(u1.getPoints());
        }
    }

    //根据发起者发布任务数排序
    static class SortByTaskNum implements Comparator {
        public int compare(Object o1,Object o2){
            User u1 = (User) o1;
            User u2 = (User) o2;
            Integer num1 = u1.getMyTasks().size();
            Integer num2 = u2.getMyTasks().size();
            return num2.compareTo(num1);
        }
    }

    //按满意度排序
    static class SortByWorkerSatisfaction implements Comparator {
        public int compare(Object o1,Object o2){
            User u1 = (User) o1;
            User u2 = (User) o2;
            return u2.getSatisfactionRate().compareTo(u1.getSatisfactionRate());
        }
    }

    //根据热度排序
    static class SortByHotRank implements Comparator {
        public int compare(Object o1,Object o2){
            TaskPublisher t1 = (TaskPublisher) o1;
            TaskPublisher t2 = (TaskPublisher) o2;
            return t2.getHotRank().compareTo(t1.getHotRank());
        }
    }
}
