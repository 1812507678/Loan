package zhiyuan.com.loan.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/8/2.
 */
public class Apk extends BmobObject {
    private String apkUrl;
    private String versionCode;
    private boolean isUpdateWhenOpen;
    private boolean isForceWhenUpdate;
    private String qqContactInfo;
    private String contactPhone;

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }


    public boolean isUpdateWhenOpen() {
        return isUpdateWhenOpen;
    }

    public void setUpdateWhenOpen(boolean updateWhenOpen) {
        isUpdateWhenOpen = updateWhenOpen;
    }

    public boolean isForceWhenUpdate() {
        return isForceWhenUpdate;
    }

    public void setForceWhenUpdate(boolean forceWhenUpdate) {
        isForceWhenUpdate = forceWhenUpdate;
    }

    public String getQqContactInfo() {
        return qqContactInfo;
    }

    public void setQqContactInfo(String qqContactInfo) {
        this.qqContactInfo = qqContactInfo;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}
