package cn.itcast.wh.mdmusic2;

import android.animation.Animator;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.transition.Transition;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.itcast.wh.mdmusic2.adapter.PlayingAdapter;
import cn.itcast.wh.mdmusic2.common.BaseActivity;
import cn.itcast.wh.mdmusic2.common.IMediaControllerConsumer;
import cn.itcast.wh.mdmusic2.util.BeanHelper;
import cn.itcast.wh.mdmusic2.util.DepthPageTransformer;
import cn.itcast.wh.mdmusic2.util.Lrc;

/**
 * Created by Administrator on 2015/9/3.
 * 当前曲目的音乐播放界面
 */
public class NowPlayingActivity extends BaseActivity implements IMediaControllerConsumer {

    @Bind(R.id.my_viewpager)
    ViewPager myViewpager;
    @Bind(R.id.pastTimeTv)
    TextView pastTimeTv;
    @Bind(R.id.lrcTv)
    TextView lrcTv;
    @Bind(R.id.totalTimeTv)
    TextView totalTimeTv;
    @Bind(R.id.textContainer)
    LinearLayout textContainer;
    @Bind(R.id.prevIv)
    ImageView prevIv;
    @Bind(R.id.playPauseCb)
    CheckBox playPauseCb;
    @Bind(R.id.nextIv)
    ImageView nextIv;
    @Bind(R.id.seekBar)
    SeekBar seekBar;
    @Bind(R.id.main_bottom_controller)
    RelativeLayout mainBottomController;
    private MediaController mediaController;
    private List<MediaSession.QueueItem> queue;
    private MediaMetadata metadata;
    private PlaybackState state;
    private PlayingAdapter playingAdapter;
    private Lrc lrc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1:开启转场动画
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_now_playing);
        ButterKnife.bind(this);
        playingAdapter = new PlayingAdapter(this, queue);
        myViewpager.setPageTransformer(true, new DepthPageTransformer());
        myViewpager.setAdapter(playingAdapter);
        addConsumer(this);
        prevIv.setOnClickListener(bottomClickListener);
        nextIv.setOnClickListener(bottomClickListener);
        //2:设置转场共享元素动画到指定的view上
        myViewpager.setTransitionName("COVER");
        //给底部播放控制设置一个揭示动画
        getWindow().getSharedElementEnterTransition().addListener(transitionListener);

    }

    private Transition.TransitionListener transitionListener = new Transition.TransitionListener() {

        @Override
        public void onTransitionStart(Transition transition) {
            mainBottomController.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onTransitionEnd(Transition transition) {
            mainBottomController.setVisibility(View.VISIBLE);
            //设置一个揭示动画
            float endRadius = (float) Math.hypot(mainBottomController.getWidth(), mainBottomController.getHeight());
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(mainBottomController, 0, mainBottomController.getHeight(), 0, endRadius);
            circularReveal.setDuration(2000);
            circularReveal.start();
        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    };

    @Override
    public void OnObtainMediaController(MediaController mediaController) {
        this.mediaController = mediaController;
        mediaController.registerCallback(controllback);
        this.queue = mediaController.getQueue();
        this.metadata = mediaController.getMetadata();
        this.state = mediaController.getPlaybackState();
        controllback.onQueueChanged(this.queue);
        controllback.onMetadataChanged(this.metadata);
        myViewpager.addOnPageChangeListener(simpleOnpageChangeListener);
        controllback.onPlaybackStateChanged(this.state);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    //手指是否正在seekBar上
    boolean isTouchingSeekbar = false;


    private View.OnClickListener bottomClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == prevIv) {
                //播放上一首音乐
                mediaController.getTransportControls().skipToPrevious();
            }
            if (view == nextIv) {
                //播放下一首音量
                mediaController.getTransportControls().skipToNext();
            }
        }
    };

    /**
     * seekBar进度监听
     */
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        /**
         * 当播放进度改变的时候
         * @param seekBar
         * @param progress
         * @param b
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            pastTimeTv.setText(BeanHelper.getTime(progress));
            if (lrc != null) {
                Lrc.LrcLine lrcLine = lrc.getLrcLine(progress);
                if (lrcLine != null) {
                    String content = lrcLine.getContent();
                    lrcTv.setText(content);
                } else {
                    lrcTv.setText("");
                }
            }

        }

        /**
         * 当手指滑动触摸到的时候
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isTouchingSeekbar = true;
        }

        /**
         * 当手指停止拖动触摸的时候
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isTouchingSeekbar = false;
            mediaController.getTransportControls().seekTo(seekBar.getProgress());
        }
    };


    private ViewPager.OnPageChangeListener simpleOnpageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            //当我们滑动到其中一个封面图片时，播放对应的歌曲
            if (queue != null && queue.size() > 0) {
                String mediaId = queue.get(position).getDescription().getMediaId();
                mediaController.getTransportControls().playFromMediaId(mediaId, null);
            }
        }
    };

    @Override
    public void OnReleasedMediaController(MediaController mediaController) {
        mediaController.unregisterCallback(controllback);
    }


    private static Handler handler = new Handler();

    Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            controllback.onPlaybackStateChanged(state);
        }
    };


    /**
     * 当前播放界面的信使
     */
    private android.media.session.MediaController.Callback controllback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            NowPlayingActivity.this.state = state;
            if (state != null) {
                //设置当前的播放进度（播放时长）--只要还在播放，进度和时长是一直在更新的
                /**
                 * 公式：公式：当前时刻 = (系统开机时间 [手机系统开机后，一直到当前所计算出来的毫秒值时间]-最后更新进度的时间)*播放的速度+最后更新的进度值(位置)
                 */
                long kjtime = SystemClock.elapsedRealtime();
                long lastPositionUpdateTime = state.getLastPositionUpdateTime();
                float speed = state.getPlaybackSpeed();
                long lastPosition = state.getPosition();
                //计算当前的播放进度
                long progress = (long) ((kjtime - lastPositionUpdateTime) * speed + lastPosition);
                //设置当前播放时长
                pastTimeTv.setText(BeanHelper.getTime(progress));
                //在手指正拖动的时间内，不去更新进度
                seekBar.setProgress((int) progress);
            }
            if (state != null && state.getState() == PlaybackState.STATE_PLAYING) {
                //需要去一直不断的更新播放进度
                handler.postDelayed(updateProgressRunnable, 100);
            }

            //判断一下当前是否支持上一首、或者下一首的歌曲切换，将其状态变化
            if (state != null) {
                long actions = state.getActions();
                long action = actions & PlaybackState.ACTION_SKIP_TO_PREVIOUS;
                if (action == PlaybackState.ACTION_SKIP_TO_PREVIOUS) {
                    //支持上一首播放
                    prevIv.setEnabled(true);
                } else {
                    prevIv.setEnabled(false);
                }

                long actionNext = actions & PlaybackState.ACTION_SKIP_TO_NEXT;
                if (actionNext == PlaybackState.ACTION_SKIP_TO_NEXT) {
                    //支持下一首播放
                    nextIv.setEnabled(true);
                } else {
                    nextIv.setEnabled(false);
                }
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            if (metadata != null) {
                //viewpager定位到播放歌曲的位置上
                int curIndex = BeanHelper.findIndexInQueue(queue, metadata);
                myViewpager.setCurrentItem(curIndex);
                //设置歌曲的总时长(总进度)
                long duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
                seekBar.setMax((int) duration);
                //设置歌曲的总时长,以“00:00”的形式展示
                totalTimeTv.setText(BeanHelper.getTime(duration));

                Uri uriPath = Uri.parse(metadata.getString(MediaMetadata.METADATA_KEY_ART_URI));
                lrc = Lrc.Factory.create(uriPath.getPath());
            } else {
                seekBar.setMax(0);
            }
        }

        @Override
        public void onQueueChanged(List<MediaSession.QueueItem> queue) {
            if (queue != null) {
                playingAdapter.flushData(queue);
            }
        }

        @Override
        public void onExtrasChanged(Bundle extras) {
            super.onExtrasChanged(extras);
        }
    };
}
