package zhiyuan.com.loan.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import zhiyuan.com.loan.R;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.bean.Message;
import zhiyuan.com.loan.loader.ImageLoader;
import zhiyuan.com.loan.util.Constant;
import zhiyuan.com.loan.util.MyUtils;


public class ChattingActivity extends BaseActivity {

	private static final String TAG = "ChattingActivity";
	private TextView tv_chatt_nickname;
	private ImageView iv_chatt_send;
	private Button bt_chatt_send;
	private EditText et_chatt_message;

	private List<Message> messageList = new ArrayList<>();
	private ListView lv_chatt_message;
	private MyListViewAdapter myListViewAdapter;
	private String toPhone;
	private AlertDialog alertDialog;
	private EMMessage lastMessage;
	private RotateAnimation rotateAnimation;
	private boolean isRefreshable = false;
	private int startY;
	private int viewHeadMeasuredHeight;
	private RelativeLayout viewHead;

	private boolean EDITTEXTSTATE = true;
	private TextView tv_chatt_record;
	private ImageView iv_chatt_recordicon;
	private MediaRecorder mediaRecorder;
	private File currectFile;
	private long startTime;
	private boolean isStartRecord = false;
	private LinearLayout ll_chatt_addother;
	private ImageView iv_chatt_tackpicture;
	private ImageView iv_chatt_choosepicture;
	private File currentImageSaveFile;
	private boolean mIsGridViewIdle = true;
	private ImageLoader mImageLoader;
	private String toIconUrl;
	private String toNickName;
	private String myNickname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatting);
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		initView();
		initData();
	}

	private void initView() {
		Intent intent = getIntent();
		toPhone = intent.getStringExtra("toPhone");
		toNickName = intent.getStringExtra("toNickname");
		toIconUrl = intent.getStringExtra("toIconUrl");

		Log.i(TAG,"initView  nickname:"+ toNickName);
		Log.i(TAG,"initView  toIconUrl:"+ toIconUrl);

		myNickname = MyApplication.sharedPreferences.getString("nickname","");
		mImageLoader = ImageLoader.build(this);
		tv_chatt_nickname = (TextView) findViewById(R.id.tv_chatt_nickname);
		final ImageView iv_chatt_recorde = (ImageView) findViewById(R.id.iv_chatt_recorde);
		tv_chatt_record = (TextView) findViewById(R.id.tv_chatt_record);
		iv_chatt_recordicon = (ImageView) findViewById(R.id.iv_chatt_recordicon);
		ll_chatt_addother = (LinearLayout) findViewById(R.id.ll_chatt_addother);
		iv_chatt_tackpicture = (ImageView) findViewById(R.id.iv_chatt_tackpicture);
		iv_chatt_choosepicture = (ImageView) findViewById(R.id.iv_chatt_choosepicture);
		iv_chatt_send =  (ImageView) findViewById(R.id.iv_chatt_send);


		MyOnClickListener myOnClickListener = new MyOnClickListener();
		iv_chatt_tackpicture.setOnClickListener(myOnClickListener);
		iv_chatt_choosepicture.setOnClickListener(myOnClickListener);
		iv_chatt_send.setOnClickListener(myOnClickListener);
		tv_chatt_nickname.setText(toNickName);
		et_chatt_message = (EditText) findViewById(R.id.et_chatt_message);
		bt_chatt_send = (Button) findViewById(R.id.bt_chatt_send);
		lv_chatt_message = (ListView) findViewById(R.id.lv_chatt_message);

		rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(500);
		rotateAnimation.setRepeatCount(2);

		//输入框文本变化监听器
		et_chatt_message.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!et_chatt_message.getText().toString().equals("")){
					iv_chatt_send.setVisibility(View.INVISIBLE);
					bt_chatt_send.setVisibility(View.VISIBLE);
				}
				else {
					ChattingActivity.this.iv_chatt_send.setVisibility(View.VISIBLE);
					bt_chatt_send.setVisibility(View.INVISIBLE);
				}
			}
		});

		//输入框点击事件
		et_chatt_message.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()==MotionEvent.ACTION_DOWN){
					if (myListViewAdapter!=null){
						lv_chatt_message.setSelection(myListViewAdapter.getCount()-1);
						ll_chatt_addother.setVisibility(View.GONE);
					}
				}
				return false;
			}
		});

		//发送消息点击事件
		bt_chatt_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String message = et_chatt_message.getText().toString();
				if (!message.equals("")){
					et_chatt_message.setText("");
					String time = getCurrentFormatTime();
					Message firstMessage = new Message(time, "", message, 0, Constant.MESSAGETYPPE_TXT);
					messageList.add(firstMessage);
					myListViewAdapter.notifyDataSetChanged();
					lv_chatt_message.setSelection(myListViewAdapter.getCount()-1);
					sendTextMessage(message,toPhone);
				}
				else {
					Toast.makeText(ChattingActivity.this,"请输入",Toast.LENGTH_SHORT).show();
				}
			}
		});

		//改ListView加入头部刷新布局
		viewHead = (RelativeLayout) View.inflate(this, R.layout.view_head, null);
		final ImageView iv_chatt_refresh = (ImageView) viewHead.findViewById(R.id.iv_chatt_refresh);
		viewHead.measure(2,2);
		viewHeadMeasuredHeight = -viewHead.getMeasuredHeight();
		viewHead.setPadding(0, viewHeadMeasuredHeight,0,0);
		lv_chatt_message.addHeaderView(viewHead);

		//ListView设置触摸事件
		lv_chatt_message.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (isRefreshable){
					switch (event.getAction()){
						case MotionEvent.ACTION_DOWN:
							startY = (int) event.getY();
							break;
						case MotionEvent.ACTION_MOVE:
							int currentY = (int) event.getY();
							int varyY = (currentY-startY);
							int downY = viewHeadMeasuredHeight + varyY;
							//下滑
							if (varyY>0 && Math.abs(downY)>10){
								viewHead.setPadding(0,0,0,0);
								iv_chatt_refresh.setAnimation(rotateAnimation);
								rotateAnimation.start();
							}
							break;
						case MotionEvent.ACTION_UP:
							loadMoreMessage();
							break;
					}
				}
				return false;
			}
		});

		//ListView设置滑动事件
		lv_chatt_message.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
					mIsGridViewIdle = true;
					myListViewAdapter.notifyDataSetChanged();

				} else {
					mIsGridViewIdle = false;
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//到顶部，可以滑动
				isRefreshable = firstVisibleItem == 0;
                /*相当于
                if (firstVisibleItem == 0) {
                    isRefreshable = true;
                } else {
                    isRefreshable = false;
                }*/
			}
		});

		//edit和record进行切换
		iv_chatt_recorde.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (EDITTEXTSTATE){
					iv_chatt_recorde.setImageResource(R.drawable.ease_chatting_setmode_keyboard_btn_normal);
					tv_chatt_record.setVisibility(View.VISIBLE);
					et_chatt_message.setVisibility(View.INVISIBLE);
					EDITTEXTSTATE = false;
				}
				else {
					iv_chatt_recorde.setImageResource(R.drawable.ease_chatting_setmode_voice_btn_normal);
					tv_chatt_record.setVisibility(View.INVISIBLE);
					et_chatt_message.setVisibility(View.VISIBLE);
					EDITTEXTSTATE = true;
				}
			}
		});
		tv_chatt_record.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				iv_chatt_recordicon.setVisibility(View.VISIBLE);
				recordVoice();

				return false;
			}
		});

		tv_chatt_record.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()){
					case MotionEvent.ACTION_UP:
						if (iv_chatt_recordicon.getVisibility()==View.VISIBLE){
							iv_chatt_recordicon.setVisibility(View.INVISIBLE);
							//判断是否正在录音
							if (isStartRecord){
								//调用MediaRecorder的start()与stop()间隔不能小于1秒(有时候大于1秒也崩)，否则必崩。
								//设置后不会崩
								if (mediaRecorder!=	null){
									mediaRecorder.setOnErrorListener(null);
									mediaRecorder.stop();
									mediaRecorder.release();
									mediaRecorder = null;
								}

								isStartRecord = false;

								long endTime = System.currentTimeMillis();
								int timeLength = (int) ((endTime - startTime)/1000);
								//判断时间不会太短，大于等于1秒
								if (timeLength>0){
									if (currectFile!=null){
										sendVioceMessage(currectFile.getAbsolutePath(), timeLength, toPhone);
										String time = getCurrentFormatTime();
										Message firstMessage = new Message(time, "", currectFile.getAbsolutePath(), 0, Constant.MESSAGETYPPE_VIOCE, timeLength);
										messageList.add(firstMessage);
										myListViewAdapter.notifyDataSetChanged();
										lv_chatt_message.setSelection(myListViewAdapter.getCount()-1);
									}
								}
								else {
									Toast.makeText(ChattingActivity.this,"时间太短",Toast.LENGTH_SHORT).show();
								}
							}
						}
						break;
				}
				return false;
			}
		});
	}

	private void initData() {
		myListViewAdapter = new MyListViewAdapter();
		lv_chatt_message.setAdapter(myListViewAdapter);
		isUserLogin();
		EMClient.getInstance().chatManager().addMessageListener(msgListener);
		Log.i(TAG,"initData==phone:"+toPhone);

		if (!TextUtils.isEmpty(toPhone)){
			EMConversation conversation = EMClient.getInstance().chatManager().getConversation(toPhone);
			if (conversation!=null){
				lastMessage = conversation.getLastMessage();
				if (lastMessage!=null){
					//收到消息
					////获取startMsgId之前的pagesize条消息，此方法获取的messages SDK会自动存入到此会话中，APP中无需再次把获取到的messages添加到会话中
					List<EMMessage> messages = conversation.loadMoreMsgFromDB(lastMessage.getMsgId(), 10);
					messages.add(lastMessage);
					Log.i(TAG,"lastMessage:"+lastMessage.toString());
					for (int i=messages.size()-1;i>=0;i--) {
						EMMessage emMessage = messages.get(i);
						if (emMessage!=null){
							addMessageListItem(emMessage,0);
						}
					}
					lastMessage = messages.get(0);
					Log.i(TAG, messages.size()+"");

					myListViewAdapter.notifyDataSetChanged();
					lv_chatt_message.setSelection(myListViewAdapter.getCount()-1);
				}
			}
		}

	}

	public void isUserLogin(){
		//咨询Fragment传过来的咨询师信息
		Intent intent = getIntent();
		String advisePhone = intent.getStringExtra("advisePhone");
		String adviseName = intent.getStringExtra("adviseName");
		String adviseIconUrl = intent.getStringExtra("adviseIconUrl");

		//
		if (advisePhone!=null){
			String userPhone = MyApplication.sharedPreferences.getString("phone", "");
			//用户尚未登录
			if (userPhone.equals("")){
				startActivity(new Intent(this,LoginActivity.class));
				finish();
			}
			this.toPhone = advisePhone;
			tv_chatt_nickname.setText(adviseName);
			toNickName = adviseName;
			toIconUrl = adviseIconUrl;
			Log.i(TAG,"phone:"+toPhone);
			Log.i(TAG,"name:"+adviseName);

			MyApplication.sharedPreferences.edit().putString("adIconUrl",adviseIconUrl).apply();
			MyApplication.sharedPreferences.edit().putString("adNickname",adviseName).apply();
		}
	}

	//改ListView添加一个消息条目
	public void addMessageListItem(EMMessage emMessage, int position){
		Log.i(TAG,"emMessage:"+emMessage);
		final String body = emMessage.getBody().toString();
		Log.i(TAG,"body:"+body);
		String time = getSpecfFormatTime(emMessage.getMsgTime());

		int tag = 0;
		if (emMessage.getFrom().equals(toPhone)){
			tag = 1;  //自己发出去的
		}

		String[] split = body.split(":");
		String message = "";
		String localUrl = "";
		int type = 0;
		int duration = 0;

		switch (split[0]){
			case "txt":
				//文字    from:haijun0007, to:haijun0001 body:txt:"123"
				message = split[1].split("\"")[1];
				type = Constant.MESSAGETYPPE_TXT;
				break;
			case "voice":
				//声音   from:haijun0007, to:haijun0001 body:voice:1468833981983.amr,localurl:/data/data/ctyon.com.ctsservice/cache/1468833981983.amr,remoteurl:https://a1.easemob.com/1812507678/ctsservice/chatfiles/c4c6a8f0-4cc9-11e6-be42-2b9057d2e71e,length:1
				message = split[2].split(",")[0];
				type = Constant.MESSAGETYPPE_VIOCE;
				duration = Integer.parseInt(split[split.length-1]);
				Log.i(TAG,"voice:message="+message);
				Log.i(TAG,"voice:duration="+duration);
				break;

			case "image":
				type = Constant.MESSAGETYPPE_IMG;
				if (tag==0){
					//图片   msg{from:haijun0001, to:haijun0007 body:image:QiYiVideo_transfer203145101_420932600_cover.jpg,localurl:/storage/emulated/0/QiYiVideo_Local/QiYiVideo_transfer203145101_420932600_cover.jpg,remoteurl:https://a1.easemob.com/1812507678/ctsservice/chatfiles/9aa203b0-4e2e-11e6-8be3-29d218df3a6d,thumbnial:https://a1.easemob.com/1812507678/ctsservice/chatfiles/9aa203b0-4e2e-11e6-8be3-29d218df3a6d
					localUrl = split[2].split(",")[0];
				}
				else {//接收到的
					//msg{from:admin, to:haijun0001 body:image:C:\fakepath\chatto_bg_focused.9.png,localurl:/storage/emulated/0/Android/data/ctyon.com.ctsservice/1812507678#ctsservice/files/haijun0001/admin/c2916ea0-4fd8-11e6-a95f-8bdb36be392d.png,remoteurl:https://a1.easemob.com/1812507678/ctsservice/chatfiles/c2916ea0-4fd8-11e6-a95f-8bdb36be392d,thumbnial:https://a1.easemob.com/1812507678/ctsservice/chatfiles/c2916ea0-4fd8-11e6-a95f-8bdb36be392d
					//有效图片路径："/storage/emulated/0/Android/data/ctyon.com.ctsservice/1812507678#ctsservice/files/haijun0001/admin/thumb_c2916ea0-4fd8-11e6-a95f-8bdb36be392d"
					localUrl = split[split.length-2]+":"+split[split.length-1];
					Log.i(TAG,"localUrl:"+localUrl);
				}

				break;

		}
		messageList.add(position,new Message(emMessage.getMsgId(),time,localUrl, message,tag,type,duration));
	}

	//发送文本消息
	private void sendTextMessage(String message,String nickname) {
		//创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
		EMMessage emMessage = EMMessage.createTxtSendMessage(message, nickname);
		//如果是群聊，设置chattype，默认是单聊
        /*if (chatType == CHATTYPE_GROUP)
            message.setChatType(EMMessage.ChatType.GroupChat);*/
		//发送消息
		if (emMessage!=null){
			addAttributeToMsg(emMessage);
			EMClient.getInstance().chatManager().sendMessage(emMessage);
		}
	}

	//发送语音消息
	private void sendVioceMessage(String filePath,int length, String toChatUsername) {
		//filePath为语音文件路径，length为录音时间(秒)
		EMMessage emMessage = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
		addAttributeToMsg(emMessage);
		EMClient.getInstance().chatManager().sendMessage(emMessage);
	}

	//发送图片消息
	private void sendImgMessage(String imagePath, String toChatUsername) {
		//imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
		EMMessage imageSendMessage = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
		addAttributeToMsg(imageSendMessage);
		EMClient.getInstance().chatManager().sendMessage(imageSendMessage);
	}

	//给消息添加属性
	public void addAttributeToMsg(EMMessage emMessage){
		emMessage.setAttribute("toIconUrl",toIconUrl);
		emMessage.setAttribute("toNickName",toNickName);

		emMessage.setAttribute("myIconUrl",MyApplication.sharedPreferences.getString("iconUrl",""));
		emMessage.setAttribute("myNickname",myNickname);
	}

	//录音
	private void recordVoice() {
		//File cacheDir = Environment.getExternalStorageDirectory();   ///storage/emulated/0/1468910151133.amr
		File cacheDir = getCacheDir();
		currectFile = new File(cacheDir,System.currentTimeMillis()+".amr");
		mediaRecorder = new MediaRecorder();
		        /*设定录音来源为麦克风*/
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mediaRecorder.setOutputFile(currectFile.getAbsolutePath());
		try {
			mediaRecorder.prepare();
			mediaRecorder.start();
			isStartRecord = true;
			startTime = System.currentTimeMillis();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//加载更多数据
	private void loadMoreMessage() {
		EMConversation conversation = EMClient.getInstance().chatManager().getConversation(toPhone);
		if (conversation!=null){
			if (lastMessage!=null){
				List<EMMessage> messages = conversation.loadMoreMsgFromDB(lastMessage.getMsgId(), 10);

				//大小
				final int size = messages.size();
				//加载到更多数据
				if (size>0){
					for (int i=messages.size()-1;i>=0;i--) {
						EMMessage emMessage = messages.get(i);
						addMessageListItem(emMessage,0);
					}
					lastMessage = messages.get(0);
				}
				else {
					Toast.makeText(ChattingActivity.this,"没有更多消息了",Toast.LENGTH_SHORT).show();
				}
				new Thread(){
					@Override
					public void run() {
						try {
							Thread.sleep(500);
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									viewHead.setPadding(0,viewHeadMeasuredHeight,0,0);
									if (size>0){
										myListViewAdapter.notifyDataSetChanged();
										lv_chatt_message.setSelection(size);
									}
								}
							});
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						super.run();
					}
				}.start();
			}
		}
	}


	class MyListViewAdapter extends BaseAdapter{
		private ImageLoader imageLoader;
		MyOnLongclickListener myOnLongclickListener;
		MyOnClickListener myOnClickListener ;

		public MyListViewAdapter() {
			imageLoader = new ImageLoader(ChattingActivity.this);
		}

		@Override
		public int getCount() {
			return messageList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final Message message = messageList.get(position);
			View inflate;
			MyHonder myHonder;
			myOnLongclickListener = new MyOnLongclickListener(message,position);
			myOnClickListener = new MyOnClickListener(message);

			//自己发的消息
			if (message.getTag()==0){
				//填充过布局了，用缓存的
				if (convertView !=null && convertView.getTag(R.id.right_tag)!=null){
					inflate = (View) convertView.getTag(R.id.right_tag);
					myHonder = (MyHonder) convertView.getTag(R.id.myhonder);
				}
				else {
					inflate = View.inflate(ChattingActivity.this, R.layout.chatting_item_listview_right, null);
					inflate.setTag(R.id.right_tag,inflate);

					myHonder = new MyHonder();
					myHonder.tv_chattitem_time = (TextView) inflate.findViewById(R.id.tv_chattitem_time);
					myHonder.tv_chattitem_message = (TextView) inflate.findViewById(R.id.tv_chattitem_message);
					myHonder.iv_chatt_voice = (ImageView) inflate.findViewById(R.id.iv_chatt_voice);
					myHonder.tv_chatt_duration = (TextView) inflate.findViewById(R.id.tv_chatt_duration);
					myHonder.iv_chatt_iamgeitem = (ImageView) inflate.findViewById(R.id.iv_chatt_iamgeitem);
					myHonder.iv_chattitem_icon = (ImageView) inflate.findViewById(R.id.iv_chattitem_icon);
					myHonder.tv_chattitem_message.setOnLongClickListener(myOnLongclickListener);
					myHonder.iv_chatt_voice.setOnLongClickListener(myOnLongclickListener);
					myHonder.iv_chatt_iamgeitem.setOnLongClickListener(myOnLongclickListener);

					myHonder.iv_chatt_iamgeitem.setOnClickListener(myOnClickListener);
					myHonder.iv_chatt_voice.setOnClickListener(myOnClickListener);

					myHonder.iv_chatt_iamgeitem.setOnTouchListener(new MyOnTouchListener(myHonder.iv_chatt_iamgeitem));

					inflate.setTag(R.id.myhonder,myHonder);
				}
				int height = (int) MyUtils.dp2px(ChattingActivity.this, 50);
				imageLoader.bindBitmap(MyApplication.sharedPreferences.getString("iconUrl",""),myHonder.iv_chattitem_icon,height,height);
			}

			//对方消息
			else {
				if (convertView !=null && convertView.getTag(R.id.left_tag)!=null){
					inflate = (View) convertView.getTag(R.id.left_tag);
					myHonder = (MyHonder) convertView.getTag(R.id.myhonder);
				}
				else {
					inflate = View.inflate(ChattingActivity.this, R.layout.chatting_item_listview_left, null);
					inflate.setTag(R.id.left_tag,inflate);

					myHonder = new MyHonder();
					myHonder.tv_chattitem_time = (TextView) inflate.findViewById(R.id.tv_chattitem_time);
					myHonder.tv_chattitem_message = (TextView) inflate.findViewById(R.id.tv_chattitem_message);
					myHonder.iv_chatt_voice = (ImageView) inflate.findViewById(R.id.iv_chatt_voice);
					myHonder.tv_chatt_duration = (TextView) inflate.findViewById(R.id.tv_chatt_duration);
					myHonder.iv_chatt_iamgeitem = (ImageView) inflate.findViewById(R.id.iv_chatt_iamgeitem);
					myHonder.iv_chattitem_icon = (ImageView) inflate.findViewById(R.id.iv_chattitem_icon);

					myHonder.tv_chattitem_message.setOnLongClickListener(myOnLongclickListener);
					myHonder.iv_chatt_voice.setOnLongClickListener(myOnLongclickListener);
					myHonder.iv_chatt_iamgeitem.setOnLongClickListener(myOnLongclickListener);


					myHonder.iv_chatt_iamgeitem.setOnClickListener(myOnClickListener);
					myHonder.iv_chatt_voice.setOnClickListener(myOnClickListener);

					myHonder.iv_chatt_iamgeitem.setOnTouchListener(new MyOnTouchListener(myHonder.iv_chatt_iamgeitem));

					inflate.setTag(R.id.myhonder,myHonder);
				}
				imageLoader.bindBitmap(toIconUrl,myHonder.iv_chattitem_icon);
			}

			myHonder.tv_chattitem_time.setText(message.getTime());
			//文本
			if (message.getType()==Constant.MESSAGETYPPE_TXT){
				myHonder.iv_chatt_iamgeitem.setVisibility(View.GONE);
				myHonder.iv_chatt_voice.setVisibility(View.GONE);
				myHonder.tv_chatt_duration.setVisibility(View.GONE);
				myHonder.tv_chattitem_message.setVisibility(View.VISIBLE);
				myHonder.tv_chattitem_message.setText(message.getMessage());
			}
			//语音
			else if (message.getType()==Constant.MESSAGETYPPE_VIOCE){
				myHonder.tv_chattitem_message.setVisibility(View.GONE);
				myHonder.iv_chatt_iamgeitem.setVisibility(View.GONE);
				myHonder.iv_chatt_voice.setVisibility(View.VISIBLE);
				myHonder.tv_chatt_duration.setVisibility(View.VISIBLE);
				myHonder.tv_chatt_duration.setText(message.getDuration()+"''");
			}
			//图片
			else if (message.getType()==Constant.MESSAGETYPPE_IMG){
				myHonder.tv_chattitem_message.setVisibility(View.GONE);
				myHonder.iv_chatt_voice.setVisibility(View.GONE);
				myHonder.tv_chatt_duration.setVisibility(View.GONE);
				myHonder.iv_chatt_iamgeitem.setVisibility(View.VISIBLE);

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				options.inJustDecodeBounds = false;
				options.inSampleSize= 6;

				String iconUrl = message.getIcon();
				File iconFile = new File(iconUrl);
				Log.i(TAG,"message.getIcon():"+iconUrl);

				if (mIsGridViewIdle){
					if (iconFile.exists()){
						Bitmap bitmap = BitmapFactory.decodeFile(message.getIcon(),options);
						myHonder.iv_chatt_iamgeitem.setImageBitmap(bitmap);
					}
					//去网络访问数据，主要是对方发过来的消息
					else {
						int mImageWidth = (int) MyUtils.dp2px(ChattingActivity.this,100);
						mImageLoader.bindBitmap(iconUrl, myHonder.iv_chatt_iamgeitem, mImageWidth, mImageWidth);
					}
				}
				
				  /* ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (bitmap!=null){
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                }
              *//*  //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                int quality = 100;
                while ( baos.toByteArray().length / 1024>100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                    baos.reset();//重置baos即清空baos
                    quality -= 10;//每次都减少10
                    if (quality<0){
                        break;
                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);//这里压缩options%，把压缩后的数据存放到baos中
                }*//*
				                ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
                bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片*/
			}

			return inflate;
		}

		class MyHonder {
			TextView tv_chattitem_time;
			TextView tv_chattitem_message;
			ImageView iv_chatt_voice;
			TextView tv_chatt_duration;
			ImageView iv_chatt_iamgeitem;
			ImageView iv_chattitem_icon;
		}
	}

	//消息监听器
	EMMessageListener msgListener = new EMMessageListener() {

		@Override
		public void onMessageReceived(List<EMMessage> messages) {
			Log.i(TAG,"onMessageReceived");
			//收到消息
			for (int i=0;i<messages.size();i++){
				final EMMessage emMessage = messages.get(i);
				addMessageListItem(emMessage,messageList.size());
			}

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					myListViewAdapter.notifyDataSetChanged();
					lv_chatt_message.setSelection(myListViewAdapter.getCount()-1);
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
		public void onMessageRecalled(List<EMMessage> list) {

		}

		@Override
		public void onMessageChanged(EMMessage message, Object change) {
			//消息状态变动
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		//返回上一个Activity，将未读消息列表设为空，需要在onPause()里面执行，因为onPause()执行后会执行上一个Activity的onResume()等方法
		EMConversation conversation = EMClient.getInstance().chatManager().getConversation(toPhone);
		if (conversation!=null){
			//指定会话消息未读数清零
			conversation.markAllMessagesAsRead();
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		EMClient.getInstance().chatManager().removeMessageListener(msgListener);

	}

	//消息长按事件
	class MyOnLongclickListener implements View.OnLongClickListener{
		Message message;
		int position;

		public MyOnLongclickListener(Message message, int position) {
			this.message = message;
			this.position = position;
		}

		@Override
		public boolean onLongClick(View v) {
			switch (v.getId()){
				//文字长按
				case R.id.tv_chattitem_message:
					View inflate1 = View.inflate(ChattingActivity.this, R.layout.alert_choose, null);
					TextView tv_alert_copy = (TextView) inflate1.findViewById(R.id.tv_alert_copy);
					TextView tv_alert_delete = (TextView) inflate1.findViewById(R.id.tv_alert_delete);
					tv_alert_copy.setOnClickListener(new MyItemOnClickListener(message,position));
					tv_alert_delete.setOnClickListener(new MyItemOnClickListener(message,position));

					AlertDialog.Builder  alertDialogBuilder =  new AlertDialog.Builder(ChattingActivity.this);
					alertDialog = alertDialogBuilder.setView(inflate1).show();
					break;

				//录音长按
				case R.id.iv_chatt_voice:
					View inflate2 = View.inflate(ChattingActivity.this, R.layout.alert_delete, null);
					TextView tv_alert_delete1 = (TextView) inflate2.findViewById(R.id.tv_alert_delete);
					tv_alert_delete1.setOnClickListener(new MyItemOnClickListener(message,position));
					AlertDialog.Builder  alertDialogBuilder1 =  new AlertDialog.Builder(ChattingActivity.this);
					alertDialog = alertDialogBuilder1.setView(inflate2).show();
					break;

				//图片长按
				case R.id.iv_chatt_iamgeitem:
					View inflate3 = View.inflate(ChattingActivity.this, R.layout.alert_delete, null);
					TextView tv_alert_delete3 = (TextView) inflate3.findViewById(R.id.tv_alert_delete);
					tv_alert_delete3.setOnClickListener(new MyItemOnClickListener(message,position));
					AlertDialog.Builder  alertDialogBuilder3 =  new AlertDialog.Builder(ChattingActivity.this);
					alertDialog = alertDialogBuilder3.setView(inflate3).show();
					break;
			}
			return false;
		}
	}

	class MyOnClickListener implements View.OnClickListener{
		Message message;

		public MyOnClickListener() {
		}

		public MyOnClickListener(Message message) {
			this.message = message;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()){
				//添加图标
				case R.id.iv_chatt_send:
					if (ll_chatt_addother.getVisibility()!=View.VISIBLE){
						ll_chatt_addother.setVisibility(View.VISIBLE);
					}
					else {
						ll_chatt_addother.setVisibility(View.GONE);
					}
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					boolean isOpen = imm.isActive();
					if (isOpen) {
						imm.hideSoftInputFromWindow(bt_chatt_send.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					}

					break;

				//相册选择图片
				case R.id.iv_chatt_choosepicture:
					Intent intent = new Intent();
					//匹配其过滤器
					intent.setAction("android.intent.action.PICK");
					intent.setType("image/*");
					startActivityForResult(intent,100);
					break;
				//相机拍照
				case R.id.iv_chatt_tackpicture:
					Intent tackIntent = new Intent();
					//匹配其过滤器
					tackIntent.setAction("android.media.action.IMAGE_CAPTURE");
					File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/cts");
					if (!file.exists()){
						boolean mkdirs = file.mkdirs();
						if (!mkdirs){
							break;
						}
					}
					///storage/emulated/0/cts/1469067312871.png
					currentImageSaveFile = new File(file,System.currentTimeMillis() + ".png");
					tackIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageSaveFile));
					startActivityForResult(tackIntent,101);
					break;

				//查看图片
				case R.id.iv_chatt_iamgeitem:
					Intent intent1 = new Intent(ChattingActivity.this,ChatImageActivity.class);
					intent1.putExtra("localIconUrl",message.getIcon());
					startActivity(intent1);
					break;

				//播放语音
				case R.id.iv_chatt_voice:
					String vioceLocalurl = message.getMessage();
					MediaPlayer mediaPlayer = new MediaPlayer();
					try {
						mediaPlayer.setDataSource(vioceLocalurl);
						mediaPlayer.prepare();
						mediaPlayer.start();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
			}
		}
	}


	//设置点击图片影音效果
	class MyOnTouchListener implements View.OnTouchListener{
		View view;

		public MyOnTouchListener(View view) {
			this.view = view;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:
					view.setAlpha((float) 0.5);
                            /*if (finalMyHonder.iv_chatt_iamgeitem!=null){
                                finalMyHonder.iv_chatt_iamgeitem.setColorFilter(Color.parseColor("#B98B7D7B"));
                            }*/
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
                            /*The current gesture has been aborted.You should treat this as
                              an up event, but not perform any action that you normally would.*/
					view.setAlpha((float) 1);
                           /* if (finalMyHonder.iv_chatt_iamgeitem!=null) {
                                finalMyHonder.iv_chatt_iamgeitem.clearColorFilter();
                            }*/
					break;
			}
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==RESULT_OK){
			String time = getCurrentFormatTime();
			String path ="";
			if (requestCode==100){
				Uri uri = data.getData();
				Cursor cursor = getContentResolver().query(uri, null, null, null,null);
				if (cursor != null && cursor.moveToFirst()) {
					path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)); //storage/emulated/0/360Browser/download/20151006063040806.jpg
					Log.i(TAG,"path:"+path);
				}
				if (cursor != null) {
					cursor.close();
				}
			}

			else if(requestCode==101){
				if (currentImageSaveFile!=null){
					path = currentImageSaveFile.getAbsolutePath();
				}
			}

			if (path!=null){
				sendImgMessage(path,toPhone);
				messageList.add(new Message(time,path,"",0,Constant.MESSAGETYPPE_IMG));
				myListViewAdapter.notifyDataSetChanged();
				lv_chatt_message.setSelection(myListViewAdapter.getCount()-1);
			}
		}
	}

	class MyItemOnClickListener implements View.OnClickListener{
		Message message;
		int position;
		public MyItemOnClickListener(Message message,int position) {
			this.message = message;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.tv_alert_copy:
					Log.i(TAG,"tv_alert_copy");
					copyText(message.getMessage());
					break;
				case R.id.tv_alert_delete:
					Log.i(TAG,"tv_alert_delete");
					deleteMessage(message,position);

					break;
			}
			alertDialog.dismiss();
		}
	}

	public String getCurrentFormatTime(){
		//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);  //07-12 15:10

		return format.format(new Date());
	}

	public String getSpecfFormatTime(long time){
		//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);  //07-12 15:10
		return format.format(new Date(time));
	}


	//复制文字
	private void copyText(String text) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(text);
	}

	//删除文字
	private void deleteMessage(Message message,int position) {
		messageList.remove(position);
		myListViewAdapter.notifyDataSetChanged();

		EMConversation conversation = EMClient.getInstance().chatManager().getConversation(toPhone);
		conversation.removeMessage(message.getMesId());
	}

	//返回按钮
	public void back(View view){
		finish();
	}

}
