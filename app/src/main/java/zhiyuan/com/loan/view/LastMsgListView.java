package zhiyuan.com.loan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import zhiyuan.com.loan.R;

/**
 * Created by Administrator on 2016/8/10.
 */
public class LastMsgListView extends ListView {

    private static final String TAG = "LastMsgListView";
    private View viewHead;
    private int startY;
    public  int state = 0;
    private int STATE_PULLTOREFRESH = 0;
    private int STATE_UNDOTOREFRESH = 1;
    private int STATE_REFRESHING = 2;
    public  TextView tv_head_msg;
    public  ImageView iv_head_icon;
    private RotateAnimation rotateAnimation;
    private RotateAnimation circleAnimation;
    public View viewFoot;
    public ImageView iv_chatt_refresh;
    private boolean isFoot;


    public LastMsgListView(Context context) {
        super(context);
        addHead(context);
        addFoot(context);
        inintView(context);
    }

    public LastMsgListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addHead(context);
        addFoot(context);
        inintView(context);
    }

    public LastMsgListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addHead(context);
        addFoot(context);
        inintView(context);
    }

    private void addHead(Context context) {
        viewHead = View.inflate(context, R.layout.lastmsg_view_head, null);
        viewHead.measure(2,2);
        setPadding(0, -viewHead.getMeasuredHeight(),0,0);
        addHeaderView(viewHead);
    }

    private void addFoot(Context context) {
        viewFoot = View.inflate(context, R.layout.view_head, null);
        viewFoot.measure(2,2);
        viewFoot.setPadding(0,0,0,-viewFoot.getMeasuredHeight());
        addFooterView(viewFoot);
    }

    private void inintView(Context context) {
        tv_head_msg = (TextView) findViewById(R.id.tv_head_msg);
        iv_head_icon = (ImageView) findViewById(R.id.iv_head_icon);
        iv_chatt_refresh = (ImageView) findViewById(R.id.iv_chatt_refresh);

        rotateAnimation = new RotateAnimation(0,180, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(100);
        rotateAnimation.setFillAfter(true);

        circleAnimation = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        circleAnimation.setDuration(500);
        circleAnimation.setRepeatCount(-1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //只有顶部和底部才有刷新和加载更多操作，其他位置无操作
        if (getFirstVisiblePosition()!=0 && getLastVisiblePosition()!=getCount()-1 ){
            return super.onTouchEvent(ev);
        }

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int currentY = (int) ev.getY();
                int varyY = (currentY- startY);

                //刷新
                if (varyY>0){
                    if (getFirstVisiblePosition()!=0){
                        return super.onTouchEvent(ev);
                    }

                    int downY = -viewHead.getMeasuredHeight() + varyY;

                    setPadding(0,downY/2,0,0);
                    Log.i(TAG,"downY:"+downY);
                    Log.i(TAG,"state:"+state);

                    if (downY>0 && state==STATE_PULLTOREFRESH){
                        state = STATE_UNDOTOREFRESH;
                        tv_head_msg.setText("松手刷新");
                        iv_head_icon.setAnimation(rotateAnimation);
                        rotateAnimation.start();
                    }
                }
                //加载更多
                else{
                    if (getLastVisiblePosition()!=getCount()-1 ){
                        return super.onTouchEvent(ev);
                    }
                    if (!isFoot){
                        viewFoot.setPadding(0,0,0,0);
                        iv_chatt_refresh.setAnimation(circleAnimation);
                        circleAnimation.start();
                        isFoot = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isFoot){
                    refreshDataListener.loadMore();
                    isFoot = false;
                }

                else {
                    if (state == STATE_UNDOTOREFRESH){
                        state = STATE_REFRESHING;
                        iv_head_icon.clearAnimation();
                        iv_head_icon.setImageResource(R.drawable.default_ptr_rotate);
                        iv_head_icon.setAnimation(circleAnimation);
                        circleAnimation.start();

                        tv_head_msg.setText("正在刷新");
                        setPadding(0,0,0,0);
                        if (refreshDataListener!= null){
                            refreshDataListener.refresh();
                        }
                    }
                    else if (state == STATE_PULLTOREFRESH){
                        state = STATE_PULLTOREFRESH;
                        tv_head_msg.setText("下拉刷新");
                        iv_head_icon.clearAnimation();
                        iv_head_icon.setImageResource(R.drawable.indicator_arrow);
                        setPadding(0,-viewHead.getMeasuredHeight(),0,0);
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
    RefreshDataListener refreshDataListener;

    public void setRefreshDataListener(RefreshDataListener refreshDataListener){
        this.refreshDataListener = refreshDataListener;
    }

    public interface RefreshDataListener{
        void refresh();
        void loadMore();
    }
}
