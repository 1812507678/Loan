package zhiyuan.com.loan.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/7/16.
 */
public class Adviser extends BmobObject{
    private String iconUrl;
    private String userId;
    private String name;
    private String type;
    private int account;
    private String phone;
    private BmobFile iocn;
    private String password;
    private String qq;
    private String email;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAccount() {
        return account;
    }
    public void setAccount(int account) {
        this.account = account;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BmobFile getIocn() {
        return iocn;
    }

    public void setIocn(BmobFile iocn) {
        this.iocn = iocn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Adviser{" +
                "iconUrl='" + iconUrl + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", account=" + account +
                ", phone='" + phone + '\'' +
                ", iocn=" + iocn +
                '}';
    }
}
