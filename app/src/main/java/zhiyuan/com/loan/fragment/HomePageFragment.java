package zhiyuan.com.loan.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.activity.ChattingActivity;
import zhiyuan.com.loan.activity.CommitMaterialStep1Activity;
import zhiyuan.com.loan.activity.CreditPlatListActivity;
import zhiyuan.com.loan.activity.ShowArticleDetailActivity;
import zhiyuan.com.loan.adapter.ArticlelistAdapter;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.bean.Apk;
import zhiyuan.com.loan.bean.MessageEvent;
import zhiyuan.com.loan.bean.StrategyArticle;
import zhiyuan.com.loan.util.ApkVersionUtil;
import zhiyuan.com.loan.util.ChooseAlertDialogUtil;
import zhiyuan.com.loan.util.Constant;
import zhiyuan.com.loan.util.EventbusProxy;
import zhiyuan.com.loan.util.MyUtils;

/**
 * Created by Administrator on 2016/7/7.
 */
public class HomePageFragment  extends Fragment {

    private static final String TAG = "StrategyPageFragment";
    private View inflate;
    private ViewPager vp_home_msg;
    int previousSelectedPosition = 0;
    private List<StrategyArticle> articleListListContent;
    private ListView lv_strategy_articlelist1;
    private ProgressBar pb_progress;
    private int mCurApkInfoDownloadFinshed;
    private int mCurArticleListDownloadFinshed;
    private TextView tv_home_loadfailtext;
    private LinearLayout ll_home_view;
    private ArticlelistAdapter mStrategyAdapter;
    private Apk mApk;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.frgament_page_home, new LinearLayout(getActivity()),false);

        initView();
        initData();

        return inflate;
    }



    private void initView() {
        initViewPage();
        TextView tv_home_creditStep = (TextView) inflate.findViewById(R.id.tv_home_creditStep);
        TextView tv_home_creditcall = (TextView) inflate.findViewById(R.id.tv_home_creditcall);
        RelativeLayout rl_home_creditClass = (RelativeLayout) inflate.findViewById(R.id.rl_home_creditClass);
        RelativeLayout rl_home_consult = (RelativeLayout) inflate.findViewById(R.id.rl_home_consult);
        ImageView iv_home_qr = inflate.findViewById(R.id.iv_home_qr);
        pb_progress = inflate.findViewById(R.id.pb_progress);
        tv_home_loadfailtext = inflate.findViewById(R.id.tv_home_loadfailtext);

        HomeOnClickListener homeOnClickListener = new HomeOnClickListener();
        tv_home_creditStep.setOnClickListener(homeOnClickListener);
        tv_home_creditcall.setOnClickListener(homeOnClickListener);
        rl_home_creditClass.setOnClickListener(homeOnClickListener);
        rl_home_consult.setOnClickListener(homeOnClickListener);
        iv_home_qr.setOnClickListener(homeOnClickListener);
        tv_home_loadfailtext.setOnClickListener(homeOnClickListener);

        EventBus.getDefault().register(this);

    }

    private void initData() {
        ll_home_view = (LinearLayout) inflate.findViewById(R.id.ll_home_view);
        lv_strategy_articlelist1 = (ListView)  inflate.findViewById(R.id.lv_strategy_articlelist1);
        articleListListContent = new ArrayList<>();



        if (Constant.isuploadApkTest){
            mCurApkInfoDownloadFinshed = ((MyApplication) getActivity().getApplication()).getCurApkInfoDownloadFinshed();
            mCurArticleListDownloadFinshed = ((MyApplication) getActivity().getApplication()).getCurArticleListDownloadFinshed();
            if (mCurApkInfoDownloadFinshed ==Constant.curdownloadType_success){
                //apk下载成功
                Apk apkFromSP = MyUtils.getApkFromSP();
                if (apkFromSP!=null){
                    boolean isuploadApkTest = apkFromSP.isIsuploadApkTest();
                    if(isuploadApkTest){
                        //需要动态切换模块
                        ll_home_view.setVisibility(View.GONE);
                        if (mCurArticleListDownloadFinshed == Constant.curdownloadType_success){
                            //文章加载成功
                            lv_strategy_articlelist1.setVisibility(View.VISIBLE);
                            pb_progress.setVisibility(View.GONE);
                        }
                        else  if (mCurArticleListDownloadFinshed == Constant.curdownloadType_failure){
                            //文章加载失败，可能是网络问题
                            lv_strategy_articlelist1.setVisibility(View.GONE);
                            pb_progress.setVisibility(View.GONE);
                            tv_home_loadfailtext.setVisibility(View.VISIBLE);
                        }
                        else {
                            //文章还没有加载完，显示进度
                            lv_strategy_articlelist1.setVisibility(View.GONE);
                            pb_progress.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            else if (mCurApkInfoDownloadFinshed ==Constant.curdownloadType_failure){
                //apk下载失败，可能是网络问题
                ll_home_view.setVisibility(View.GONE);
                lv_strategy_articlelist1.setVisibility(View.GONE);
                pb_progress.setVisibility(View.GONE);
                tv_home_loadfailtext.setVisibility(View.VISIBLE);
            }
            else {
                //apk还没有加载完，显示进度
                ll_home_view.setVisibility(View.GONE);
                lv_strategy_articlelist1.setVisibility(View.GONE);
                pb_progress.setVisibility(View.VISIBLE);
            }


        }

        List<StrategyArticle> articleListListContent_1 = ((MyApplication) getActivity().getApplication()).getArticleListListContent();
        if (articleListListContent_1!=null){
            articleListListContent = articleListListContent_1;
        }
        mStrategyAdapter = new ArticlelistAdapter(getContext(), articleListListContent);
        lv_strategy_articlelist1.setAdapter(mStrategyAdapter);

        lv_strategy_articlelist1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position<articleListListContent.size()&& position>=0){
                    StrategyArticle article = articleListListContent.get(position);
                    if (article!=null){
                        Log.i(TAG,article.toString());
                        String articleurl = article.getArticleUrl();
                        String objectId = article.getObjectId();
                        Intent intent = new Intent(getActivity(),ShowArticleDetailActivity.class);
                        intent.putExtra("articleurl",articleurl);
                        Log.i(TAG,"articleurl:"+articleurl);
                        Log.i(TAG,"article:"+article.getTitle());
                        intent.putExtra("articleId",article.getArticleId());

                        intent.putExtra("objectId",objectId);
                        intent.putExtra("readCount",article.getReadCount());
                        startActivity(intent);
                    }
                }
            }
        });

    }

    private void initViewPage() {
        vp_home_msg = (ViewPager) inflate.findViewById(R.id.vp_home_msg);

        final LinearLayout ll_home_point = (LinearLayout) inflate.findViewById(R.id.ll_home_point);

        for (int i = 0; i < 4; i++){
            View pointView = new View(getActivity());
            pointView.setBackgroundResource(R.drawable.selector_bg_point);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(30, 30);
            if(i != 0)
                layoutParams.leftMargin = 10;
            pointView.setEnabled(false);
            ll_home_point.addView(pointView, layoutParams);
        }

        vp_home_msg.setAdapter(new MyViewPageAdapter());

        vp_home_msg.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int newPosition = position % 4;
                ll_home_point.getChildAt(previousSelectedPosition).setEnabled(false);
                ll_home_point.getChildAt(newPosition).setEnabled(true);
                previousSelectedPosition  = newPosition;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                vp_home_msg.setCurrentItem(vp_home_msg.getCurrentItem() + 1);
                            }
                        });
                    }
                }
            }
        }.start();



    }

    private Map<Integer,Bitmap> mViewsBitmap = new HashMap<>();

    class MyViewPageAdapter extends PagerAdapter {
        int []iamges = new int[]{R.drawable.viewpage02,R.drawable.viewpage01,R.drawable.viewpage03,R.drawable.viewpage04};
        private BitmapFactory.Options options ;

        public MyViewPageAdapter() {
            options = new BitmapFactory.Options();
            //options.inPreferredConfig = Bitmap.Config.ARGB_4444;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            //options.inSampleSize = 2;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int newPosition = position % 4;
            //
            // Log.i(TAG,"instantiateItem newPosition:"+newPosition);

            ImageView imageView = new ImageView(getContext());
            Bitmap bitmap = null;
            bitmap = mViewsBitmap.get(newPosition);

            if (bitmap==null){
                bitmap = BitmapFactory.decodeResource(getContext().getResources(), iamges[newPosition], options);
                mViewsBitmap.put(newPosition,bitmap);
            }
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            int i = position % 4;
            //Log.i(TAG,"destroyItem newPosition:"+i);
            Bitmap bitmap = mViewsBitmap.get(i);
            if (bitmap!=null){
                bitmap.recycle();
                bitmap = null;
                mViewsBitmap.remove(i);
            }

            container.removeView((View) object);
        }
    }

    class HomeOnClickListener implements View.OnClickListener{


        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_home_creditStep:
                    startActivity(new Intent(getActivity(), CommitMaterialStep1Activity.class));
                    break;
                case R.id.tv_home_creditcall:
                    calling();
                    break;
                case R.id.rl_home_creditClass:
                    getActivity().startActivity(new Intent(getActivity(), CreditPlatListActivity.class));
                    break;

                case R.id.rl_home_consult:
                    /*HomeActivity activity = (HomeActivity) getActivity();
                    activity.setCurrentItem(1);
                    ConsultPageFragment consultPageFragment = (ConsultPageFragment) activity.pageFragments.get(1);
                    consultPageFragment.switchState(1);*/
                    Intent intent1 = new Intent(getActivity(), ChattingActivity.class);
                    intent1.putExtra("toPhone","18689463192");
                    intent1.putExtra("toNickname", Constant.adviserDefaultName);
                    intent1.putExtra("toIconUrl",Constant.adviserDefaultIcon);
                    startActivity(intent1);
                    break;

                case R.id.iv_home_qr:
                    ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(getContext());
                    chooseAlertDialogUtil.setAlertDialogText();
                    break;

                case R.id.tv_home_loadfailtext:
                    reDownloadData();
                    break;
            }
        }
    }

    private void reDownloadData() {
        MyApplication application = (MyApplication) getActivity().getApplication();

        if (mCurApkInfoDownloadFinshed!=Constant.curdownloadType_success){
            //重新加载apk
            ApkVersionUtil.initApkData(application);
        }

        if (mCurArticleListDownloadFinshed!=Constant.curdownloadType_success){
            //重新加载文章
            ApkVersionUtil.loadArticleListtData(application);
        }
    }

    private void calling() {
        String phone = "0000000";
        String contactPhone = MyApplication.sharedPreferences.getString("contactPhone", "");
        if (!contactPhone.equals("")){
            phone = contactPhone;
            MobclickAgent.onEvent(getActivity(), "calling");
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+phone));
            startActivity(intent);
        }

    }

    //加载初始化数据
    private void loadInitData() {
        BmobQuery<StrategyArticle> bmobQuery = new BmobQuery<>();

        bmobQuery.order("articleId");
        bmobQuery.findObjects(new FindListener<StrategyArticle>() {
            @Override
            public void done(List<StrategyArticle> list, BmobException e) {
                if (e==null){
                    articleListListContent.clear();
                    if (list!=null && list.size()>0){
                        Log.i(TAG,"list:"+list.toString());
                        articleListListContent.addAll(list);
                        ArticlelistAdapter strategyAdapter = new ArticlelistAdapter(getActivity(),articleListListContent);
                        lv_strategy_articlelist1.setAdapter(strategyAdapter);
                    }
                }
                else {
                    Toast.makeText(getActivity(),"失败,请检查网络"+e, Toast.LENGTH_SHORT).show();
                }
            }

        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.messageType){
            case msgType_RequestNetwork_APK:
                Log.i(TAG,"apk下载" );
                onApkDownloadFinshed(event);
                break;
            case msgType_RequestNetwork_ArticleList:
                Log.i(TAG,"文章下载" );
                onArticleListDownloadFinshed(event);
                break;
        }
    }


    private void onApkDownloadFinshed(MessageEvent event) {
        if (event.singleValue== EventbusProxy.success){
            //下载成功
            mCurApkInfoDownloadFinshed = Constant.curdownloadType_success;
            mApk = (Apk) event.object;
            Log.i(TAG,"mApk:"+mApk);
            if (mApk !=null){
                boolean isuploadApkTest = mApk.isIsuploadApkTest();
                if (isuploadApkTest){
                    //需要加载文章界面
                    if (mCurArticleListDownloadFinshed == Constant.curdownloadType_success){
                        ll_home_view.setVisibility(View.GONE);
                        lv_strategy_articlelist1.setVisibility(View.VISIBLE);
                        tv_home_loadfailtext.setVisibility(View.GONE);
                        pb_progress.setVisibility(View.GONE);

                        mStrategyAdapter = new ArticlelistAdapter(getActivity(),articleListListContent);
                        lv_strategy_articlelist1.setAdapter(mStrategyAdapter);
                    }
                    else {
                        tv_home_loadfailtext.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    //展示原有界面
                    ll_home_view.setVisibility(View.VISIBLE);
                    lv_strategy_articlelist1.setVisibility(View.GONE);
                    tv_home_loadfailtext.setVisibility(View.GONE);
                    pb_progress.setVisibility(View.GONE);
                }
            }
        }else {
            //下载失败
            if (mApk!=null && mApk.isIsuploadApkTest()){
                tv_home_loadfailtext.setVisibility(View.VISIBLE);
            }
            mCurApkInfoDownloadFinshed = Constant.curdownloadType_failure;
            pb_progress.setVisibility(View.GONE);
        }

    }

    private void onArticleListDownloadFinshed(MessageEvent event) {

        Log.i(TAG,"文章list:"+event.object);
        if (event.singleValue== EventbusProxy.success){
            //下载成功
            mCurArticleListDownloadFinshed = Constant.curdownloadType_success;
            List<StrategyArticle> list = (List<StrategyArticle>) event.object;
            if (list!=null && list.size()>0){
                articleListListContent = list;
            }

            if (mCurApkInfoDownloadFinshed == Constant.curdownloadType_success){
                if (mApk!=null && mApk.isIsuploadApkTest()){
                    ll_home_view.setVisibility(View.GONE);
                    lv_strategy_articlelist1.setVisibility(View.VISIBLE);
                    tv_home_loadfailtext.setVisibility(View.GONE);
                    pb_progress.setVisibility(View.GONE);
                    mStrategyAdapter = new ArticlelistAdapter(getActivity(),articleListListContent);
                   lv_strategy_articlelist1.setAdapter(mStrategyAdapter);
                    Log.i(TAG,"更新文章");
                }
            }
            else {
                tv_home_loadfailtext.setVisibility(View.VISIBLE);
            }

        }else {
            //下载失败
            mCurArticleListDownloadFinshed = Constant.curdownloadType_failure;
            tv_home_loadfailtext.setVisibility(View.VISIBLE);
            pb_progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}