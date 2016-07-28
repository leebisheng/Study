package cn.itcast.wh.mdmusic2;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.*;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.ChangeImageTransform;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.itcast.wh.mdmusic2.adapter.MainAdapter;
import cn.itcast.wh.mdmusic2.common.BaseActivity;
import cn.itcast.wh.mdmusic2.common.IMediaControllerConsumer;
import cn.itcast.wh.mdmusic2.enigee.PlayMode;


public class MainActivity extends BaseActivity implements IMediaControllerConsumer {

    @Bind(R.id.music_rv)
    RecyclerView musicRv;
    @Bind(R.id.littleConverIv)
    ImageView littleConverIv;
    @Bind(R.id.titleTv)
    TextView titleTv;
    @Bind(R.id.artistTv)
    TextView artistTv;
    @Bind(R.id.cb_play)
    CheckBox cbPlay;
    @Bind(R.id.content)
    FrameLayout content;
    @Bind(R.id.drawLayout)
    DrawerLayout drawLayout;
    @Bind(R.id.tv_udate_volume)
    TextView tvUdateVolume;
    @Bind(R.id.iv_all)
    ImageView ivAll;
    @Bind(R.id.tv_repeat_all)
    TextView tvRepeatAll;
    @Bind(R.id.iv_one)
    ImageView ivOne;
    @Bind(R.id.tv_repeat_one)
    TextView tvRepeatOne;
    @Bind(R.id.iv_random)
    ImageView ivRandom;
    @Bind(R.id.tv_random)
    TextView tvRandom;
    @Bind(R.id.tv_quit_play)
    TextView tvQuitPlay;
    @Bind(R.id.menu_container)
    LinearLayout menuContainer;
    private MediaController mediaController;
    private MainAdapter mainAdapter;
    private ActionBarDrawerToggle toggle;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1:开启转场动画支持
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        addConsumer(this);
        //初始化播放列表
        initDraw();
        musicRv.setLayoutManager(new GridLayoutManager(this, 2));
        mainAdapter = new MainAdapter(this, mediaController, queue);
        musicRv.setAdapter(mainAdapter);
        cbPlay.setOnClickListener(bottomClickListner);
        littleConverIv.setOnClickListener(bottomClickListner);
    }

    /**
     * 搭建开关菜单
     */
    private void initDraw() {
        toggle = new ActionBarDrawerToggle(this, drawLayout, 0, 0);
        drawLayout.setDrawerListener(toggle);
        //显示出来
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        toggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        toggle.syncState();
        super.onPostCreate(savedInstanceState);
    }

    private boolean hasTarget = false;

    private View.OnClickListener bottomClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == cbPlay) {
                //如果当前没有正在播放的音乐，应该怎么办:默认播放列表的第一首歌曲
                if (!hasTarget && queue != null && queue.size() > 0) {
                    String mediaId = queue.get(0).getDescription().getMediaId();
                    mediaController.getTransportControls().playFromMediaId(mediaId, null);
                    hasTarget = true;
                }
                if (hasTarget) {
                    if (cbPlay.isChecked()) {
                        //继续播放
                        mediaController.getTransportControls().play();
                    } else {
                        mediaController.getTransportControls().pause();
                    }
                }
            } else if (view == littleConverIv) {
                //2：设置转场动画
                ChangeImageTransform changeImageTransform = new ChangeImageTransform();
                changeImageTransform.setDuration(1000);
                getWindow().setSharedElementExitTransition(changeImageTransform);
                //3: 使用activityOptions去开启activity
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, littleConverIv, "COVER");
                Intent intent = new Intent(MainActivity.this, NowPlayingActivity.class);
