package zhiyuan.com.loan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.bean.Adviser;
import zhiyuan.com.loan.bean.User;

public class ModifyAccountItemActivity extends BaseActivity {

    private EditText ed_modifyuser_vlaue;
    private int type;
    private String initValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_account_item);

        initView();
    }


    private void initView() {
        TextView tv_modify_top = (TextView) findViewById(R.id.tv_modify_top);
        ed_modifyuser_vlaue = (EditText) findViewById(R.id.ed_modifyuser_vlaue);

        Intent intent = getIntent();
        type = intent.getIntExtra("type",0);
        if (type==1){
            tv_modify_top.setText("修改昵称");
            ed_modifyuser_vlaue.setText(MyApplication.sharedPreferences.getString("nickname",""));
        }
        else if (type==2){
            tv_modify_top.setText("修改qq");
            ed_modifyuser_vlaue.setText(MyApplication.sharedPreferences.getString("qq",""));
        }
        else if (type==3){
            tv_modify_top.setText("修改email");
            ed_modifyuser_vlaue.setText(MyApplication.sharedPreferences.getString("email",""));
        }

        initValue = ed_modifyuser_vlaue.getText().toString();

    }

    public void modify(View view){
        final String value = ed_modifyuser_vlaue.getText().toString();
        if (initValue.equals(value)) {
            finish();
            return;
        }

        String objectId = MyApplication.sharedPreferences.getString("objectId", "");
        boolean isAdmin = MyApplication.sharedPreferences.getBoolean("isAdmin", false);

        BmobObject bmobObject;

        if (isAdmin){
            Adviser adviser = new Adviser();
            if (type==1){
                adviser.setName(value);
            }
            else if (type==2){
                adviser.setQq(value);
            }
            else if (type==3){
                adviser.setEmail(value);
            }
            bmobObject = adviser;
        }


        else {
            User user = new User();
            if (type==1){
                user.setNickname(value);
            }
            else if (type==2){
                user.setQq(value);
            }
            else if (type==3){
                user.setEmail(value);
            }
            bmobObject = user;
        }

        bmobObject.setObjectId(objectId);

        bmobObject.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){
                    Toast.makeText(ModifyAccountItemActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                    if (type==1){
                        MyApplication.sharedPreferences.edit().putString("nickname",value).apply();
                    }

                    else if (type==2){
                        MyApplication.sharedPreferences.edit().putString("qq",value).apply();;
                    }
                    else if (type==3){
                        MyApplication.sharedPreferences.edit().putString("email",value).apply();
                    }

                    Intent intent = getIntent();
                    intent.putExtra("result",value);
                    setResult(RESULT_OK,intent);
                    finish();
                }else {
                    Toast.makeText(ModifyAccountItemActivity.this,"修改失败"+e,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void back(View view){
        finish();
    }
}
