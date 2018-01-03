package zhiyuan.com.loan.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.List;
import java.util.Map;

import zhiyuan.com.loan.R;
import zhiyuan.com.loan.util.ShowNotificationBarUtil;

public class CoreService extends Service {
    private static final String TAG = CoreService.class.getSimpleName();

    public CoreService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand");
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        Map<String, EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();
        Log.i(TAG,"allConversations:"+allConversations);
        if (allConversations!=null && allConversations.size()>0){
            EMConversation next = allConversations.values().iterator().next();
            if (next!=null){
                int unreadMsgCount = next.getUnreadMsgCount();
                if (unreadMsgCount>0){
                    EMMessage emMessage = next.getLastMessage();
                    Log.i(TAG,"emMessage:"+emMessage);
                    String content = "客服 : "+emMessage.getBody().toString().split(":")[1].split("\"")[1];
                    ShowNotificationBarUtil.setServiceForegrounByNotify(getResources().getString(R.string.app_name),content,CoreService.this,emMessage);
                }
            }
        }
        return START_STICKY;
    }

    //消息监听器
    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            Log.i(TAG,"onMessageReceived:"+messages);
            if (messages.size()>0){
                EMMessage emMessage = messages.get(messages.size() - 1);String x= "\"8\"";
                Log.i(TAG,"emMessage:"+emMessage);
                String content = "客服 : "+emMessage.getBody().toString().split(":")[1].split("\"")[1];
                ShowNotificationBarUtil.setServiceForegrounByNotify(getResources().getString(R.string.app_name),content,CoreService.this,emMessage);
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> messages) {

        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

}
