package horizon.taglib.enums;

/**
 * 申请状态
 * <br>
 * created on 2018/06/14
 *
 * @author 巽
 **/
public enum ApplyState {
	NOT_YET("尚未申请"), APPLYING("申请中"), PASS("申请成功");

	private String value;

	ApplyState(String value){
		this.value=value;
	}

	public String toString(){
		return value;
	}
}
