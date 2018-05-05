package horizon.taglib.vo;

import horizon.taglib.enums.Level;
import horizon.taglib.enums.UserType;
import lombok.Data;

@Data
public class UserVO {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户类型
     */
    private Integer userType;
    /**
     * 用户积分
     */
    private Long points;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 用户等级
     */
    private Level level;
    /**
     * 用户经验
     */
    private Long exp;
    /**
     * 准确度
     */
    private Double accuracyRate;
    /**
     * 准时度
     */
    private Double punctualityRate;
    /**
     * 客户满意度
     */
    private Double satisfactionRate;
    /**
     * 是否签到
     */
    private Boolean isAttendant;
    /**
     * 接受或发起的task的数量
     */
    private Integer taskNum;

    public UserVO(Long id, String username, String password, String phone, String email, Integer userType) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.userType = userType;
    }

    public UserVO() {
    }

    public UserVO(Long id, String username, String password, String phone, String email, Integer userType, Long points, String avatar, Level level, Long exp, Double accuracyRate, Double punctualityRate, Double satisfactionRate, Boolean isAttendant) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.userType = userType;
        this.points = points;
        this.avatar = avatar;
        this.level = level;
        this.exp = exp;
        this.accuracyRate = accuracyRate;
        this.punctualityRate = punctualityRate;
        this.satisfactionRate = satisfactionRate;
        this.isAttendant = isAttendant;
    }
}
