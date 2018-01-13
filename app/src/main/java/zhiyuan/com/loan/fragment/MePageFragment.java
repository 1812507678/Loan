package zhiyuan.com.loan.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.sharesdk.onekeyshare.OnekeyShare;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.activity.AcceptPushActivity;
import zhiyuan.com.loan.activity.AccountnfoActivity;
import zhiyuan.com.loan.activity.CollectArticleActivity;
import zhiyuan.com.loan.activity.LoginActivity;
import zhiyuan.com.loan.activity.LookupApplyActivity;
import zhiyuan.com.loan.activity.SettingActivity;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.view.CircleImageView;

/**
 * Created by Administrator on 2016/7/7.
 */
public class MePageFragment extends Fragment {

    private View inflate;
    private String currentPhone;
    private String nickname;
    private TextView tv_me_nickname;
    private CircleImageView cv_me_icon;
    private String apkUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.frgament_page_me, new LinearLayout(getActivity()),false);
        initView();

        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
        String iconUrl = MyApplication.sharedPreferences.getString("iconUrl", "");
        if (!iconUrl.equals("")){
            BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
            bitmapUtils.display(cv_me_icon,iconUrl);
        }
    }

    private void initView() {
        cv_me_icon = (CircleImageView) inflate.findViewById(R.id.cv_me_icon);
        tv_me_nickname = (TextView) inflate.findViewById(R.id.tv_me_nickname);
        LinearLayout ll_me_collect = (LinearLayout) inflate.findViewById(R.id.ll_me_collect);
        LinearLayout ll_me_push = (LinearLayout) inflate.findViewById(R.id.ll_me_push);
        LinearLayout ll_me_personinf = (LinearLayout) inflate.findViewById(R.id.ll_me_personinf);
        LinearLayout ll_me_invite = (LinearLayout) inflate.findViewById(R.id.ll_me_invite);
        LinearLayout ll_me_applyhis = (LinearLayout) inflate.findViewById(R.id.ll_me_applyhis);
        LinearLayout ll_me_setting = (LinearLayout) inflate.findViewById(R.id.ll_me_setting);


        currentPhone = MyApplication.sharedPreferences.getString("phone", "");
        apkUrl = MyApplication.sharedPreferences.getString("apkUrl", "");

        if (!currentPhone.equals("")){
            nickname = MyApplication.sharedPreferences.getString("nickname", "");

            if (nickname.equals("")){
                tv_me_nickname.setText(currentPhone);
            }
            else {
                tv_me_nickname.setText(nickname);
            }


        }

        MyOnClickListener myOnClickListener = new MyOnClickListener();

        cv_me_icon.setOnClickListener(myOnClickListener);

        ll_me_collect.setOnClickListener(myOnClickListener);
        ll_me_push.setOnClickListener(myOnClickListener);
        ll_me_personinf.setOnClickListener(myOnClickListener);
        ll_me_invite.setOnClickListener(myOnClickListener);
        ll_me_applyhis.setOnClickListener(myOnClickListener);
        ll_me_setting.setOnClickListener(myOnClickListener);
		
		
       /* //查询apk地址
        BmobQuery<Apk> apkBmobQuery = new BmobQuery<>();
        apkBmobQuery.findObjects(getActivity(), new FindListener<Apk>() {
            @Override
            public void onSuccess(List<Apk> list) {
                if (list!=null){
                    apkUrl = list.get(0).getApkUrl();
                   // MyApplication.sharedPreferences.edit().putString()
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });*/

        saveFileToSD();

    }

    private void saveFileToSD() {
        String path = Environment.getExternalStorageDirectory() + "/logo.png";
        File file = new File(path);
        if (file.exists()){
            return;
        }
        Bitmap logoBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.logo);
        ByteArrayOutputStream logoStream = new ByteArrayOutputStream();
        boolean res = logoBitmap.compress(Bitmap.CompressFormat.PNG, 100, logoStream);
        //将图像读取到logoStream中
        byte[] logoBuf = logoStream.toByteArray();
        //将图像保存到byte[]中
        Bitmap temp = BitmapFactory.decodeByteArray(logoBuf, 0, logoBuf.length);
        //将图像从byte[]中读取生成Bitmap 对象 temp
        saveMyBitmap(path,temp);
    }


    //将图像保存到SD卡中
    public void saveMyBitmap(String path,Bitmap mBitmap){

        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ll_me_setting:
                    startActivity(new Intent(getActivity(),SettingActivity.class));
                    MyApplication.activityList.clear();
                    MyApplication.activityList.add(getActivity());
                    break;
                case R.id.cv_me_icon:

                    if (!currentPhone.equals("")){
                        startActivityForResult(new Intent(getActivity(),AccountnfoActivity.class),120);
                    }
                    else {
                        startActivity(new Intent(getActivity(),LoginActivity.class));
                    }
                    break;
                case R.id.ll_me_collect:
                    if (!currentPhone.equals("")){
                        startActivity(new Intent(getActivity(),CollectArticleActivity.class));
                    }
                    else {
                        startActivity(new Intent(getActivity(),LoginActivity.class));
                    }

                    break;
                case R.id.ll_me_push:
                    if (!currentPhone.equals("")){
                        startActivity(new Intent(getActivity(),AcceptPushActivity.class));
                    }
                    else {
                        startActivity(new Intent(getActivity(),LoginActivity.class));
                    }

                    break;
                case R.id.ll_me_personinf:
                    if (!currentPhone.equals("")){
                        startActivity(new Intent(getActivity(),AccountnfoActivity.class));
                    }
                    else {
                        startActivity(new Intent(getActivity(),LoginActivity.class));
                    }
                    break;
                case R.id.ll_me_invite:

                    share();
                    break;
                case R.id.ll_me_applyhis:
                    startActivity(new Intent(getActivity(), LookupApplyActivity.class));

                    break;
            }
        }
    }

    private void share() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("58名校贷，专业的大学生贷款软件");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(apkUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(apkUrl);

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        String path = Environment.getExternalStorageDirectory() + "/logo.png";
        oks.setImagePath(path);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(apkUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("很好用的软件");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(apkUrl);

        //隐藏掉不需要显示的平台
        oks.addHiddenPlatform("SinaWeibo");
        oks.addHiddenPlatform("ShortMessage");

        // 启动分享GUI
        oks.show(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== Activity.RESULT_OK && requestCode==120){
            String formNickname = data.getStringExtra("nickname");
            if (!formNickname.equals(nickname)){
                tv_me_nickname.setText(formNickname);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
