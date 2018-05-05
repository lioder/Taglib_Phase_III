package horizon.taglib.enums;

public enum SortBy {
    TIME("时间"), RATING("好评率"), REWARD("奖励"), WORKLOAD("任务量"), ALL("全部"),POINTS("积分"),EXP("经验值"),ACCURACYRATE("准确度"),SATISFACTIONRATE("客户满意度");

    private String value;

    SortBy(String value){
        this.value = value;
    }

    public String toString(){
        return value;
    }
}
