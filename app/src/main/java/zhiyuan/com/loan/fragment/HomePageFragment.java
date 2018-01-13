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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import zhiyuan.com.loan.R;
import zhiyuan.com.loan.activity.ChattingActivity;
import zhiyuan.com.loan.activity.CommitMaterialStep1Activity;
import zhiyuan.com.loan.activity.CreditPlatListActivity;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.util.ChooseAlertDialogUtil;
import zhiyuan.com.loan.util.Constant;

/**
 * Created by Administrator on 2016/7/7.
 */
public class HomePageFragment  extends Fragment {

    private static final String TAG = "StrategyPageFragment";
    private View inflate;
    private ViewPager vp_home_msg;
    int previousSelectedPosition = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.frgament_page_home, new LinearLayout(getActivity()),false);

        initView();

        return inflate;
    }

    private void initView() {
        initViewPage();
        TextView tv_home_creditStep = (TextView) inflate.findViewById(R.id.tv_home_creditStep);
        TextView tv_home_creditcall = (TextView) inflate.findViewById(R.id.tv_home_creditcall);
        RelativeLayout rl_home_creditClass = (RelativeLayout) inflate.findViewById(R.id.rl_home_creditClass);
        RelativeLayout rl_home_consult = (RelativeLayout) inflate.findViewById(R.id.rl_home_consult);
        ImageView iv_home_qr = inflate.findViewById(R.id.iv_home_qr);

        HomeOnClickListener homeOnClickListener = new HomeOnClickListener();
        tv_home_creditStep.setOnClickListener(homeOnClickListener);
        tv_home_creditcall.setOnClickListener(homeOnClickListener);
        rl_home_creditClass.setOnClickListener(homeOnClickListener);
        rl_home_consult.setOnClickListener(homeOnClickListener);
        iv_home_qr.setOnClickListener(homeOnClickListener);

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
            Log.i(TAG,"instantiateItem newPosition:"+newPosition);

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
            Log.i(TAG,"destroyItem newPosition:"+i);
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
            }
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


}