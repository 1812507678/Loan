package zhiyuan.com.loan.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

import zhiyuan.com.loan.activity.SettingActivity;
import zhiyuan.com.loan.application.MyApplication;

/**
 * Created by haijun on 2016/9/28.
 */

public class ApkVersionUtil {
    private static final String TAG = "ApkVersionUtil";
    private static ProgressBar progressBar;
    private static AlertDialog downProcess;


    //版本更新
    public static void updateVersion(int versionCode, final boolean isForceInstall, final Context context) {
        progressBar  = new ProgressBar(context,null,android.R.attr.progressBarStyleHorizontal);
        progressBar.setMinimumHeight(10);

        downProcess = new AlertDialog.Builder(context)
                .setTitle("下载进度")
                .setView(progressBar)
                .create();

        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            if (versionCode>packageInfo.versionCode){
                //有新版本
                final String apkUrl = MyApplication.sharedPreferences.getString("apkUrl", "");
                if (!apkUrl.equals("")){
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("有新版本")
                            .setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //弹出对话框，显示下载进度


                                    //设置在对话框之外的其他地方点击，对话框不会消失
                                    downProcess.setCanceledOnTouchOutside(false);
                                    downProcess.show();
                                    //进行下载和安装
                                    downloadAndInstallApp(apkUrl,context);
                                }
                            })
                            .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (isForceInstall){
                                        Activity activity = (Activity) context;
                                        activity.finish();
                                    }
                                }
                            })
                            .create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void downloadAndInstallApp(String apkUrl, final Context context) {
        HttpUtils httpUtils = new HttpUtils();
        final String path = context.getCacheDir()+"/58名校贷.apk";
        Log.i(TAG,"path:"+path);
        Log.i(TAG,"apkUrl:"+apkUrl);
        httpUtils.download(apkUrl, path, false,false,new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                Toast.makeText(context,"下载完成",Toast.LENGTH_SHORT).show();
                installApp(path,context);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(context,"下载失败"+s,Toast.LENGTH_SHORT).show();
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
    private static void installApp(String path, Context context) {
        File file = new File(path);
        //启动系统中专门安装app的组件进行安装app
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
