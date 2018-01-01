package zhiyuan.com.loan.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by haijun on 2016/9/27.
 */
public class Suggestion extends BmobObject{
    String userPhone;
    String suggestion;

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public Suggestion(String userPhone, String suggestion) {
        this.userPhone = userPhone;
        this.suggestion = suggestion;
    }
}
