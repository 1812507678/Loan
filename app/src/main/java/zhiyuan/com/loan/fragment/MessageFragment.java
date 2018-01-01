package zhiyuan.com.loan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;
import com.lidroid.xutils.BitmapUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import zhiyuan.com.loan.R;
import zhiyuan.com.loan.activity.ChattingActivity;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.bean.LastMessage;
import zhiyuan.com.loan.util.MessageComparator;
import zhiyuan.com.loan.view.LastMsgListView;


/**
 * Created by Administrator on 2016/7/8.
 */

public class MessageFragment extends Fragment {
	private static final String TAG ="MessageFragment" ;
	private View inflate;

	private List<LastMessage> lastMessageList = new ArrayList<>();
	private MyListViewAdapter myListViewAdapter;
	private LastMsgListView lv_msg_message;

	private String phone;
	private TextView tv_msg_noMsgContent;
	private int  showCount;
	private boolean isLogon ;
	private String password;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		inflate = inflater.inflate(R.layout.fragment_message, new LinearLayout(getActivity()),false);

		phone = MyApplication.sharedPreferences.getString("phone", "");
		password = MyApplication.sharedPreferences.getString("password", "");
		if (!phone.equals("")){
			login(phone, password);
		}

		initView();
		return inflate;
	}

	private void initView() {
		lv_msg_message = (LastMsgListView) inflate.findViewById(R.id.lv_msg_message);
		tv_msg_noMsgContent = (TextView) inflate.findViewById(R.id.tv_msg_noMsgContent);

		myListViewAdapter = new MyListViewAdapter();
		lv_msg_message.setAdapter(myListViewAdapter);

		//设置点击刷新
		tv_msg_noMsgContent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//已经登录了，刷新时重新加载数据
				if (isLogon){
					loadNetInitData();
					if (lastMessageList.size()>0){
						tv_msg_noMsgContent.setVisibility(View.GONE);
						myListViewAdapter.notifyDataSetChanged();
					}
				}

				//注：存在问题，因为在登陆失败的情况下一般是没网，这时环信SDK初始化失败，登陆异常
				//没有登录，需重新登陆
				else {
					if (!phone.equals("")){
						login(phone, password);
					}
				}
			}
		});

		lv_msg_message.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LastMessage lastMessage = lastMessageList.get(position-1);
				String phone = lastMessage.getPhone();
				String nickname = lastMessage.getNickName();
				String iconUrl = lastMessage.getIconUrl();

				Intent intent = new Intent(getActivity(), ChattingActivity.class);
				intent.putExtra("toPhone",phone);
				intent.putExtra("toNickname",nickname);
				intent.putExtra("toIconUrl",iconUrl);
				EMConversation conversation = EMClient.getInstance().chatManager().getConversation(phone);
				//指定会话消息未读数清零
				if (conversation!=null){
					conversation.markAllMessagesAsRead();
				}
				MessageFragment.this.startActivityForResult(intent,199);
			}

		});

		//下拉刷新监听
		lv_msg_message.setRefreshDataListener(new LastMsgListView.RefreshDataListener() {
			@Override
			public void refresh() {
				//开启线程，在线程中睡1秒后刷新消失，模拟下载过程
				new Thread(){
					@Override
					public void run() {
						super.run();
						try {
							Thread.sleep(1000);
							loadNetInitData();
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									View viewHead = lv_msg_message.getChildAt(0);
									Log.i(TAG,"viewHead.getMeasuredHeight():"+viewHead.getMeasuredHeight());
									lv_msg_message.setPadding(0,-viewHead.getMeasuredHeight(),0,0);
									lv_msg_message.state = 0;
									lv_msg_message.tv_head_msg.setText("下拉刷新");
									lv_msg_message.iv_head_icon.clearAnimation();
									lv_msg_message.iv_head_icon.setImageResource(R.drawable.indicator_arrow);

									if (lastMessageList.size()>0){
										tv_msg_noMsgContent.setVisibility(View.GONE);
									}
									myListViewAdapter.notifyDataSetChanged();
								}
							});

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}.start();

			}

			@Override
			public void loadMore() {
				lv_msg_message.iv_chatt_refresh.clearAnimation();
				lv_msg_message.viewFoot.setPadding(0,0,0,-lv_msg_message.viewFoot.getMeasuredHeight());
			}
		});
	}

	//初始化聊天信息列表
	public void loadNetInitData(){
		lastMessageList.clear();
		Map<String, EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();
		Log.i(TAG,"allConversations.size():"+allConversations.size());
		Log.i(TAG,"allConversations:"+allConversations);

		Collection<EMConversation> values = allConversations.values();
		for (EMConversation next : values) {
			Log.i(TAG, "next:" + next);
			/*if (!next.getUserName().equals(phone)) {
				EMConversation conversation = EMClient.getInstance().chatManager().getConversation(next.getUserName());
				int unreadMsgCount = conversation.getUnreadMsgCount();

				EMMessage emMessage = conversation.getLastMessage();
				if (emMessage == null) {
					continue;
				}

				String iconUrl = "";
				String nickName = "";
				try {
					iconUrl = emMessage.getStringAttribute("toIconUrl");
					nickName = emMessage.getStringAttribute("toNickName");

				} catch (HyphenateException e) {
					e.printStackTrace();
				}

				Log.i(TAG, "iconUrl:" + iconUrl);
				Log.i(TAG, "nickname:" + nickName);
				//Log.i(TAG, "next.getUserName():" + next.getUserName());

				String adIconUrl = MyApplication.sharedPreferences.getString("adIconUrl", "");
				String adNickname = MyApplication.sharedPreferences.getString("adNickname", "");

				if (iconUrl.equals("") && nickName.equals("")){
					iconUrl = adIconUrl;
					nickName = adNickname;
				}

				String messageTime = getSpecfFormatTime(emMessage.getMsgTime());
				String message = "";
				String body = emMessage.getBody().toString();
				String[] split = body.split(":");
				switch (split[0]) {
					case "txt":
						//文字
						message = split[1].split("\"")[1];
						break;
					case "voice":
						//文字
						message = "[语音]";
						break;
					case "image":
						//图片
						message = "[图片]";
						break;
				}

				//lastMessageList.add(new LastMessage(next.getUserName(), message, messageTime, iconUrl, nickName, unreadMsgCount));
			}*/
		}

		MessageComparator messageComparator = new MessageComparator();
		Collections.sort(lastMessageList,messageComparator);

		for (int i=0;i<lastMessageList.size();i++){
			LastMessage lastMessage = lastMessageList.get(i);
			Log.i(TAG,"lastMessage"+lastMessage.toString());
		}
	}
	@Override
	public void onStart() {
		super.onStart();

		//showCount变量保证在第一次显示时不重复加载，下一次onStart()执行时再进行加载数据
		if (showCount!=0){
			loadNetInitData();
			if (myListViewAdapter!=null){
				myListViewAdapter.notifyDataSetChanged();
			}
		}
		showCount++;
	}


	//环信账号登陆
	public void login(String username,String password){
		EMClient.getInstance().login(username,password,new EMCallBack() {
			@Override
			public void onSuccess() {
				showCount = 0;
				isLogon = true;

				//加载数据
				loadNetInitData();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (lastMessageList.size()>0){
							tv_msg_noMsgContent.setVisibility(View.GONE);
							myListViewAdapter.notifyDataSetChanged();
						}

						Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_SHORT).show();
					}
				});

				Log.i(TAG, "登录聊天服务器成功！");
				//添加消息监听
				EMClient.getInstance().chatManager().addMessageListener(msgListener);
				//添加聊天监听器
				addMConnectionListener();
			}

			@Override
			public void onProgress(int progress, String status) {

			}


			@Override
			public void onError(int code, String message) {
				Looper.prepare();
				Toast.makeText(getActivity(), "登录失败:"+message, Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		});

	}

	//退出登陆
	public static void exit(){
		//EMClient.getInstance().logout(true);//此方法为同步方法，里面的参数 true 表示退出登录时解绑 GCM 或者小米推送的 token

		//此方法为异步方法
		EMClient.getInstance().logout(true, new EMCallBack() {

			@Override
			public void onSuccess() {
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, String message) {
			}
		});
	}

	public void addMConnectionListener(){
		//注册一个监听连接状态的listener
		EMClient.getInstance().addConnectionListener(new MyConnectionListener());

	}

	//实现ConnectionListener接口
	private class MyConnectionListener implements EMConnectionListener {
		@Override
		public void onConnected() {
		}
		@Override
		public void onDisconnected(final int error) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if(error == EMError.USER_REMOVED){
						// 显示帐号已经被移除
						Toast.makeText(getActivity(), "服务器断开，帐号已经被移除", Toast.LENGTH_SHORT).show();
					}

					else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
						// 显示帐号在其他设备登录
						Toast.makeText(getActivity(), "服务器断开，帐号在其他设备登录", Toast.LENGTH_SHORT).show();
					}
					else {
						if (NetUtils.hasNetwork(getActivity())){
							//连接不到聊天服务器
							Toast.makeText(getActivity(), "服务器断开，连接不到聊天服务器", Toast.LENGTH_SHORT).show();
						}
						else{
							//当前网络不可用，请检查网络设置
							Toast.makeText(getActivity(), "服务器断开，当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
						}

					}
				}
			});
		}
	}

	class MyListViewAdapter extends BaseAdapter{
		BitmapUtils bitmapUtils;

		public MyListViewAdapter() {
			bitmapUtils = new BitmapUtils(getActivity());
		}

		@Override
		public int getCount() {
			return lastMessageList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LastMessage lastMessage = lastMessageList.get(position);

			View inflate = View.inflate(getActivity(), R.layout.message_item_listview, null);
			FrameLayout fl_item_point = (FrameLayout) inflate.findViewById(R.id.fl_item_point);
			TextView tv_item_unread = (TextView) inflate.findViewById(R.id.tv_item_unread);
			TextView tv_item_time = (TextView) inflate.findViewById(R.id.tv_item_time);
			TextView tv_item_message = (TextView) inflate.findViewById(R.id.tv_item_message);
			TextView tv_item_nickname = (TextView) inflate.findViewById(R.id.tv_item_nickname);
			ImageView iv_item_icon = (ImageView) inflate.findViewById(R.id.iv_item_icon);

			//有未读
			if(lastMessage.getMessageCount()>0){
				fl_item_point.setVisibility(View.VISIBLE);
				tv_item_unread.setText(""+lastMessage.getMessageCount());
			}

			tv_item_time.setText(lastMessage.getLastTime());
			tv_item_message.setText(lastMessage.getLastMessage());
			tv_item_nickname.setText(lastMessage.getNickName());

			String iconUrl = lastMessage.getIconUrl();
			if (!iconUrl.equals("")){
				bitmapUtils.display(iv_item_icon,iconUrl);
			}

			return inflate;
		}

	}

	//消息监听器
	EMMessageListener msgListener = new EMMessageListener() {

		@Override
		public void onMessageReceived(List<EMMessage> messages) {
			Log.i(TAG,"onMessageReceived");
			//收到消息

			EMMessage emMessage = messages.get(messages.size()-1);
			String fromName = emMessage.getFrom();

			String iconUrl = "";
			String pickname = "";

			try {
				iconUrl = emMessage.getStringAttribute("toIconUrl");
				pickname = emMessage.getStringAttribute("toNickName");

			} catch (HyphenateException e) {
				e.printStackTrace();
			}

			Log.i(TAG, "msgListener==iconUrl:" + iconUrl);
			Log.i(TAG, "msgListener==nickname:" + pickname);

			if (iconUrl.equals("") && pickname.equals("")){
				iconUrl = MyApplication.sharedPreferences.getString("iconUrl", "");
				pickname = MyApplication.sharedPreferences.getString("nickname", "");
			}

			Log.i(TAG, "msgListener==iconUrl:" + iconUrl);
			Log.i(TAG, "msgListener==nickname:" + pickname);

			Log.i(TAG,"emMessage:"+emMessage);
			final String body = emMessage.getBody().toString();

			String time = getSpecfFormatTime(emMessage.getMsgTime());
			String[] split = body.split(":");
			String message = "";

			switch (split[0]){
				case "txt":
					//文字    from:haijun0007, to:haijun0001 body:txt:"123"
					message = split[1].split("\"")[1];
					break;
				case "voice":
					message  = "语音";
					break;
				case "image":
					message  = "图片";
					break;
			}

			//isNewUser保证有重复的消息收到时不会新建列表
			boolean isNewUser = true;
			for (int i=0;i<lastMessageList.size();i++){
				if (lastMessageList.get(i).getPhone().equals(fromName)){
					isNewUser = false;
					lastMessageList.get(i).setLastMessage(message);
					lastMessageList.get(i).setMessageCount(lastMessageList.get(i).getMessageCount()+1);
					lastMessageList.get(i).setLastTime(time);
				}
			}
			//新的对话
			if (isNewUser){
				lastMessageList.add(new LastMessage(emMessage.getUserName(),message,time,iconUrl,pickname,1));
			}

			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (myListViewAdapter!=null){
						myListViewAdapter.notifyDataSetChanged();
					}
				}
			});

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
		public void onMessageChanged(EMMessage message, Object change) {
			//消息状态变动
		}
	};

	public String getSpecfFormatTime(long time){
		//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);  //07-12 15:10
		return format.format(new Date(time));
	}

	@Override
	public void onDestroy() {
		exit();
		EMClient.getInstance().chatManager().removeMessageListener(msgListener);
		super.onDestroy();
		Log.i(TAG,"onDestroy");
	}
}

