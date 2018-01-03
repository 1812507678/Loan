package zhiyuan.com.loan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import zhiyuan.com.loan.R;
import zhiyuan.com.loan.adapter.ArticlelistAdapter;
import zhiyuan.com.loan.application.MyApplication;
import zhiyuan.com.loan.bean.ArticleCollection;
import zhiyuan.com.loan.bean.StrategyArticle;

public class CollectArticleActivity extends BaseActivity {

    private static final String TAG = "CollectArticleActivity";
    private ListView lv_strategy_articlelist;
    private List<StrategyArticle> articleListLis;
    private TextView tv_null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_article);
        initView();
    }

    private void initView() {
        lv_strategy_articlelist = (ListView) findViewById(R.id.lv_collected_articlelist);
        tv_null = (TextView) findViewById(R.id.tv_null);

        articleListLis = new ArrayList<>();


        BmobQuery<ArticleCollection> collectionQuery = new BmobQuery<>();
        collectionQuery.addWhereEqualTo("type",1); // 收藏
        collectionQuery.addWhereEqualTo("userId", MyApplication.sharedPreferences.getString("userId",""));
        collectionQuery.findObjects(new FindListener<ArticleCollection>() {
            @Override
            public void done(List<ArticleCollection> list, BmobException e) {
                if (e==null){
                    BmobQuery<StrategyArticle> bmobQuery = new BmobQuery<>();
                    Collection<String> collection = new ArrayList<>();
                    for (int i=0;i<list.size();i++){
                        collection.add(list.get(i).getArticleId());
                    }
                    bmobQuery.addWhereContainedIn("articleId",collection);

                    bmobQuery.findObjects(new FindListener<StrategyArticle>() {
                        @Override
                        public void done(List<StrategyArticle> list, BmobException e) {
                            if (e==null){
                                Log.i(TAG,"list.size()2:"+list.size());
                                if (list.size()>0){
                                    lv_strategy_articlelist.setVisibility(View.VISIBLE);
                                    tv_null.setVisibility(View.INVISIBLE);
                                }

                                for (int i=0;i<list.size();i++){
                                    articleListLis.add(list.get(i));
                                    Log.i(TAG,"list.get(i):"+ articleListLis.get(i).toString());

                                }
                                lv_strategy_articlelist.setAdapter(new ArticlelistAdapter(CollectArticleActivity.this,articleListLis));
                            }else {
                                Toast.makeText(CollectArticleActivity.this,"失败,请检查网络"+e,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {

                }
            }
        });


        lv_strategy_articlelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StrategyArticle article = articleListLis.get(position);
                Log.i(TAG,article.toString());
                String articleurl = article.getArticleUrl();
                String objectId = article.getObjectId();
                Intent intent = new Intent(CollectArticleActivity.this,ShowArticleDetailActivity.class);
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

    public void back(View view){
        finish();
    }
}
