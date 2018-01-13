package zhiyuan.com.loan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.bean.Apk;
import zhiyuan.com.loan.util.MyUtils;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();

        initData();
    }


    private void initView() {


        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /*boolean isInstall = MyApplication.sharedPreferences.getBoolean("isInstall", false);
                if (isInstall){
                    startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                }
                else {
                    startActivity(new Intent(SplashActivity.this,InstallGuideActivity.class));
                    MyApplication.sharedPreferences.edit().putBoolean("isInstall",true).apply();
                }*/
                startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                finish();
            }
        }.start();


    }

    private void initData() {
        BmobQuery<Apk> apkBmobQuery = new BmobQuery<>();
        apkBmobQuery.findObjects(new FindListener<Apk>() {
            @Override
            public void done(List<Apk> list, BmobException e) {
                if (e==null){
                    if (list!=null && list.size()>0){
                        Apk apk = list.get(0);
                        int versionCode = Integer.parseInt(apk.getVersionCode());
                        String apkUrl = apk.getApkUrl();
                        boolean forceWhenUpdate = apk.isForceWhenUpdate();
                        boolean updateWhenOpen = apk.isUpdateWhenOpen();
                        String qqContactInfo = apk.getQqContactInfo();
                        String contactPhone = apk.getContactPhone();

                        Log.i(TAG,"versionCode:"+versionCode+",apkUrl:"+apkUrl+",forceWhenUpdate:"+forceWhenUpdate+",updateWhenOpen:"+updateWhenOpen);

                        SharedPreferences.Editor edit = MyApplication.sharedPreferences.edit();
                        edit.putInt("versionCode",versionCode).apply();
                        edit.putString("apkUrl",apkUrl).apply();
                        edit.putString("qqContactInfo",qqContactInfo).apply();
                        edit.putString("contactPhone",contactPhone).apply();
                        edit.putBoolean("forceWhenUpdate",forceWhenUpdate).apply();
                        edit.putBoolean("updateWhenOpen",updateWhenOpen).apply();
                        edit.putString("weixinContactInfo",apk.getWeixinContactInfo()).apply();

                        MyUtils.putApkToSP(apk);
                    }
                }else {

                }
            }
        });


    }
}
