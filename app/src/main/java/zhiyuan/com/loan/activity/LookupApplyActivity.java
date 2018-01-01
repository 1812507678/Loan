package zhiyuan.com.loan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.bean.StudentApply58;

public class LookupApplyActivity extends BaseActivity {

    private TextView tv_histery_name;
    private TextView tv_histery_certif;
    private TextView tv_histery_qq;
    private TextView tv_histery_password;
    private TextView tv_histery_acount;
    private TextView tv_histery_record;
    private TextView tv_histery_grade;
    private String applyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_apply);

        initView();
    }

    private void initView() {
        TextView tv_lookup_histery = (TextView) findViewById(R.id.tv_lookup_histery);
        ScrollView sv_lookup_svro = (ScrollView) findViewById(R.id.sv_lookup_svro);

        applyId = MyApplication.sharedPreferences.getString("applyId", "");
        if (!applyId.equals("")){
            tv_lookup_histery.setVisibility(View.GONE);
            sv_lookup_svro.setVisibility(View.VISIBLE);
            tv_histery_name = (TextView) findViewById(R.id.tv_histery_name);
            tv_histery_qq = (TextView) findViewById(R.id.tv_histery_qq);
            tv_histery_certif = (TextView) findViewById(R.id.tv_histery_certif);
            tv_histery_acount = (TextView) findViewById(R.id.tv_histery_acount);
            tv_histery_password = (TextView) findViewById(R.id.tv_histery_password);
            tv_histery_record = (TextView) findViewById(R.id.tv_histery_record);
            tv_histery_grade = (TextView) findViewById(R.id.tv_histery_grade);

            loadData();
        }
    }
    private void loadData() {
        BmobQuery<StudentApply58> applyInfoBmobQuery = new BmobQuery<>();

        applyInfoBmobQuery.addWhereEqualTo("applyId",applyId);
        applyInfoBmobQuery.findObjects(this, new FindListener<StudentApply58>() {
            @Override
            public void onSuccess(List<StudentApply58> list) {
                if (list.size()>0){
                    StudentApply58 applyInfo = list.get(0);
                    tv_histery_name.setText(applyInfo.getName());
                    tv_histery_qq.setText(applyInfo.getQq());
                    tv_histery_certif.setText(applyInfo.getCertificate());
                    tv_histery_acount.setText(applyInfo.getAccount());
                    tv_histery_password.setText(applyInfo.getPassword());
                    tv_histery_record.setText(applyInfo.getMoney());
                    tv_histery_grade.setText(applyInfo.getMouth());
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    public void back(View view){
        finish();
    }
}

	