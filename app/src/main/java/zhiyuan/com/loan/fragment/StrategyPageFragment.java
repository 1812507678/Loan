package zhiyuan.com.loan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.activity.ShowArticleDetailActivity;
import zhiyuan.com.loan.adapter.ArticlelistAdapter;
import zhiyuan.com.loan.bean.StrategyArticle;
import zhiyuan.com.loan.view.LastMsgListView;



/**
 * Created by Administrator on 2016/7/7.
 */
public class StrategyPageFragment extends Fragment {

    private static final String TAG = "HomePageFragment";
    private View inflate;
    private List<StrategyArticle> articleListListContent;
    private LastMsgListView lv_strategy_articlelist;
    private boolean isRefresh = false;
    private ArticlelistAdapter strategyAdapter;
    private String lastAritcleID;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.frgament_page_strategy, new LinearLayout(getActivity()),false);
        initView();
        return inflate;
    }

    private void initView() {
        articleListListContent = new ArrayList<>();
        lv_strategy_articlelist = (LastMsgListView) inflate.findViewById(R.id.lv_strategy_articlelist);

        lv_strategy_articlelist.setRefreshDataListener(new LastMsgListView.RefreshDataListener() {
            @Override
            public void refresh() {
                isRefresh = true;
                loadInitData();
            }

            @Override
            public void loadMore() {
                Log.i(TAG,"loadMore");
                BmobQuery<StrategyArticle> bmobQuery = new BmobQuery<>();
                bmobQuery.setLimit(10);
                Log.i(TAG,"articleListListContent.get(0):"+articleListListContent.get(0));
                bmobQuery.addWhereGreaterThan("articleId",lastAritcleID);
                bmobQuery.findObjects(new FindListener<StrategyArticle>() {
                    @Override
                    public void done(List<StrategyArticle> list, BmobException e) {
                        if (e==null){
                            Log.i(TAG,"onSuccess");

                            if (list!=null && list.size()>0){
                                lastAritcleID = list.get(0).getArticleId();
                                Toast.makeText(getContext(),"加载成功",Toast.LENGTH_SHORT).show();

                                for (int i=0;i<list.size();i++){
                                    if (Integer.parseInt(lastAritcleID)<Integer.parseInt(list.get(i).getArticleId())){
                                        lastAritcleID =list.get(i).getArticleId();
                                    }

                                    articleListListContent.add(list.get(i));
                                    Log.i(TAG,"list.get(i):"+articleListListContent.get(i).toString());
                                }
                                strategyAdapter.notifyDataSetChanged();
                            }
                            else {
                                Toast.makeText(getContext(),"没有更多数据了",Toast.LENGTH_SHORT).show();
                            }

                            lv_strategy_articlelist.iv_chatt_refresh.clearAnimation();
                            lv_strategy_articlelist.viewFoot.setPadding(0,0,0,-lv_strategy_articlelist.viewFoot.getMeasuredHeight());
                        }else {
                            Toast.makeText(getContext(),"刷新失败,请检查网络"+e,Toast.LENGTH_SHORT).show();
                            lv_strategy_articlelist.iv_chatt_refresh.clearAnimation();
                            lv_strategy_articlelist.viewFoot.setPadding(0,0,0,-lv_strategy_articlelist.viewFoot.getMeasuredHeight());
                        }
                    }
                });
            }
        });


        loadInitData();

        lv_strategy_articlelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position-1<articleListListContent.size() && position-1>=0){
                    StrategyArticle article = articleListListContent.get(position-1);
                    if (article!=null){
                        Log.i(TAG,article.toString());
                        String articleurl = article.getArticleUrl();
                        String objectId = article.getObjectId();
                        Intent intent = new Intent(getActivity(),ShowArticleDetailActivity.class);
                        intent.putExtra("articleurl",articleurl);
                        Log.i(TAG,"articleurl:"+articleurl);
                        Log.i(TAG,"article:"+article.getTitle());
                        intent.putExtra("articleId",article.getArticleId());

                        intent.putExtra("objectId",objectId);
                        intent.putExtra("readCount",article.getReadCount());
                        startActivity(intent);
                    }
                }
            }
        });
    }

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if (isRefresh){
                        View viewHead = lv_strategy_articlelist.getChildAt(0);
                        if (viewHead!=null){
                            Log.i(TAG,"viewHead.getMeasuredHeight():"+viewHead.getMeasuredHeight());
                            lv_strategy_articlelist.setPadding(0,-viewHead.getMeasuredHeight(),0,0);
                            lv_strategy_articlelist.state = 0;
                            lv_strategy_articlelist.tv_head_msg.setText("下拉刷新");
                            lv_strategy_articlelist.iv_head_icon.clearAnimation();
                            lv_strategy_articlelist.iv_head_icon.setImageResource(R.drawable.indicator_arrow);

                            strategyAdapter.notifyDataSetChanged();
                            Toast.makeText(getContext(),"刷新成功",Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    };

    //加载初始化数据
    private void loadInitData() {
        BmobQuery<StrategyArticle> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(10);
        bmobQuery.findObjects(new FindListener<StrategyArticle>() {
            @Override
            public void done(List<StrategyArticle> list, BmobException e) {
                if (e==null){
                    articleListListContent.clear();
                    if (list!=null && list.size()>0){
                        myHandler.sendEmptyMessage(0);
                        lastAritcleID =list.get(0).getArticleId();
                        for (int i=0;i<list.size();i++){
                            articleListListContent.add(list.get(i));

                            if (Integer.parseInt(lastAritcleID)<Integer.parseInt(list.get(i).getArticleId())){
                                lastAritcleID =list.get(i).getArticleId();
                            }
                        }
                    }
                    strategyAdapter = new ArticlelistAdapter(getActivity(),articleListListContent);
                    lv_strategy_articlelist.setAdapter(strategyAdapter);
                }else {
                    Toast.makeText(getContext(),"失败,请检查网络"+e,Toast.LENGTH_SHORT).show();
                }
            }

        });
    }



}
