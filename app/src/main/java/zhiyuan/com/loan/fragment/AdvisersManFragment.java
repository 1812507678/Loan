package zhiyuan.com.loan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.activity.ChattingActivity;
import zhiyuan.com.loan.bean.Adviser;
import zhiyuan.com.loan.view.CircleImageView;


/**
 * Created by haijun on 2016/7/16.
 */
public class AdvisersManFragment extends Fragment{

    private View inflate;

    private List<Adviser> adviserList ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_advisersman, new LinearLayout(getActivity()),false);

        initView();
        return inflate;
    }


    private void initView() {
        adviserList = new ArrayList<>();
        final ListView lv_advise_adviser = (ListView) inflate.findViewById(R.id.lv_advise_adviser);

        BmobQuery<Adviser> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<Adviser>() {
            @Override
            public void done(List<Adviser> list, BmobException e) {
                if (e==null){
                    if (list!=null){
                        for (int i=0;i<list.size();i++){
                            adviserList.add(list.get(i));

                        }
                    }
                    lv_advise_adviser.setAdapter(new MyListViewAdapter());
                }else {
                    Toast.makeText(getContext(),"失败,请检查网络"+e,Toast.LENGTH_SHORT).show();
                }
            }
        });

        lv_advise_adviser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Adviser adviser = adviserList.get(i);
                Intent intent = new Intent(getActivity(), ChattingActivity.class);
                intent.putExtra("advisePhone",adviser.getPhone());
                intent.putExtra("adviseName",adviser.getName());
                intent.putExtra("adviseIconUrl",adviser.getIconUrl());
                startActivity(intent);
            }
        });
    }

    class MyListViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return adviserList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Adviser adviser = adviserList.get(i);

            View inflate = View.inflate(getContext(), R.layout.list_advise_item, null);
            CircleImageView cv_adviseitem_icon = (CircleImageView) inflate.findViewById(R.id.cv_adviseitem_icon);
            TextView tv_adviseitem_name = (TextView) inflate.findViewById(R.id.tv_adviseitem_name);

            cv_adviseitem_icon.setImageResource(R.drawable.account_icon);
            BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
            bitmapUtils.display(cv_adviseitem_icon,adviser.getIconUrl());
            tv_adviseitem_name.setText(adviser.getName());

            return inflate;
        }
    }
}
