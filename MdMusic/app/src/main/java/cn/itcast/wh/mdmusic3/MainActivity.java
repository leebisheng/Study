package cn.itcast.wh.mdmusic3;

import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.itcast.wh.mdmusic3.adapter.MainAdapter;
import cn.itcast.wh.mdmusic3.common.BaseActivity;
import cn.itcast.wh.mdmusic3.common.IMediaControllerConsumer;


public class MainActivity extends BaseActivity implements IMediaControllerConsumer {

    @Bind(R.id.music_Rv)
    RecyclerView musicRv;
    @Bind(R.id.littleConverIv)
    ImageView littleConverIv;
    @Bind(R.id.titleTv)
    TextView titleTv;
    @Bind(R.id.artistTv)
    TextView artistTv;
    @Bind(R.id.cb_play)
    CheckBox cbPlay;

    private MediaController mediaController;

    public List<MediaSession.QueueItem> queue;
    private MainAdapter mainAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        addConsumer(this);
        musicRv.setLayoutManager(new GridLayoutManager(this, 2));
        mainAdapter = new MainAdapter(this, mediaController, queue);
        musicRv.setAdapter(mainAdapter);
        littleConverIv.setOnClickListener(bottomClickListener);
        cbPlay.setOnClickListener(bottomClickListener);
    }


    public static boolean hasTarget = false;

    /**
     *
     */
    private View.OnClickListener bottomClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
             if(view == cbPlay){
                 if(!hasTarget){
                     //如果当前一首歌曲都没有，默认我们播放第一首
                     if(queue!=null && queue.size()>0){
                         String mediaId = queue.get(0).getDescription().getMediaId();
                         mediaController.getTransportControls().playFromMediaId(mediaId,null);
                         hasTarget=true;
                     }
                 }else{
                     if(cbPlay.isChecked()){
                         mediaController.getTransportControls().play();
                     }else{
                         mediaController.getTransportControls().pause();
                     }
                 }
             }
        }
    };


    @Override
    public void onObtainMediaController(MediaController mediaController) {
        this.mediaController = mediaController;
        mediaController.registerCallback(controllCallback);
    }

    @Override
    public void onReleasedMediaController(MediaController mediaController) {
        mediaController.unregisterCallback(controllCallback);
    }

    private MediaController.Callback controllCallback = new MediaController.Callback() {

        /**
         * 当播放状态改变的时候
         * @param state
         */
        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            mainAdapter.updateState(state);
            //同步播放状态
            if(state!=null && state.getState()==PlaybackState.STATE_PLAYING){
                cbPlay.setChecked(true);
            }else{
                cbPlay.setChecked(false);
            }
        }

        /**
         * 当播放的歌曲变化的时候
         * @param metadata
         */
        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            mainAdapter.updateMetadata(metadata);
            //同步一下底部控制栏信息
            syncBottomInfo(metadata);
        }

        @Override
        public void onQueueChanged(List<MediaSession.QueueItem> queue) {
            MainActivity.this.queue = queue;
            //通知加载出所有的音乐列表资源
            mainAdapter.flushData(queue, mediaController);
        }

        @Override
        public void onExtrasChanged(Bundle extras) {
            super.onExtrasChanged(extras);
        }
    };

    /**
     * 同步底部播放控制信息
     *
     * @param metadata
     */
    private void syncBottomInfo(MediaMetadata metadata) {
        titleTv.setText(metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE));
        artistTv.setText(metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE));
        Picasso.with(this).load(metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI)).into(littleConverIv);
    }
}
