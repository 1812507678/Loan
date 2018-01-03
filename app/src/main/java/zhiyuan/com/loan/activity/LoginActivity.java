package zhiyuan.com.loan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.bean.User;
import zhiyuan.com.loan.util.Constant;
import zhiyuan.com.loan.util.MyUtils;
import zhiyuan.com.loan.util.SharedPreferencesUtil;


public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private EditText et_login_account;
    private EditText et_login_pwd;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        et_login_account = (EditText) findViewById(R.id.et_login_account);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
    }

    public void login(View view){
        String account = et_login_account.getText().toString();
        String password = et_login_pwd.getText().toString();

        //输入信息验证
        if (account.isEmpty()||password.isEmpty()){
            Toast.makeText(this,"请输入用户名或密码",Toast.LENGTH_SHORT).show();
            return;
        }
        //登陆验证
        validate(account,password);
    }

    public void forgetPassword(View view){
        //跳到找回密码页面
        startActivity(new Intent(this,RetrievePasswordActivity.class));
    }

    public void register(View view){
        //跳到注册页面，注册成功，则跳到主页面
        startActivityForResult(new Intent(this,RegisterActivity.class),120);
    }

    //验证是否时注册用户
    private void validate(final String phone, final String password) {
        showDialog("正在登陆...");
        BmobQuery bmobQuery = new BmobQuery();
        bmobQuery.addWhereEqualTo("phone",phone);

        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e==null){
                    hideDialog();
                    //查询到对应的信息，则注册过，跳到主页面
                    if (list.size()==0){
                        //没有查询到对应的信息，则没注册过，提示该用户未注册
                        Toast.makeText(LoginActivity.this,"还未注册，请先进行注册",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        User user = list.get(0);
                        if (!user.getPassword().equals(password)){
                            Toast.makeText(LoginActivity.this,"密码不正确",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        login(phone,password,user);
                    }
                }else {
                    hideDialog();
                    Toast.makeText(LoginActivity.this,"登陆失败"+e,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //环信账号登陆
    public void login(final String phone, String password, final User user){
        EMClient.getInstance().login(phone,password,new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "登录聊天服务器成功！");
                SharedPreferencesUtil.putBooleanValueFromSP(Constant.isChatLogined,true);

                MobclickAgent.onProfileSignIn(phone); //友盟统计
                saveToApplication(user);
                //Toast.makeText(LoginActivity.this,"欢迎:"+user.getNickname(),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Toast.makeText(LoginActivity.this,"登陆失败"+message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void saveToApplication(User user) {
        //将登陆用户信息保存在MyApplication类的sharedPreferences
        SharedPreferences.Editor edit = MyApplication.sharedPreferences.edit();
        edit.putString("nickname",user.getNickname());
        edit.putString("password",user.getPassword());
        edit.putString("phone",user.getPhone());
        edit.putString("qq",user.getQq());
        edit.putString("email",user.getEmail());
        edit.putString("userId",user.getUserId());
        edit.putString("iconUrl",user.getIconUrl());
        edit.putString("objectId",user.getObjectId());
        edit.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //注册成功，页面销毁
        if (resultCode==RESULT_OK && requestCode==120){
            if (data.getBooleanExtra("registOK",false)) {
                finish();
            }
        }
    }

    void showDialog(String message) {
        try {
            if (dialog == null) {
                dialog = new ProgressDialog(this);
                dialog.setCancelable(true);
            }
            dialog.setMessage(message);
            dialog.show();
        } catch (Exception e) {
            // 在其他线程调用dialog会报错
        }
    }

    void hideDialog() {
        if (dialog != null && dialog.isShowing())
            try {
                dialog.dismiss();
            } catch (Exception e) {
            }
    }


}