//                startActivity(intent);
                startActivity(intent, options.toBundle());
            }
        }
    };


    @Override
    public void OnObtainMediaController(MediaController mediaController) {
        this.mediaController = mediaController;
        mediaController.registerCallback(controllCallback);
        controllCallback.onQueueChanged(mediaController.getQueue());
        controllCallback.onMetadataChanged(mediaController.getMetadata());
        controllCallback.onPlaybackStateChanged(mediaController.getPlaybackState());
        //演示作为mediaController方给mediaSession发送播放控制命令，看mediaSession方的信使是否可以收到
    }

    @Override
    public void OnReleasedMediaController(MediaController mediaController) {
        mediaController.unregisterCallback(controllCallback);
    }

    private List<MediaSession.QueueItem> queue;

    /**
     * mediaController的信使，接收mediaSession方的setXX行为
     */
    private MediaController.Callback controllCallback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            //更新当前播放音乐的状态
            if (state != null) {
                mainAdapter.updateState(state);
                if (state.getState() == PlaybackState.STATE_PLAYING) {
                    cbPlay.setChecked(true);
                } else {
                    cbPlay.setChecked(false);
                }
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            if (metadata != null) {
                hasTarget = true;
                //更新当前播放音乐
                mainAdapter.updateMetadata(metadata);
                //同步底部的播放控制栏信息
                updateBottomInfo(metadata);
            }
        }

        @Override
        public void onQueueChanged(List<MediaSession.QueueItem> queue) {
            MainActivity.this.queue = queue;
            mainAdapter.flushData(queue, mediaController);
            //关闭扫描音乐对话框
            if(progressDialog!=null){
                progressDialog.dismiss();
                drawLayout.closeDrawer(menuContainer);
            }
        }

        @Override
        public void onExtrasChanged(Bundle extras) {
            String playmode = extras.getString("playmode");
            if (!TextUtils.isEmpty(playmode)) {
                //设置一下当前播放模式的突出显示
                ivAll.setEnabled(true);
                ivOne.setEnabled(true);
                ivRandom.setEnabled(true);
                PlayMode mode = PlayMode.valueOf(playmode);
                switch (mode) {
                    case REPETA_ALL:
                        ivAll.setEnabled(false);
                        break;
                    case REPETA_ONE:
                        ivOne.setEnabled(false);
                        break;
                    case RANDOM:
                        ivRandom.setEnabled(false);
                        break;
                }
            }
        }
    };

    /**
     * 同步底部播放控制栏信息
     *
     * @param metadata
     */
    private void updateBottomInfo(MediaMetadata metadata) {
        titleTv.setText(metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE));
        artistTv.setText(metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE));
        Picasso.with(this).load(metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI)).into(littleConverIv);
    }


    //菜单栏click事件
    @OnClick(R.id.tv_udate_volume)
    public void ajvolumn(View view) {
        final AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int streamVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int streamMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("音量调节");
        SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(streamMaxVolume);
        seekBar.setProgress(streamVolume);
        builder.setView(seekBar);
        builder.create().show();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                //AudioManager.FLAG_PLAY_SOUND:调整音量的时候让音乐声音跟着播放出来
                //FLAG_SHOW_UI:调整音量的时候讲系统音量调节框展示出来
                am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @OnClick(R.id.tv_quit_play)
    public void quitApp(View view) {
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @OnClick({R.id.tv_repeat_all, R.id.tv_repeat_one, R.id.tv_random})
    public void setPmode(View view) {
        PlayMode mode = PlayMode.REPETA_ALL;
        switch (view.getId()) {
            case R.id.tv_repeat_all:
                //列表循环模式
                mode = PlayMode.REPETA_ALL;
                break;
            case R.id.tv_repeat_one:
                //单曲循环
                mode = PlayMode.REPETA_ONE;
                break;
            case R.id.tv_random:
                //随机
                mode = PlayMode.RANDOM;
                break;
        }
        //通知给service的信使，播放模式要改变了
        Bundle bundle = new Bundle();
        bundle.putString("playmode", mode.name());
        mediaController.getTransportControls().sendCustomAction("pm", bundle);
    }

    /**
     * 刷新音乐列表
     * @param view
     */
    @OnClick(R.id.tv_flush_list)
    public void flushMusic(View view){
        //发送一个行为
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("扫描音乐文件");
        progressDialog.show();
        mediaController.getTransportControls().sendCustomAction("flushMusic",null);
    }
}
