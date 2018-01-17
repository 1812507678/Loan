package zhiyuan.com.loan.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;

import java.util.List;

import zhiyuan.com.loan.R;
import zhiyuan.com.loan.bean.StrategyArticle;
import zhiyuan.com.loan.util.MyUtils;


/**
 * Created by Administrator on 2016/7/26.
 */
public class ArticlelistAdapter extends BaseAdapter {
    private BitmapUtils bitmapUtils;
    private Context context;
    private List<StrategyArticle> articleListListContent;
    private BitmapFactory.Options options ;
    private final BitmapDisplayConfig bitmapDisplayConfig;

    public ArticlelistAdapter(Context context,List<StrategyArticle> data) {
        this.context = context;
        this.articleListListContent = data;
        bitmapUtils= new BitmapUtils(context);
        options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        //options.inSampleSize = 2;

        bitmapDisplayConfig = new BitmapDisplayConfig();
        BitmapSize bitmapSize = new BitmapSize((int) MyUtils.dp2px(context, 117), (int) MyUtils.dp2px(context, 70));
        bitmapDisplayConfig.setBitmapMaxSize(bitmapSize);
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

        View inflate;
        MyHolder myHolder;
        if (convertView!=null){
            inflate = convertView;
            myHolder = (MyHolder) inflate.getTag();
        }
        else {
            inflate =View.inflate(context, R.layout.list_article_item,null);
            myHolder = new MyHolder();
            myHolder.iv_readlist_artcileimage = (ImageView) inflate.findViewById(R.id.iv_readlist_artcileimage);
            myHolder.tv_readlist_title = (TextView) inflate.findViewById(R.id.tv_readlist_title);
            myHolder.tv_readlist_time = (TextView) inflate.findViewById(R.id.tv_readlist_time);
            myHolder.tv_readlist_count = (TextView) inflate.findViewById(R.id.tv_readlist_count);
            inflate.setTag(myHolder);
        }

        String imageurl = article.getimageUrl();
        bitmapUtils.display(myHolder.iv_readlist_artcileimage,imageurl,bitmapDisplayConfig);

        myHolder.tv_readlist_title.setText(article.getTitle());
        myHolder.tv_readlist_time.setText(article.getTime());
        myHolder.tv_readlist_count.setText("阅读量："+ article.getReadCount()+"");

        return inflate;
    }


    class MyHolder {
        ImageView iv_readlist_artcileimage;
        TextView tv_readlist_title;
        TextView tv_readlist_time;
        TextView tv_readlist_count;
    }

}

