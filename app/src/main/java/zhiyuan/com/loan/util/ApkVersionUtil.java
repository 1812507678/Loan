package zhiyuan.com.loan.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.bean.Apk;
import zhiyuan.com.loan.bean.MessageEvent;
import zhiyuan.com.loan.bean.StrategyArticle;

/**
 * Created by haijun on 2016/9/28.
 */

public class ApkVersionUtil {
    private static final String TAG = "ApkVersionUtil";
    private static ProgressBar progressBar;
    private static AlertDialog downProcess;


    //版本更新
    public static void updateVersion(int versionCode, final boolean isForceInstall, final Context context,boolean isNeedShowToast) {
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
                            .setMessage("发现新的app版本，更加稳定，要更新吗？")
                            .setPositiveButton("更新", new DialogInterface.OnClickListener() {
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
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (isForceInstall){
                                        Activity activity = (Activity) context;
                                        activity.finish();
                                    }
                                }
                            })
                            .create();
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.show();
                }
                else {
                    if (isNeedShowToast){
                        MyUtils.showToask(context,"apk新版的连接地址错误");
                    }
                }
            }
            else {
                if (isNeedShowToast){
                    MyUtils.showToask(context,"当前已是最新版本");
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


    public static void initApkData(final MyApplication myApplication) {
        BmobQuery<Apk> apkBmobQuery = new BmobQuery<>();

        apkBmobQuery.findObjects(new FindListener<Apk>() {
            @Override
            public void done(List<Apk> list, BmobException e) {
                Log.i(TAG,"apk list:"+list+",  e:"+e);
                if (e==null){
                    if (list!=null && list.size()>0){
                        Apk apk = list.get(0);
                        int versionCode = Integer.parseInt(apk.getVersionCode());
                        String apkUrl = apk.getApkUrl();
                        boolean forceWhenUpdate = apk.isForceWhenUpdate();
                        boolean updateWhenOpen = apk.isUpdateWhenOpen();
                        String qqContactInfo = apk.getQqContactInfo();
                        String contactPhone = apk.getContactPhone();

                        Log.i(TAG,"versionCode:"+versionCode+",apkUrl:"+apkUrl+",forceWhenUpdate:"+forceWhenUpdate+",updateWhenOpen:"+updateWhenOpen);

                        SharedPreferences.Editor edit = MyApplication.sharedPreferences.edit();
                        edit.putInt("versionCode",versionCode).apply();
                        edit.putString("apkUrl",apkUrl).apply();
                        edit.putString("qqContactInfo",qqContactInfo).apply();
                        edit.putString("contactPhone",contactPhone).apply();
                        edit.putBoolean("forceWhenUpdate",forceWhenUpdate).apply();
                        edit.putBoolean("updateWhenOpen",updateWhenOpen).apply();
                        edit.putString("weixinContactInfo",apk.getWeixinContactInfo()).apply();

                        MyUtils.putApkToSP(apk);
                        myApplication.setCurApkInfoDownloadFinshed(Constant.curdownloadType_success);

                        EventbusProxy.getInstance().postDataOnBus(new MessageEvent(EventbusProxy.MessageEventType.msgType_RequestNetwork_APK,EventbusProxy.success,"加载成功",apk));
                    }
                }else {
                    EventbusProxy.getInstance().postDataOnBus(new MessageEvent(EventbusProxy.MessageEventType.msgType_RequestNetwork_APK,EventbusProxy.fail,"加载失败",null));
                    myApplication.setCurApkInfoDownloadFinshed(Constant.curdownloadType_failure);
                    MyUtils.showToask(myApplication,"网络异常，加载失败");
                }
            }
        });

    }

    //加载初始化数据
    public static void loadArticleListtData(final MyApplication myApplication) {
        BmobQuery<StrategyArticle> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(8);
        bmobQuery.order("readCount");
        bmobQuery.findObjects(new FindListener<StrategyArticle>() {
            @Override
            public void done(List<StrategyArticle> list, BmobException e) {
                Log.i(TAG,"文章 list:"+list+",  e:"+e);
                if (e==null){
                    EventbusProxy.getInstance().postDataOnBus(new MessageEvent(EventbusProxy.MessageEventType.msgType_RequestNetwork_ArticleList,EventbusProxy.success,"加载成功",list));
                    myApplication.setCurArticleListDownloadFinshed(Constant.curdownloadType_success);
                    myApplication.setArticleListListContent(list);
                }
                else {
                    EventbusProxy.getInstance().postDataOnBus(new MessageEvent(EventbusProxy.MessageEventType.msgType_RequestNetwork_ArticleList,EventbusProxy.fail,"加载失败",null));
                    myApplication.setCurArticleListDownloadFinshed(Constant.curdownloadType_failure);
                }
            }

        });
    }
}
