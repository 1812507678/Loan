package zhiyuan.com.loan.activity;import android.app.Activity;import android.content.Context;import android.content.Intent;import android.graphics.Color;import android.os.Build;import android.os.Bundle;import android.support.v4.app.Fragment;import android.support.v4.app.FragmentActivity;import android.support.v4.app.FragmentPagerAdapter;import android.util.Log;import android.view.View;import android.view.ViewGroup;import android.view.Window;import android.view.WindowManager;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.TextView;import java.util.ArrayList;import java.util.List;import zhiyuan.com.loan.R;import zhiyuan.com.loan.application.MyApplication;import zhiyuan.com.loan.fragment.ConsultPageFragment;import zhiyuan.com.loan.fragment.StrategyPageFragment;import zhiyuan.com.loan.fragment.HomePageFragment;import zhiyuan.com.loan.fragment.MePageFragment;import zhiyuan.com.loan.util.ApkVersionUtil;import zhiyuan.com.loan.util.MyViewPager;public class HomeActivity extends BaseActivity {    private static final String TAG = "HomeActivity";    private TextView tv_home_home;    private TextView tv_home_care;    private TextView tv_home_find;    private TextView tv_home_me;    private ImageView iv_home_home;    private ImageView iv_home_care;    private ImageView iv_home_find;    private ImageView iv_home_me;    private MyViewPager vp_home_page;    public List<Fragment> pageFragments;    @Override    public void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_home);        initView();        int statusBarHeight = getStatusBarHeight(this);        Log.i(TAG,"statusBarHeight:"+statusBarHeight);    }    private int getStatusBarHeight(Context context) {        int result = 0;        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");        if (resourceId > 0) {            result = context.getResources().getDimensionPixelSize(resourceId);        }        return result;    }    private void initView() {        initViewPager();        tv_home_home = (TextView) findViewById(R.id.tv_home_home);        tv_home_care = (TextView) findViewById(R.id.tv_home_care);        tv_home_find = (TextView) findViewById(R.id.tv_home_find);        tv_home_me = (TextView) findViewById(R.id.tv_home_me);        iv_home_home = (ImageView) findViewById(R.id.iv_home_home);        iv_home_care = (ImageView) findViewById(R.id.iv_home_care);        iv_home_find = (ImageView) findViewById(R.id.iv_home_find);        iv_home_me = (ImageView) findViewById(R.id.iv_home_me);        LinearLayout ll_home_home = (LinearLayout) findViewById(R.id.ll_home_home);        LinearLayout ll_home_care = (LinearLayout) findViewById(R.id.ll_home_care);        LinearLayout ll_home_find = (LinearLayout) findViewById(R.id.ll_home_find);        LinearLayout ll_home_me = (LinearLayout) findViewById(R.id.ll_home_me);        OnHomeBottomItemOnclickListener onHomeBottomItemOnclickListener = new OnHomeBottomItemOnclickListener();        ll_home_home.setOnClickListener(onHomeBottomItemOnclickListener);        ll_home_care.setOnClickListener(onHomeBottomItemOnclickListener);        ll_home_find.setOnClickListener(onHomeBottomItemOnclickListener);        ll_home_me.setOnClickListener(onHomeBottomItemOnclickListener);        Intent intent = getIntent();        if (intent!=null){            boolean isContant = intent.getBooleanExtra("isContant", false);            if (isContant){                setCurrentItem(1);            }        }        checkAndUpdateVersion();    }    private void checkAndUpdateVersion() {        boolean forceWhenUpdate = MyApplication.sharedPreferences.getBoolean("forceWhenUpdate", false);        boolean updateWhenOpen = MyApplication.sharedPreferences.getBoolean("updateWhenOpen", false);        int versionCode = MyApplication.sharedPreferences.getInt("versionCode", 0);        Log.i(TAG,"versionCode:"+versionCode+",forceWhenUpdate:"+forceWhenUpdate+",updateWhenOpen:"+updateWhenOpen);        if (updateWhenOpen){            //打开应用时检查更新            ApkVersionUtil.updateVersion(versionCode,forceWhenUpdate,this);        }    }    private void initViewPager() {        vp_home_page = (MyViewPager) findViewById(R.id.vp_home_page);        pageFragments = new ArrayList<>();        pageFragments.add(new HomePageFragment());        pageFragments.add(new ConsultPageFragment());        pageFragments.add(new StrategyPageFragment());        pageFragments.add(new MePageFragment());        vp_home_page.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {            @Override            public Fragment getItem(int position) {                return pageFragments.get(position);            }            @Override            public void destroyItem(ViewGroup container, int position, Object object) {                super.destroyItem(container, position, object);                Log.i(TAG,"destroyItem:"+position);            }            @Override            public Object instantiateItem(ViewGroup container, int position) {                Log.i(TAG,"instantiateItem:"+position);                return super.instantiateItem(container, position);            }            @Override            public int getCount() {                return pageFragments.size();            }        });        vp_home_page.setOffscreenPageLimit(3);    }    //切换当前点击的Fragment    public void setCurrentItem(int itemPostion){        switch (itemPostion){            case 0:                vp_home_page.setCurrentItem(0,false); //false表示禁止滑动效果                tv_home_home.setTextColor(Color.parseColor("#0b9a27"));                tv_home_care.setTextColor(Color.parseColor("#999999"));                tv_home_find.setTextColor(Color.parseColor("#999999"));                tv_home_me.setTextColor(Color.parseColor("#999999"));                iv_home_home.setImageResource(R.drawable.widget_bar_news_over);                iv_home_care.setImageResource(R.drawable.widget_bar_tweet_nor);                iv_home_find.setImageResource(R.drawable.widget_bar_explore_nor);                iv_home_me.setImageResource(R.drawable.widget_bar_me_nor);                break;            case 1:                vp_home_page.setCurrentItem(1,false);                tv_home_home.setTextColor(Color.parseColor("#999999"));                tv_home_care.setTextColor(Color.parseColor("#0b9a27"));                tv_home_find.setTextColor(Color.parseColor("#999999"));                tv_home_me.setTextColor(Color.parseColor("#999999"));                iv_home_home.setImageResource(R.drawable.widget_bar_news_nor);                iv_home_care.setImageResource(R.drawable.widget_bar_tweet_over);                iv_home_find.setImageResource(R.drawable.widget_bar_explore_nor);                iv_home_me.setImageResource(R.drawable.widget_bar_me_nor);                break;            case 2:                vp_home_page.setCurrentItem(2,false);                tv_home_home.setTextColor(Color.parseColor("#999999"));                tv_home_care.setTextColor(Color.parseColor("#999999"));                tv_home_find.setTextColor(Color.parseColor("#0b9a27"));                tv_home_me.setTextColor(Color.parseColor("#999999"));                iv_home_home.setImageResource(R.drawable.widget_bar_news_nor);                iv_home_care.setImageResource(R.drawable.widget_bar_tweet_nor);                iv_home_find.setImageResource(R.drawable.widget_bar_explore_over);                iv_home_me.setImageResource(R.drawable.widget_bar_me_nor);                break;            case 3:                vp_home_page.setCurrentItem(3,false);                tv_home_home.setTextColor(Color.parseColor("#999999"));                tv_home_care.setTextColor(Color.parseColor("#999999"));                tv_home_find.setTextColor(Color.parseColor("#999999"));                tv_home_me.setTextColor(Color.parseColor("#0b9a27"));                iv_home_home.setImageResource(R.drawable.widget_bar_news_nor);                iv_home_care.setImageResource(R.drawable.widget_bar_tweet_nor);                iv_home_find.setImageResource(R.drawable.widget_bar_explore_nor);                iv_home_me.setImageResource(R.drawable.widget_bar_me_over);                break;        }    }    class OnHomeBottomItemOnclickListener implements View.OnClickListener{        @Override        public void onClick(View v) {            switch (v.getId()){                case R.id.ll_home_home:                    setCurrentItem(0);                    break;                case R.id.ll_home_care:                    setCurrentItem(1);                    break;                case R.id.ll_home_find:                    setCurrentItem(2);                    break;                case R.id.ll_home_me:                    setCurrentItem(3);                    break;            }        }    }    @Override    public void onActivityResult(int requestCode, int resultCode, Intent data) {        //super.onActivityResult(requestCode, resultCode, data);        Log.i("onActivityResult","HomeActivity"+requestCode+","+resultCode);        ConsultPageFragment consultPageFragment = new ConsultPageFragment();        consultPageFragment.onActivityResult(requestCode, resultCode, data);    }}