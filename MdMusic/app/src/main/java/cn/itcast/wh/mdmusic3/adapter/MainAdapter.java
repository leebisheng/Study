package cn.itcast.wh.mdmusic3.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.media.MediaMetadata;
import android.media.MediaScannerConnection;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.itcast.wh.mdmusic3.MainActivity;
import cn.itcast.wh.mdmusic3.R;
import cn.itcast.wh.mdmusic3.util.BeanHelper;

/**
 * Created by Administrator on 2015/9/7.
 */
public class MainAdapter extends RecyclerView.Adapter {

    private Context context;
    private MediaController mediaController;
    private List<MediaSession.QueueItem> queue;

    //当前正在播放的音乐
    private MediaMetadata mediaMetadata;

    //当前正在播放音乐的状态
    private PlaybackState state;

    public MainAdapter(Context context, MediaController mediaController, List<MediaSession.QueueItem> queue) {
        this.context = context;
        this.mediaController = mediaController;
        this.queue = queue;
    }

    /**
     * 生成一个viewholder对象
     *
     * @param viewGroup
     * @param i
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(View.inflate(context, R.layout.music_item, null));
    }

    /**
     * 给生成的viewholder对象设置数据
     *
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final MyViewHolder myViewHolder = ((MyViewHolder) viewHolder);
        MediaSession.QueueItem item = queue.get(position);
        myViewHolder.titleTv.setText(item.getDescription().getTitle());
        myViewHolder.artistTv.setText(item.getDescription().getSubtitle());
        myViewHolder.mediaId = item.getDescription().getMediaId();
        Picasso.with(context).load(item.getDescription().getIconUri()).into(myViewHolder.coverIv);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击播放对应的音乐
                MainActivity.hasTarget=true;
                mediaController.getTransportControls().playFromMediaId(myViewHolder.mediaId, null);
            }
        });
        //给正在播放的音乐设置帧动画，进行突出显示
        if (state == null || (mediaMetadata != null && !mediaMetadata.getDescription().getMediaId().equals(myViewHolder.mediaId))) {
            myViewHolder.playStateIv.setBackgroundResource(0);
        } else {
            myViewHolder.playStateIv.setBackgroundResource(R.drawable.playing_bg);
            if (state.getState() == PlaybackState.STATE_PLAYING) {
                ((Animatable) myViewHolder.playStateIv.getBackground()).start();
            } else {
                ((Animatable) myViewHolder.playStateIv.getBackground()).stop();
            }
        }
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView coverIv;
        TextView titleTv;
        TextView artistTv;
        ImageView playStateIv;
        String mediaId;

        public MyViewHolder(View itemView) {
            super(itemView);
            coverIv = (ImageView) itemView.findViewById(R.id.coverIv);
            titleTv = (TextView) itemView.findViewById(R.id.titleTv);
            artistTv = (TextView) itemView.findViewById(R.id.artistTv);
            playStateIv = (ImageView) itemView.findViewById(R.id.playStateIv);
        }
    }


    @Override
    public int getItemCount() {
        return queue == null ? 0 : queue.size();
    }

    /**
     * 刷新列表数据
     *
     * @param queue
     */
    public void flushData(List<MediaSession.QueueItem> queue, MediaController mediaController) {
        this.queue = queue;
        this.mediaController = mediaController;
        notifyDataSetChanged();
    }

    public void updateMetadata(MediaMetadata mediaMetadata) {
        //通知重新适配当前歌曲
        if (this.mediaMetadata != null) {
            int oldIndex = BeanHelper.findIndexByMusic(queue, this.mediaMetadata);
            notifyItemChanged(oldIndex);
        }
        int newIndex = BeanHelper.findIndexByMusic(queue, mediaMetadata);
        if (newIndex != -1) {
            notifyItemChanged(newIndex);
            this.mediaMetadata = mediaMetadata;
        }
    }

    public void updateState(PlaybackState state) {
        this.state = state;
        if (this.mediaMetadata != null) {
            int curIndex = BeanHelper.findIndexByMusic(queue, this.mediaMetadata);
            notifyItemChanged(curIndex);
        }
    }
}
