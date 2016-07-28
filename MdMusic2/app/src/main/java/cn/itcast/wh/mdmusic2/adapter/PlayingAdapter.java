package cn.itcast.wh.mdmusic2.adapter;

import android.content.Context;
import android.media.session.MediaSession;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.wh.mdmusic2.NowPlayingActivity;

/**
 * Created by Administrator on 2015/9/3.
 * <p/>
 * 音乐列表封面的viewpager适配器
 * 需要使用缓存机制
 */
public class PlayingAdapter extends PagerAdapter {


    private Context context;
    private List<MediaSession.QueueItem> queue;

    private List<ImageView> cache = new ArrayList<ImageView>();

    public PlayingAdapter(Context context, List<MediaSession.QueueItem> queue) {
        this.context = context;
        this.queue = queue;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //如果缓存集合当中没有，就new一个image,如果有，就从缓存集合当中移出出来，然后将移出出来的对象返回给adapter去生成界面
        ImageView result = null;
        if (cache.size() == 0) {
            result = new ImageView(context);
            result.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            result.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            result = cache.remove(0);
        }
        Picasso.with(context).load(queue.get(position).getDescription().getIconUri()).into(result);
        container.addView(result);
        return result;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
        cache.add((ImageView) object);
    }

    @Override
    public int getCount() {
        return queue == null ? 0 : queue.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    /**
     * 刷新集合数据
     *
     * @param list
     */
    public void flushData(List<MediaSession.QueueItem> list) {
        this.queue = list;
        notifyDataSetChanged();
    }
}
