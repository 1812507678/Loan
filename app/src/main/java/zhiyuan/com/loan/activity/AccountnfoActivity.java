package zhiyuan.com.loan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.bean.User;
import zhiyuan.com.loan.view.CircleImageView;

public class AccountnfoActivity extends BaseActivity {

	private static final String TAG = "AccountnfoActivity";
	private TextView tv_personcenter_nickname;
	private TextView tv_personcenter_qq;
	private TextView tv_personcenter_email;
	private CircleImageView iv_personcenter_iocn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accountnfo);

		initView();
	}

	private void initView() {
		RelativeLayout rl_personcenter_persondata = (RelativeLayout) findViewById(R.id.rl_personcenter_persondata);
		tv_personcenter_nickname = (TextView) findViewById(R.id.tv_personcenter_nickname);
		tv_personcenter_qq = (TextView) findViewById(R.id.tv_personcenter_qq);
		tv_personcenter_email = (TextView) findViewById(R.id.tv_personcenter_email);
		iv_personcenter_iocn = (CircleImageView) findViewById(R.id.iv_personcenter_iocn);
		TextView tv_personcenter_phone = (TextView) findViewById(R.id.tv_personcenter_phone);

		RelativeLayout rl_personcenter_nickname = (RelativeLayout) findViewById(R.id.rl_personcenter_nickname);
		RelativeLayout rl_personcenter_qq = (RelativeLayout) findViewById(R.id.rl_personcenter_qq);
		RelativeLayout rl_personcenter_email = (RelativeLayout) findViewById(R.id.rl_personcenter_email);

		MyOnClicklListener myOnClicklListener =new MyOnClicklListener();
		rl_personcenter_persondata.setOnClickListener(myOnClicklListener);
		rl_personcenter_nickname.setOnClickListener(myOnClicklListener);
		rl_personcenter_qq.setOnClickListener(myOnClicklListener);
		rl_personcenter_email.setOnClickListener(myOnClicklListener);

		tv_personcenter_nickname.setText(MyApplication.sharedPreferences.getString("nickname",""));
		tv_personcenter_qq.setText(MyApplication.sharedPreferences.getString("qq",""));
		tv_personcenter_email.setText(MyApplication.sharedPreferences.getString("email",""));
		tv_personcenter_phone.setText(MyApplication.sharedPreferences.getString("phone",""));
		BitmapUtils bitmapUtils = new BitmapUtils(this);

		String iconUrl = MyApplication.sharedPreferences.getString("iconUrl", "");
		if (!iconUrl.equals("")){
			bitmapUtils.display(iv_personcenter_iocn, iconUrl);
		}
	}

	class MyOnClicklListener implements View.OnClickListener{
		Intent intent = new Intent(AccountnfoActivity.this, ModifyAccountItemActivity.class);
		@Override
		public void onClick(View view) {
			switch (view.getId()){
				//选择头像
				case R.id.rl_personcenter_persondata:
					Intent pickiIntent = new Intent();
					//匹配其过滤器
					pickiIntent.setAction("android.intent.action.PICK");
					pickiIntent.setType("image/*");
					startActivityForResult(pickiIntent,113);
					break;
				case R.id.rl_personcenter_nickname:
					intent.putExtra("type",1);
					startActivityForResult(intent,110);
					break;

				case R.id.rl_personcenter_qq:
					intent.putExtra("type",2);
					startActivityForResult(intent,111);
					break;
				case R.id.rl_personcenter_email:
					intent.putExtra("type",3);
					startActivityForResult(intent,112);
					break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode==RESULT_OK){
			String result = data.getStringExtra("result");
			if (requestCode==110){
				tv_personcenter_nickname.setText(result);
				Intent intent = getIntent();
				intent.putExtra("nickname",result);
				setResult(RESULT_OK,intent);
			}
			else if (requestCode==111){
				tv_personcenter_qq.setText(result);
			}
			else if (requestCode==112){
				tv_personcenter_email.setText(result);
			}

			else if (requestCode==113){
				Uri uri = data.getData();
				Cursor cursor = getContentResolver().query(uri, null, null, null,null);
				if (cursor != null && cursor.moveToFirst()) {
					String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)); //storage/emulated/0/360Browser/download/20151006063040806.jpg
					Log.i(TAG, "path:" + path);
					iv_personcenter_iocn.setImageBitmap(BitmapFactory.decodeFile(path));

					final BmobFile icon = new BmobFile(new File(path));
					icon.upload(new UploadFileListener() {
						@Override
						public void done(BmobException e) {
							if (e==null){
								User user = new User();
								String objectId = MyApplication.sharedPreferences.getString("objectId", "");
								user.setObjectId(objectId);
								user.setIconUrl(icon.getUrl());
								if (!objectId.equals("")){
									user.update(new UpdateListener() {
										@Override
										public void done(BmobException e) {
											if (e==null){
												Toast.makeText(AccountnfoActivity.this,"头像上传成功",Toast.LENGTH_SHORT).show();
												SharedPreferences.Editor edit = MyApplication.sharedPreferences.edit();
												edit.putString("iconUrl",icon.getUrl());
												edit.apply();
											}else {
												Toast.makeText(AccountnfoActivity.this,"头像上传失败"+e,Toast.LENGTH_SHORT).show();
											}
										}
									});
								}
							}else {
								Log.i(TAG,"上传失败error:   "+e);
							}
						}
					});

					cursor.close();

				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void back(View view){
		finish();
	}


}

		