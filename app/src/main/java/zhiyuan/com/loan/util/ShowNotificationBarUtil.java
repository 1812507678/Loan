package zhiyuan.com.loan.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.hyphenate.chat.EMMessage;

import zhiyuan.com.loan.R;
import zhiyuan.com.loan.activity.BaseActivity;
import zhiyuan.com.loan.activity.ChattingActivity;
import zhiyuan.com.loan.application.MyApplication;


/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.utils
 * @time 12/6/2017 5:44 PM
 * @describe
 */
public class ShowNotificationBarUtil {
    public static final int notifyActivityIndex_StartRunActivity = 1;
    public static final int notifyActivityIndex_HealthyDataActivity = 2;
    public static final int notifyActivityIndex_InsoleRunningActivity = 3;

    public static Service mContext;


    public static void setServiceForegrounByNotify(String title , String content, Service context, EMMessage emMessage) {
        BaseActivity baseActivity = MyApplication.getInstance().getmCurrApplicationActivity();
        if (!(baseActivity instanceof ChattingActivity)){
            if (mContext==null){
                mContext = context;
            }

            //NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.logo)
                    .setOngoing(true)
                    .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))
                    .build();
            //notification.flags |= Notification.FLAG_NO_CLEAR;
            //给另一个设置任务栈属性，FLAG_ACTIVITY_NEW_TASK表示新建一个任务栈来显示当前的Activity
            //PendingIntent 主要用于任务栏提醒和桌面weigetde 显示，

            //这里用4个参数需要注意下，130表示requestCode（请求马，自定义）
            //第三个参数书Intent对象，intent1是上面定义的 Intent对象
            //第四个对象是PendingIntent的标签属性，表叔显示方式，这里FLAG_UPDATE_CURRENT表示显示当前的通知，如果用新的通知时，更新当期的通知，这个属性注意下，如果不设置的话每次点击都是同一个通知


            Intent intent1 = new Intent(context, ChattingActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("toPhone",emMessage.getFrom());
            intent1.putExtra("toNickname",Constant.adviserDefaultName);
            intent1.putExtra("toIconUrl",Constant.adviserDefaultIcon);

            PendingIntent activity = PendingIntent.getActivity(context, 130, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.contentIntent = activity;
            //nm.notify(0, notification);
            context.startForeground(1, notification); //将Service设置为前台服务
        }

    }

    public static void  detoryServiceForegrounByNotify(){
        if (mContext!=null){
            mContext.stopForeground(true);
            mContext = null;
        }
    }


}
