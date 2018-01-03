package zhiyuan.com.loan.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

import zhiyuan.com.loan.R;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.util.StatusBarUtil;

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);


        StatusBarUtil.StatusBarLightMode(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MyApplication.getInstance().setmCurrApplicationActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MyApplication.getInstance().setmCurrApplicationActivity(null);
    }

    public void back(View view){
        finish();
    }
}
