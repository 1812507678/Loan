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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.activity.ShowArticleDetailActivity;
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

                            if (list!=null){
                                if (list.size()>1){
                                    lastAritcleID = list.get(0).getArticleId();
                                    Toast.makeText(getActivity(),"加载成功",Toast.LENGTH_SHORT).show();
                                }


                                else {
                                    Toast.makeText(getActivity(),"没有更多数据了",Toast.LENGTH_SHORT).show();
                                }

                                for (int i=0;i<list.size();i++){
                                    if (Integer.parseInt(lastAritcleID)<Integer.parseInt(list.get(i).getArticleId())){
                                        lastAritcleID =list.get(i).getArticleId();
                                    }

                                    articleListListContent.add(list.get(i));
                                    Log.i(TAG,"list.get(i):"+articleListListContent.get(i).toString());
                                }
                            }

                            strategyAdapter.notifyDataSetChanged();

                            lv_strategy_articlelist.iv_chatt_refresh.clearAnimation();
                            lv_strategy_articlelist.viewFoot.setPadding(0,0,0,-lv_strategy_articlelist.viewFoot.getMeasuredHeight());
                        }else {
                            Toast.makeText(getActivity(),"刷新失败,请检查网络"+e,Toast.LENGTH_SHORT).show();
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
                StrategyArticle article = articleListListContent.get(position-1);
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
                            Toast.makeText(getActivity(),"刷新成功",Toast.LENGTH_SHORT).show();
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
                    strategyAdapter = new ArticlelistAdapter();
                    lv_strategy_articlelist.setAdapter(strategyAdapter);
                }else {
                    Toast.makeText(getActivity(),"失败,请检查网络"+e,Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    class ArticlelistAdapter extends BaseAdapter {
        BitmapUtils bitmapUtils;

        public ArticlelistAdapter() {
            bitmapUtils= new BitmapUtils(getActivity());
        }

        @Override
        public int getCount() {
            return articleListListContent.size();
        }

        @Override
        public Object getItem(int position) {
            return articleListListContent.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }



        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final StrategyArticle article = articleListListContent.get(position);
            View inflate = View.inflate(getActivity(), R.layout.list_article_item,null);
            ImageView iv_readlist_artcileimage = (ImageView) inflate.findViewById(R.id.iv_readlist_artcileimage);
            TextView tv_readlist_title = (TextView) inflate.findViewById(R.id.tv_readlist_title);
            TextView tv_readlist_time = (TextView) inflate.findViewById(R.id.tv_readlist_time);
            TextView tv_readlist_count = (TextView) inflate.findViewById(R.id.tv_readlist_count);

            String imageurl = article.getimageUrl();
            bitmapUtils.display(iv_readlist_artcileimage,imageurl);

            tv_readlist_title.setText(article.getTitle());
            tv_readlist_time.setText(article.getTime());
            tv_readlist_count.setText("阅读量："+ article.getReadCount()+"");

            return inflate;
        }
    }
}
