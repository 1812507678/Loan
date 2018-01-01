package zhiyuan.com.loan.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/8/12.
 */
public class StudentApply58 extends BmobObject {
    private String applyId;
    private String name;
    private String qq;
    private String certificate;
    private String account;
    private String password;
    private String phone;
    private String money;
    private String mouth;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getMouth() {
        return mouth;
    }

    public void setMouth(String mouth) {
        this.mouth = mouth;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public StudentApply58(String applyId, String name, String qq, String certificate, String account, String password, String phone, String money, String mouth) {
        this.applyId = applyId;
        this.name = name;
        this.qq = qq;
        this.certificate = certificate;
        this.account = account;
        this.password = password;
        this.phone = phone;
        this.money = money;
        this.mouth = mouth;
    }
}
