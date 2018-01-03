package zhiyuan.com.loan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.bean.StudentApply58;
import zhiyuan.com.loan.util.ChooseAlertDialogUtil;

public class CommitMaterialStep2Activity extends BaseActivity {

    private EditText et_commit_qq;
    private EditText et_commit_certif;
    private EditText et_commit_acount;
    private EditText et_commit_password;
    private ProgressDialog dialog;
    private String applyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_material_step2);

        initView();
    }

    private void initView() {
        et_commit_qq = (EditText) findViewById(R.id.et_commit_qq);
        et_commit_certif = (EditText) findViewById(R.id.et_commit_certif);
        et_commit_acount = (EditText) findViewById(R.id.et_commit_acount);
        et_commit_password = (EditText) findViewById(R.id.et_commit_password);

        RelativeLayout rl_step2_contactme = findViewById(R.id.rl_step2_contactme);
        rl_step2_contactme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseAlertDialogUtil chooseAlertDialogUtil = new ChooseAlertDialogUtil(CommitMaterialStep2Activity.this);
                chooseAlertDialogUtil.setAlertDialogTextContact();
            }
        });
    }

    public void commitInfo(View view){
        String qq = et_commit_qq.getText().toString();
        String certificate = et_commit_certif.getText().toString();
        String account = et_commit_acount.getText().toString();
        String password = et_commit_password.getText().toString();

        if (qq.equals("")){
            Toast.makeText(CommitMaterialStep2Activity.this, "请输入qq", Toast.LENGTH_SHORT).show();
        }
        else if (certificate.equals("")){
            Toast.makeText(CommitMaterialStep2Activity.this, "请输入身份证号", Toast.LENGTH_SHORT).show();
        }
        /*else if (account.equals("")){
            Toast.makeText(CommitMaterialStep2Activity.this, "请输入学信网账号", Toast.LENGTH_SHORT).show();
        }
        else if (password.equals("")){
            Toast.makeText(CommitMaterialStep2Activity.this, "请输入学信网密码", Toast.LENGTH_SHORT).show();
        }*/

        else {
            //数据提交
            applyId = "appid"+System.currentTimeMillis();

            Intent intent = getIntent();
            String name = intent.getStringExtra("name");
            String money = intent.getStringExtra("money");
            String mouth = intent.getStringExtra("mouth");
            String phone = intent.getStringExtra("phone");


            StudentApply58 applyInfo = new StudentApply58(applyId,name,qq,certificate,account,password,phone,money,mouth);
            uploadData(applyInfo);
        }

    }

    private void uploadData(StudentApply58 applyInfo) {
        showDialog("正在提交");
        applyInfo.save(new SaveListener() {


            @Override
            public void done(Object o, BmobException e) {
                if (e==null){
                    hideDialog();
                    Toast.makeText(CommitMaterialStep2Activity.this, "申请成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CommitMaterialStep2Activity.this,ApplySuccessActivity.class));
                    MyApplication.sharedPreferences.edit().putString("applyId",applyId).commit();
                    finish();
                }else {
                    hideDialog();
                    Toast.makeText(CommitMaterialStep2Activity.this, "申请失败"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });

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
