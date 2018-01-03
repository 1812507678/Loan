package zhiyuan.com.loan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.bean.User;
import zhiyuan.com.loan.util.Constant;
import zhiyuan.com.loan.util.SharedPreferencesUtil;


public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";
    private ProgressDialog dialog;
    private EditText et_register_phone;
    private EditText et_register_vercode;
    private EditText et_register_password;
    private EditText et_register_repassword;
    private EditText et_register_qq;
    private EditText et_register_email;
    private String verifycode;
    private Button bu_register_getvercode;
    private String qq;
    private String email;
    private String password;
    private String phone;
    private int MSG_UPDATE= 1;
    private int MSG_TOAST_FAIL= 3;
    private int MSG_TOAST_SUCCESS= 4;
    private int timeUpdate= 60;
    private Timer mTimer;
    private TimerTask mTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        initData();
    }

    private void initView() {
        et_register_phone = (EditText) findViewById(R.id.et_register_phone);
        et_register_vercode = (EditText) findViewById(R.id.et_register_vercode);
        et_register_password = (EditText) findViewById(R.id.et_register_password);
        et_register_repassword = (EditText) findViewById(R.id.et_register_repassword);
        et_register_qq = (EditText) findViewById(R.id.et_register_qq);
        et_register_email = (EditText) findViewById(R.id.et_register_email);
        bu_register_getvercode = (Button) findViewById(R.id.bu_register_getvercode);
    }


    private void initData() {
        EventHandler eh=new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {
                Log.i(TAG,"afterEvent====="+"event:"+event+",result:"+result+",data:"+data.toString());

                if (result==SMSSDK.RESULT_COMPLETE){
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        myHandler.sendEmptyMessage(MSG_TOAST_SUCCESS);
                    }
                    else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        myHandler.sendEmptyMessage(0);
                    }
                    else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                    }
                    else{
                        ((Throwable)data).printStackTrace();
                    }
                }
                else {
                    //event:2,result:0,data:java.lang.Throwable: {"status":603,"detail":"请填写正确的手机号码"}
                    String resultData = data.toString();
                    String josnData = resultData.split(":")[1]+":"+resultData.split(":")[2]+":"+resultData.split(":")[3];
                    Log.i(TAG,"josnData:"+josnData);
                    String detail ="";
                    int status = 0;
                    try {
                        JSONObject jsonObject = new JSONObject(josnData);
                        status = jsonObject.getInt("status");
                        detail = jsonObject.getString("detail");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG,"status:"+status+",detail:"+detail);

                    Message message = myHandler.obtainMessage(MSG_TOAST_FAIL);
                    message.obj = detail;
                    myHandler.sendMessage(message);
                }
            }


        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
        //定时器
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                Message message = myHandler.obtainMessage(MSG_UPDATE);
                message.obj = timeUpdate;
                myHandler.sendMessage(message);
                if(timeUpdate==0){
                    return;
                }
                timeUpdate--;
            }
        };
    }

    Handler myHandler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what==0){
                User user=new User();
                user.setPhone(phone);
                user.setPassword(password);
                user.setQq(qq);
                user.setEmail(email);
                user.setUserId("aa"+System.currentTimeMillis());
                user.setPlatform("58名校贷");
                insertToDB(user);
            }
            else if (msg.what==MSG_UPDATE){
                int time = (int) msg.obj;
                if (time==0){
                    bu_register_getvercode.setText("获取验证码");
                    bu_register_getvercode.setClickable(true);
                    bu_register_getvercode.setTextSize(10);
                    bu_register_getvercode.setBackgroundResource(R.drawable.bg_button);
                }

                else {
                    bu_register_getvercode.setText(time+"");
                }
            }
            else if (msg.what==MSG_TOAST_FAIL){
                String detail = (String) msg.obj;
                Toast.makeText(RegisterActivity.this,detail,Toast.LENGTH_SHORT).show();
                hideDialog();
            }
            else if (msg.what==MSG_TOAST_SUCCESS){
                bu_register_getvercode.setClickable(false);
                bu_register_getvercode.setBackgroundResource(R.drawable.bg_button_disable);
                bu_register_getvercode.setTextSize(15);
                bu_register_getvercode.setText(60+"");
                hideDialog();
                Toast.makeText(RegisterActivity.this,"验证码发送成功",Toast.LENGTH_SHORT).show();

                mTimer = new Timer();
                mTimer.schedule(mTimerTask, 1000, 1000);
            }
        }
    };

    //请求短信验证码

    public void getVercode(View view){
        String phone = et_register_phone.getText().toString();
        if(!TextUtils.isEmpty(phone)){
            /*SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String sendTime = format.format(new Date());*/

           /* //产生6位验证码
            int round = (int) Math.round(Math.random() * (9999 - 1000) + 1000);
            verifycode = String.valueOf(round);
            Log.i(TAG,"验证码:"+ verifycode +"");  //"您的验证码为"+ verifycode +"，请及时验证！"*/

            getMESCode("86",phone);  //86为国家代码
            showDialog("正在获取");
        }else{
            Toast.makeText(RegisterActivity.this,"输入手机号",Toast.LENGTH_SHORT).show();
            bu_register_getvercode.setClickable(true);
        }
    }

    public void registerConfirm(View view){

        phone = et_register_phone.getText().toString();
        String inputVerifycode = et_register_vercode.getText().toString();
        password = et_register_password.getText().toString();
        String confirmpassword = et_register_repassword.getText().toString();
        qq = et_register_qq.getText().toString();
        email = et_register_email.getText().toString();

        if (phone.isEmpty()){
            Toast.makeText(this,"请输入手机号",Toast.LENGTH_SHORT).show();
        }
        else if(inputVerifycode.isEmpty()){
            Toast.makeText(this,"请输入验证码",Toast.LENGTH_SHORT).show();
        }
        else if(password.isEmpty()){
            Toast.makeText(this,"请输入密码",Toast.LENGTH_SHORT).show();
        }

        else if(!password.isEmpty() && !confirmpassword.isEmpty() && !password.equals(confirmpassword)){
            Toast.makeText(this,"密码不一致",Toast.LENGTH_SHORT).show();
        }
        else if(qq.isEmpty()){
            Toast.makeText(this,"请输入qq",Toast.LENGTH_SHORT).show();
        }
        else if(email.isEmpty()){
            Toast.makeText(this,"请输入邮箱",Toast.LENGTH_SHORT).show();
        }
        else {
            //先将短信提交到shareSDK进行短信验证
            SMSSDK.submitVerificationCode("86", phone, inputVerifycode);
            showDialog("正在校验");
            Log.i(TAG,"submitVerificationCode");
        }

    }

    //将数据插入到数据库
    private void insertToDB(final User user) {
        showDialog("正在注册....");
        user.save(new SaveListener() {
            @Override
            public void done(Object o, BmobException e) {
                if (e==null){
                    registerHuanxin(user);
                }else {
                    Log.i(TAG,"onFailure:"+e);
                    //onFailure:401,unique index cannot has duplicate value: 1868946319
                    if (e.getErrorCode()==401){
                        Toast.makeText(RegisterActivity.this,"该号码已经注册过，请登陆！" + e,Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"注册失败：" + e,Toast.LENGTH_SHORT).show();
                    }
                    hideDialog();
                }
            }
        });
    }


    //注册环信聊天账号
    public void registerHuanxin(final User user){
        showDialog("数据一致性检查");
        new Thread(){
            @Override
            public void run() {
                //注册失败会抛出HyphenateException
                try {
                    EMClient.getInstance().createAccount(user.getPhone(), user.getPassword());//同步方法
                    Log.i(TAG,"注册成功");
                    login(user.getPhone(), user.getPassword(),user);
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    Log.i(TAG,"注册失败:"+e.toString());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            hideDialog();

                            int errorCode=e.getErrorCode();
                            if(errorCode== EMError.NETWORK_ERROR){
                                Toast.makeText(RegisterActivity.this, "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_ALREADY_EXIST){
                                Toast.makeText(RegisterActivity.this, "用户已存在！", Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_AUTHENTICATION_FAILED){
                                Toast.makeText(RegisterActivity.this, "注册失败，无权限！", Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_ILLEGAL_ARGUMENT){
                                Toast.makeText(RegisterActivity.this, "用户名不合法", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch (Exception e){}
            }
        }.start();
    }

    //环信账号登陆
    public void login(final String phone, String password, final User user){
        EMClient.getInstance().login(phone,password,new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "登录聊天服务器成功！");
                SharedPreferencesUtil.putBooleanValueFromSP(Constant.isChatLogined,true);
                runOnUiThread(new Runnable() {
                    public void run() {
                        hideDialog();
                        Toast.makeText(RegisterActivity.this, "注册成功：" + user.getObjectId(), Toast.LENGTH_SHORT).show();
                    }
                });


                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                //将登陆用户信息保存在ApplicationInfo类的user对象里面
                LoginActivity.saveToApplication(user);
                Intent intent = getIntent();
                intent.putExtra("registOK",true);
                setResult(RESULT_OK,intent);
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Toast.makeText(RegisterActivity.this,"注册成功"+message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getMESCode(String country, final String phone) {
        SMSSDK.getVerificationCode(country, phone);

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

    public void back(View view){
        finish();
    }
}
