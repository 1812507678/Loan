package zhiyuan.com.loan.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import zhiyuan.com.loan.R;
import zhiyuan.com.loan.util.MyUtils;

public class InstallGuideActivity extends BaseActivity {
    int [] imageArg = {R.drawable.guide1,R.drawable.guide2,R.drawable.guide3,R.drawable.guide4};
    private View view_red_point;
    private Button bt_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_guide);

        initView();
    }

    private void initView() {
        ViewPager vp_guide_guide = (ViewPager) findViewById(R.id.vp_guide_guide);
        LinearLayout ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);
        view_red_point = findViewById(R.id.view_red_point);
        bt_start = (Button) findViewById(R.id.bt_start);

        vp_guide_guide.setAdapter(new MyViewPageAdapter());
        vp_guide_guide.setOffscreenPageLimit(1);


        //初始化引导页的小圆点
        for (int i=0;i<imageArg.length;i++ ){
            View point = new View(this);
            point.setBackgroundResource(R.drawable.shape_point_gray);
            int value = (int) MyUtils.dp2px(this, 10);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(value,value);
            if (i>0){
                //设置圆点间隔
                params.leftMargin = value ;
            }
            //设置圆点大小
            point.setLayoutParams(params);
            //将圆点添加给线性布局
            ll_point_group.addView(point);
        }


        vp_guide_guide.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int v = (int) MyUtils.dp2px(InstallGuideActivity.this, 20);
                int len =  (int) (v * positionOffset) + position*v;
                //获取当前红点的布局参数
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view_red_point
                        .getLayoutParams();
                //设置左边距
                params.leftMargin = len ;

                //重新给小蓝点设置布局参数
                view_red_point.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                if (position == imageArg.length -1){
                    bt_start.setVisibility(View.VISIBLE);
                }else {
                    bt_start.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void startGo(View view) {
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }

    class MyViewPageAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return imageArg.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.i("instantiateItem","instantiateItem");
            ImageView imageView = new ImageView(InstallGuideActivity.this);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageArg[position], options);
            imageView.setImageBitmap(bitmap);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}

