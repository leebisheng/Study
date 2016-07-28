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
 * ��ǰ��Ŀ�����ֲ��Ž���
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
        //1:����ת������
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_now_playing);
        ButterKnife.bind(this);
        playingAdapter = new PlayingAdapter(this, queue);
        myViewpager.setPageTransformer(true, new DepthPageTransformer());
        myViewpager.setAdapter(playingAdapter);
        addConsumer(this);
        prevIv.setOnClickListener(bottomClickListener);
        nextIv.setOnClickListener(bottomClickListener);
        //2:����ת������Ԫ�ض�����ָ����view��
        myViewpager.setTransitionName("COVER");
        //���ײ����ſ�������һ����ʾ����
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
            //����һ����ʾ����
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

    //��ָ�Ƿ�����seekBar��
    boolean isTouchingSeekbar = false;


    private View.OnClickListener bottomClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == prevIv) {
                //������һ������
                mediaController.getTransportControls().skipToPrevious();
            }
            if (view == nextIv) {
                //������һ������
                mediaController.getTransportControls().skipToNext();
            }
        }
    };

    /**
     * seekBar���ȼ���
     */
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        /**
         * �����Ž��ȸı��ʱ��
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
         * ����ָ������������ʱ��
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isTouchingSeekbar = true;
        }

        /**
         * ����ָֹͣ�϶�������ʱ��
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
            //�����ǻ���������һ������ͼƬʱ�����Ŷ�Ӧ�ĸ���
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
     * ��ǰ���Ž������ʹ
     */
    private android.media.session.MediaController.Callback controllback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            NowPlayingActivity.this.state = state;
            if (state != null) {
                //���õ�ǰ�Ĳ��Ž��ȣ�����ʱ����--ֻҪ���ڲ��ţ����Ⱥ�ʱ����һֱ�ڸ��µ�
                /**
                 * ��ʽ����ʽ����ǰʱ�� = (ϵͳ����ʱ�� [�ֻ�ϵͳ������һֱ����ǰ����������ĺ���ֵʱ��]-�����½��ȵ�ʱ��)*���ŵ��ٶ�+�����µĽ���ֵ(λ��)
                 */
                long kjtime = SystemClock.elapsedRealtime();
                long lastPositionUpdateTime = state.getLastPositionUpdateTime();
                float speed = state.getPlaybackSpeed();
                long lastPosition = state.getPosition();
                //���㵱ǰ�Ĳ��Ž���
                long progress = (long) ((kjtime - lastPositionUpdateTime) * speed + lastPosition);
                //���õ�ǰ����ʱ��
                pastTimeTv.setText(BeanHelper.getTime(progress));
                //����ָ���϶���ʱ���ڣ���ȥ���½���
                seekBar.setProgress((int) progress);
            }
            if (state != null && state.getState() == PlaybackState.STATE_PLAYING) {
                //��Ҫȥһֱ���ϵĸ��²��Ž���
                handler.postDelayed(updateProgressRunnable, 100);
            }

            //�ж�һ�µ�ǰ�Ƿ�֧����һ�ס�������һ�׵ĸ����л�������״̬�仯
            if (state != null) {
                long actions = state.getActions();
                long action = actions & PlaybackState.ACTION_SKIP_TO_PREVIOUS;
                if (action == PlaybackState.ACTION_SKIP_TO_PREVIOUS) {
                    //֧����һ�ײ���
                    prevIv.setEnabled(true);
                } else {
                    prevIv.setEnabled(false);
                }

                long actionNext = actions & PlaybackState.ACTION_SKIP_TO_NEXT;
                if (actionNext == PlaybackState.ACTION_SKIP_TO_NEXT) {
                    //֧����һ�ײ���
                    nextIv.setEnabled(true);
                } else {
                    nextIv.setEnabled(false);
                }
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            if (metadata != null) {
                //viewpager��λ�����Ÿ�����λ����
                int curIndex = BeanHelper.findIndexInQueue(queue, metadata);
                myViewpager.setCurrentItem(curIndex);
                //���ø�������ʱ��(�ܽ���)
                long duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
                seekBar.setMax((int) duration);
                //���ø�������ʱ��,�ԡ�00:00������ʽչʾ
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
