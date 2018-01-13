package zhiyuan.com.loan.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

import zhiyuan.com.loan.R;
import zhiyuan.com.loan.bean.Apk;


/**
 * Created by HP on 2017/4/5.
 */

//自定义AlertDialog选择框

public class ChooseAlertDialogUtil {
    private static final String TAG = ChooseAlertDialogUtil.class.getSimpleName();
    Context context;

    public ChooseAlertDialogUtil(Context context) {
        this.context = context;
    }

    public void setAlertDialogText(){
        View inflate = View.inflate(context, R.layout.confirm_dialog, null);
        ImageView iv_dialog_close = inflate.findViewById(R.id.iv_dialog_close);
        final ImageView iv_dialog_qrcncan = inflate.findViewById(R.id.iv_dialog_qrcncan);
        TextView bt_choose_copy = inflate.findViewById(R.id.bt_choose_copy);
        final TextView tv_dialog_weixin = inflate.findViewById(R.id.tv_dialog_weixin);
        TextView bt_choose_saveimage = inflate.findViewById(R.id.bt_choose_saveimage);

        final BitmapUtils bitmapUtils = new BitmapUtils(context);
        final Apk apkFromSP = MyUtils.getApkFromSP();
        if (apkFromSP!=null){
            if (!TextUtils.isEmpty(apkFromSP.getQrCode())){
                bitmapUtils.display(iv_dialog_qrcncan,apkFromSP.getQrCode());
            }
            if (!TextUtils.isEmpty(apkFromSP.getWeixinGZHContactInfo())){
                tv_dialog_weixin.setText(apkFromSP.getWeixinGZHContactInfo());
            }
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.myCorDialog).setView(inflate).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        Window dialogWindow = alertDialog.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(0));//设置window背景
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();//获取屏幕尺寸
        lp.width = (int) (d.widthPixels * 0.8); //宽度为屏幕80%
        lp.gravity = Gravity.CENTER;  //中央居中
        dialogWindow.setAttributes(lp);


        /*final AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.myCorDialog).setView(inflate).create();

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        float width = context.getResources().getDimension(R.dimen.x800);
        float height = context.getResources().getDimension(R.dimen.x500);


        alertDialog.getWindow().setLayout(new Float(width).intValue(),new Float(height).intValue());*/


        iv_dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        bt_choose_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                MyUtils.showToask(context,"复制公众号成功");
                MyUtils.copyText(tv_dialog_weixin.getText().toString(),context);
            }
        });

        bt_choose_saveimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                MyUtils.showToask(context,"二维码图片保存成功，可在相册查看");
                try {
                    if (apkFromSP!=null && !TextUtils.isEmpty(apkFromSP.getQrCode())){
                        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmapUtils.getBitmapFileFromDiskCache(apkFromSP.getQrCode()).getAbsolutePath(), "title", "description");
                    }
                    else {
                        iv_dialog_qrcncan.setDrawingCacheEnabled(true);
                        Bitmap bitmap = iv_dialog_qrcncan.getDrawingCache();
                        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "这是title", "这是description");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG,"e:"+e);
                }
            }
        });

    }


    public void setAlertDialogTextContact(){
        View inflate = View.inflate(context, R.layout.copy_contactinfo_dialog, null);
        ImageView iv_dialog_close = inflate.findViewById(R.id.iv_dialog_close);

        final TextView tv_dialog_qq = inflate.findViewById(R.id.tv_dialog_qq);
        TextView bt_choose_copyqq = inflate.findViewById(R.id.bt_choose_copyqq);
        final TextView tv_dialog_weixin = inflate.findViewById(R.id.tv_dialog_weixin);
        TextView bt_choose_copyweixin = inflate.findViewById(R.id.bt_choose_copyweixin);
        final TextView tv_dialog_gongzhonghao = inflate.findViewById(R.id.tv_dialog_gongzhonghao);
        TextView bt_choose_copygongzhonghao = inflate.findViewById(R.id.bt_choose_copygongzhonghao);

        final BitmapUtils bitmapUtils = new BitmapUtils(context);
        final Apk apkFromSP = MyUtils.getApkFromSP();
        if (apkFromSP!=null){
            if (!TextUtils.isEmpty(apkFromSP.getQqContactInfo())){
                 tv_dialog_qq.setText("qq号："+apkFromSP.getQqContactInfo());
            }
            if (!TextUtils.isEmpty(apkFromSP.getWeixinContactInfo())){
                tv_dialog_weixin.setText("微信号："+apkFromSP.getWeixinContactInfo());
            }
            if (!TextUtils.isEmpty(apkFromSP.getWeixinGZHContactInfo())){
                tv_dialog_gongzhonghao.setText("公众号："+apkFromSP.getWeixinGZHContactInfo());
            }
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.myCorDialog).setView(inflate).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        Window dialogWindow = alertDialog.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(0));//设置window背景
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();//获取屏幕尺寸
        lp.width = (int) (d.widthPixels * 0.85); //宽度为屏幕80%
        lp.gravity = Gravity.CENTER;  //中央居中
        dialogWindow.setAttributes(lp);

        iv_dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        bt_choose_copyqq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                MyUtils.showToask(context,"复制qq号成功");
                MyUtils.copyText(tv_dialog_qq.getText().toString().split("：")[1],context);
            }
        });

        bt_choose_copyweixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                MyUtils.showToask(context,"复制微信号号成功");
                MyUtils.copyText(tv_dialog_weixin.getText().toString().split("：")[1],context);
            }
        });

        bt_choose_copygongzhonghao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                MyUtils.showToask(context,"复制公众号成功");
                MyUtils.copyText(tv_dialog_gongzhonghao.getText().toString().split("：")[1],context);
            }
        });

    }


}
