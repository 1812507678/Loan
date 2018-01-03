package zhiyuan.com.loan.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.bean.User;
import zhiyuan.com.loan.util.MyUtils;

public class RetrievePasswordActivity extends BaseActivity {

    private static final String TAG = "RetrievePassword";
    private EditText tv_retrive_phone;
    private TextView et_retrive_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);

        initView();
    }

    private void initView() {
        tv_retrive_phone = (EditText) findViewById(R.id.tv_retrive_phone);
        et_retrive_password = (TextView) findViewById(R.id.et_retrive_password);
    }

    public void getPassword(View view){
        String phone = tv_retrive_phone.getText().toString();
        if (phone.isEmpty()){
            Toast.makeText(this,"请输入手机号",Toast.LENGTH_SHORT).show();
        }
        else {
            User user=new User();
            user.setPhone(phone);
            retrievePassword(user);
        }
    }

    private void retrievePassword(final User user) {

        MyUtils.showDialog("正在查找",this);
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("phone",user.getPhone());
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e==null){
                    MyUtils.hideDialog();
                    et_retrive_password.setVisibility(View.VISIBLE);
                    if (list.size()>0){
                        User userInfo = list.get(0);
                        Log.i(TAG,"userInfo:"+userInfo.toString());
                        et_retrive_password.setText(userInfo.getPassword());
                    }

                    else {
                        et_retrive_password.setText("用户不存在或输入号码错误");
                    }
                }else {
                    MyUtils.hideDialog();
                }
            }
        });
    }

    public void back(View view){
        finish();
    }
}