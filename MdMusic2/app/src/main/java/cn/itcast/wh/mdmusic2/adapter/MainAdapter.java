package cn.itcast.wh.mdmusic2.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.itcast.wh.mdmusic2.R;
import cn.itcast.wh.mdmusic2.util.BeanHelper;

/**
 * Created by Administrator on 2015/9/3.
 */
public class MainAdapter extends RecyclerView.Adapter {

    private Context context;
    private MediaController mediaController;
    private List<MediaSession.QueueItem> queue;

    private MediaMetadata mediaMetadata;
    private PlaybackState state;

    public MainAdapter(Context context, MediaController mediaController, List<MediaSession.QueueItem> queue) {
        this.context = context;
        this.mediaController = mediaController;
        this.queue = queue;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MusicViewHolder(View.inflate(context, R.layout.music_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final MusicViewHolder musicViewHolder = (MusicViewHolder) viewHolder;
        MediaSession.QueueItem queueItem = queue.get(position);
        //设置数据
        Picasso.with(context).load(queueItem.getDescription().getIconUri()).into(musicViewHolder.coverIv);
        musicViewHolder.titleTv.setText(queueItem.getDescription().getTitle());
        musicViewHolder.artistTv.setText(queueItem.getDescription().getSubtitle());
        musicViewHolder.mediaID = queueItem.getDescription().getMediaId();
        musicViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaController.getTransportControls().playFromMediaId(musicViewHolder.mediaID, null);
            }
        });
        if (this.mediaMetadata == null || !musicViewHolder.mediaID.equals(this.mediaMetadata.getDescription().getMediaId())) {
            musicViewHolder.playStateIv.setBackgroundResource(0);
        } else {
            musicViewHolder.playStateIv.setBackgroundResource(R.drawable.playing_bg);
            if (state != null && state.getState() == PlaybackState.STATE_PLAYING) {
                ((Animatable) musicViewHolder.playStateIv.getBackground()).start();
            } else {
                ((Animatable) musicViewHolder.playStateIv.getBackground()).stop();
            }
        }
    }

    /**
     * 刷新列表
     *
     * @param queue
     * @param mediaController
     */
    public void flushData(List<MediaSession.QueueItem> queue, MediaController mediaController) {
        this.queue = queue;
        this.mediaController = mediaController;
        this.mediaMetadata = mediaController.getMetadata();
        this.state = mediaController.getPlaybackState();
        notifyDataSetChanged();
    }

    private class MusicViewHolder extends RecyclerView.ViewHolder {
        ImageView coverIv;
        TextView titleTv;
        TextView artistTv;
        ImageView playStateIv;
        String mediaID;

        public MusicViewHolder(View itemView) {
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
     * 更新播放歌曲
     * 停止上一次的音乐播放，播放本次指定的音乐
     *
     * @param mediaMetadata
     */
    public void updateMetadata(MediaMetadata mediaMetadata) {
        if (this.mediaMetadata != null) {
            //当前已经有歌曲正在播放，将正在播放的歌曲停止
            int oldIndex = BeanHelper.findIndexInQueue(queue, this.mediaMetadata);
            notifyItemChanged(oldIndex);
        }
        //进行本次歌曲的播放
        if (queue != null && queue.size() > 0) {
            int newIndex = BeanHelper.findIndexInQueue(queue, mediaMetadata);
            notifyItemChanged(newIndex);
            this.mediaMetadata = mediaMetadata;
        }
    }

    /**
     * 更新歌曲播放状态
     *
     * @param state
     */
    public void updateState(PlaybackState state) {
        this.state = state;
        if (this.mediaMetadata != null) {
            int curIndex = BeanHelper.findIndexInQueue(queue, this.mediaMetadata);
            notifyItemChanged(curIndex);
        }
    }
}
