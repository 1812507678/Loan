package zhiyuan.com.loan.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.bean.Apk;
import zhiyuan.com.loan.fragment.MessageFragment;
import zhiyuan.com.loan.util.ApkVersionUtil;


public class SettingActivity extends BaseActivity {

	private static final String TAG = "SettingActivity";
	private ProgressBar progressBar;
	private AlertDialog downProcess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		initView();

	}

	private void initView() {
		RelativeLayout ll_me_password = (RelativeLayout) findViewById(R.id.ll_me_password);
		RelativeLayout ll_me_suggest = (RelativeLayout) findViewById(R.id.ll_me_suggest);
		RelativeLayout ll_me_update = (RelativeLayout) findViewById(R.id.ll_me_update);
		RelativeLayout ll_me_aboutus = (RelativeLayout) findViewById(R.id.ll_me_aboutus);
		LinearLayout ll_me_exit = (LinearLayout) findViewById(R.id.ll_me_exit);

		MyOnClickListener myOnClickListener = new MyOnClickListener();

		ll_me_password.setOnClickListener(myOnClickListener);
		ll_me_suggest.setOnClickListener(myOnClickListener);
		ll_me_update.setOnClickListener(myOnClickListener);
		ll_me_aboutus.setOnClickListener(myOnClickListener);
		ll_me_exit.setOnClickListener(myOnClickListener);

		progressBar  = new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
		progressBar.setMinimumHeight(10);

		downProcess = new AlertDialog.Builder(SettingActivity.this)
				.setTitle("下载进度")
				.setView(progressBar)
				.create();
	}

	class MyOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.ll_me_password:
					startActivity(new Intent(SettingActivity.this,RetrievePasswordActivity.class));
					break;
				case R.id.ll_me_suggest:
					startActivity(new Intent(SettingActivity.this,ComplaintFeedbackActivity.class));
					break;
				case R.id.ll_me_update:
					checkAndUpdateVersion();
					break;
				case R.id.ll_me_aboutus:
					startActivity(new Intent(SettingActivity.this,AboutAsActivity.class));
					break;

				case R.id.ll_me_exit:
					startActivity(new Intent(SettingActivity.this,LoginActivity.class));
					List<Activity> activityList = MyApplication.activityList;
					for (int i=0;i<activityList.size();i++){
						activityList.get(i).finish();
					}
					MyApplication.sharedPreferences.edit().putString("phone","").apply();
					MyApplication.sharedPreferences.edit().putString("iconUrl","").apply();
					MessageFragment.exit();
					finish();
					break;
			}
		}
	}

	private void checkAndUpdateVersion() {
		int versionCode = MyApplication.sharedPreferences.getInt("versionCode", 0);
		ApkVersionUtil.updateVersion(versionCode,false,this);
	}

	//版本更新
	private void updateVersion(int versionCode, final boolean isForceInstall) {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			if (versionCode>packageInfo.versionCode){
				//有新版本
				final String apkUrl = MyApplication.sharedPreferences.getString("apkUrl", "");
				if (!apkUrl.equals("")){
					AlertDialog alertDialog = new AlertDialog.Builder(SettingActivity.this)

							.setTitle("有新版本")
							.setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									//弹出对话框，显示下载进度

									//设置在对话框之外的其他地方点击，对话框不会消失
									downProcess.setCanceledOnTouchOutside(false);
									downProcess.show();
									//进行下载和安装
									downloadAndInstallApp(apkUrl);
								}
							})
							.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (isForceInstall){
										android.os.Process.killProcess(android.os.Process.myPid()) ;   //获取PID
										System.exit(0);
									}
								}
							})
							.create();
					alertDialog.setCanceledOnTouchOutside(false);
					alertDialog.show();
				}
			}
			else {
				AlertDialog alertDialog = new AlertDialog.Builder(SettingActivity.this)
						.setTitle("已是最新版本")
						.setNegativeButton("确定", null)
						.create();
				alertDialog.setCanceledOnTouchOutside(false);
				alertDialog.show();
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void downloadAndInstallApp(String apkUrl) {
		HttpUtils httpUtils = new HttpUtils();
		final String path = getCacheDir()+"/58名校贷.apk";
		Log.i(TAG,"path:"+path);
		Log.i(TAG,"apkUrl:"+apkUrl);
		httpUtils.download(apkUrl, path, true,true,new RequestCallBack<File>() {
			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				Toast.makeText(SettingActivity.this,"下载完成",Toast.LENGTH_SHORT).show();
				installApp(path);
			}

			@Override
			public void onFailure(HttpException e, String s) {
				Toast.makeText(SettingActivity.this,"下载失败"+s,Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				Log.i(TAG,"onLoading==="+"total:"+total+",current:"+current);
				progressBar.setMax((int) total);
				progressBar.setProgress((int) current);
				if (progressBar.getProgress() == progressBar.getMax()) {
					downProcess.cancel();
				}
			}
		});

	}


	//安装app
	private void installApp(String path) {
		File file = new File(path);
		//启动系统中专门安装app的组件进行安装app
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivity(intent);
	}

	public void back(View view){
		finish();
	}
}
