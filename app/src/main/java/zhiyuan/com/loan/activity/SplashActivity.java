package zhiyuan.com.loan.activity;

import android.content.Intent;
import android.os.Bundle;

import zhiyuan.com.loan.R;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.util.ApkVersionUtil;

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
        MyApplication application = (MyApplication) getApplication();
        ApkVersionUtil.initApkData(application);
        ApkVersionUtil.loadArticleListtData(application);
    }



}
