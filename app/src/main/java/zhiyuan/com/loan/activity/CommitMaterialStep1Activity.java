package zhiyuan.com.loan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import zhiyuan.com.loan.R;
import zhiyuan.com.loan.application.MyApplication;

public class CommitMaterialStep1Activity extends BaseActivity {

    private static final String TAG = "CommitMaterialStep1";
    private EditText et_commit_money;
    private EditText et_commit_mouth;
    private EditText et_commit_name;
    private EditText et_commit_phone;
    private ProgressDialog dialog;
    private TextView tv_commit_contactinf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_material_step1);

        initView();
    }

    private void initView() {
        et_commit_name = (EditText) findViewById(R.id.et_commit_name);
        et_commit_money = (EditText) findViewById(R.id.et_commit_money);
        et_commit_mouth = (EditText) findViewById(R.id.et_commit_mouth);
        et_commit_phone = (EditText) findViewById(R.id.et_commit_phone);
        tv_commit_contactinf = (TextView) findViewById(R.id.tv_commit_contactinf);

        String qqContactInfo = MyApplication.sharedPreferences.getString("qqContactInfo", "");
        if (!qqContactInfo.equals("")){
            tv_commit_contactinf.setText(qqContactInfo);
        }
    }


    public void netStep(View view){
        String name = et_commit_name.getText().toString();
        String money = et_commit_money.getText().toString();
        String mouth = et_commit_mouth.getText().toString();
        String phone = et_commit_phone.getText().toString();

        Log.i(TAG,","+name+","+money+","+mouth);
        if (money.equals("")){
            Toast.makeText(CommitMaterialStep1Activity.this, "请输入金额", Toast.LENGTH_SHORT).show();
        }
        else if (mouth.equals("")){
            Toast.makeText(CommitMaterialStep1Activity.this, "请输入月数", Toast.LENGTH_SHORT).show();
        }
        else if (name.equals("")){
            Toast.makeText(CommitMaterialStep1Activity.this, "请输入姓名", Toast.LENGTH_SHORT).show();
        }

        else if (phone.equals("")){
            Toast.makeText(CommitMaterialStep1Activity.this, "请输入电话", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(this, CommitMaterialStep2Activity.class);
            intent.putExtra("name",name);
            intent.putExtra("money",money);
            intent.putExtra("mouth",mouth);
            intent.putExtra("phone",phone);
            startActivity(intent);
            finish();
        }
    }

    public void back(View view){
        finish();
    }
}
