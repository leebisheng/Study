package cn.itcast.wh.mdmusic2.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import cn.itcast.wh.mdmusic2.MusicProvider;
import cn.itcast.wh.mdmusic2.enigee.LocalPlayer;
import cn.itcast.wh.mdmusic2.enigee.PlayMode;
import cn.itcast.wh.mdmusic2.util.BeanHelper;

/**
 * Created by Administrator on 2015/9/3.
 * <p/>
 * 1:����ά����������
 * ---localPlayerȥֱ��ִ�����ֵĲ��ſ��ƣ����ţ���ͣ����λ�ȵȣ�
 * ---MusicProviderȥ���س�����ϵͳ���ڵ���������
 * 2����ΪmediaSession��ܵ�mediaSession��,�������Խ��������Ĳ��ſأ�ͬʱ�ֿ�������������Ϣ����������
 */
public class MusicService extends Service implements LocalPlayer.IMusicCompletedListener {


    private MediaSession mediaSession;

    private LocalPlayer localPlayer;

    private MusicProvider musicProvider;
    private List<MediaMetadata> mediaMetadataList;
    //��ǰ���ŵ�����
    private MediaMetadata currentMusic;

    private PlayMode playMode = PlayMode.REPETA_ALL;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSession(this, getClass().getSimpleName());
        mediaSession.setCallback(sessionCallback);
        //��������ֵ�ֱ�ӿ���?
        localPlayer = new LocalPlayer();
        //musicProvider��������س����е������б�?
        musicProvider = new MusicProvider(this);
        //������Ҫ��loadMusic�������ֿ��ܻ�ǳ���ʱ�Ĳ�������һ���첽�������..todo
        new MusicQueryTask().execute();
        localPlayer.setOnMusicCompletedListener(this);
    }

    @Override
    public void OnMusicCompleted(LocalPlayer localPlayer) {
        if (mediaMetadataList != null && mediaMetadataList.size() > 0 && currentMusic != null) {
            MediaMetadata next = playMode.getNext(mediaMetadataList, currentMusic);
            play(next);
        }
    }


    /**
     * �첽���������б�
     */
    private class MusicQueryTask extends AsyncTask<Void, Void, List<MediaMetadata>> {

        @Override
        protected List<MediaMetadata> doInBackground(Void... voids) {
            return musicProvider.loadMusic();
        }

        @Override
        protected void onPostExecute(List<MediaMetadata> list) {
            MusicService.this.mediaMetadataList = list;
            //�����������б�֪ͨ��������չʾ�б�
            mediaSession.setQueue(BeanHelper.convertMMtoQM(list));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyTokenBinder();
    }


    public class MyTokenBinder extends Binder {
        /**
         * ����һ������������ͨ��Ibinder�����ȡ��mediaSession��Token
         *
         * @return
         */
        public MediaSession.Token getToken() {
            return mediaSession.getSessionToken();
        }
    }

    /**
     * ���ս����������ڲ������ֵĿ���
     */
    private android.media.session.MediaSession.Callback sessionCallback = new MediaSession.Callback() {
        @Override
        public void onPlay() {
            long position = localPlayer.start();
            PlaybackState.Builder builder = new PlaybackState.Builder();
            builder.setState(PlaybackState.STATE_PLAYING, position, 1.0f);
            builder.setActions(getAavailableAction());
            mediaSession.setPlaybackState(builder.build());
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            //����mediaId���Ҽ����еĸ������в���
            MediaMetadata currentMusic = BeanHelper.findMusicById(mediaMetadataList, mediaId);
            play(currentMusic);
        }

        @Override
        public void onPause() {
            long position = localPlayer.pause();
            //��ǰ����״̬������ȥ
            PlaybackState.Builder builder = new PlaybackState.Builder();
            builder.setState(PlaybackState.STATE_PAUSED, position, 1.0f);
            builder.setActions(getAavailableAction());
            mediaSession.setPlaybackState(builder.build());
        }

        @Override
        public void onSkipToNext() {
            //根据当前歌曲找到下一首歌曲，然后播放
            MediaMetadata nextMusic = BeanHelper.findNextMusic(mediaMetadataList, currentMusic);
            if (nextMusic != null) {
                play(nextMusic);
            }
        }

        @Override
        public void onSkipToPrevious() {
            //根据当前音乐好到上一首歌曲，播放
            MediaMetadata preMusic = BeanHelper.findPreMusic(mediaMetadataList, currentMusic);
            if (preMusic != null) {
                play(preMusic);
            }
        }

        @Override
        public void onSeekTo(long pos) {
            long position = localPlayer.seekTo((int) pos);
            //��ǰ����״̬������ȥ
            PlaybackState.Builder builder = new PlaybackState.Builder();
            if (localPlayer.isPlaying()) {
                builder.setState(PlaybackState.STATE_PLAYING, position, 1.0f);
            } else {
                builder.setState(PlaybackState.STATE_PAUSED, position, 1.0f);
            }
            builder.setActions(getAavailableAction());
            mediaSession.setPlaybackState(builder.build());
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            if (!TextUtils.isEmpty(action) && "pm".equals(action)) {
                String playmode = extras.getString("playmode");
                playMode = PlayMode.valueOf(playmode);
                //����ģʽ������ȥ
                //������ģʽ���������б����棬�õ�ǰ����ģʽ���и�����ʾ
                Bundle bundle = new Bundle();
                bundle.putString("playmode", playMode.name());
                mediaSession.setExtras(bundle);
            }

            //ˢ�������б�
            if (!TextUtils.isEmpty(action) && "flushMusic".equals(action)) {
                musicProvider.reloadMusic(reloadFinishRunnale);
            }
        }
    };

    Runnable reloadFinishRunnale = new Runnable() {
        @Override
        public void run() {
            MusicService.this.mediaMetadataList = musicProvider.getMediaMetadataList();
            //֪ͨ������ˢ��
            mediaSession.setQueue(BeanHelper.convertMMtoQM(MusicService.this.mediaMetadataList));
        }
    };


    /**
     * @param currentMusic
     */
    private void play(MediaMetadata currentMusic) {
        this.currentMusic = currentMusic;
        Uri uripath = Uri.parse(currentMusic.getString(MediaMetadata.METADATA_KEY_ART_URI));
        long position = localPlayer.play(uripath.getPath());
        PlaybackState.Builder builder = new PlaybackState.Builder();
        builder.setState(PlaybackState.STATE_PLAYING, position, 1.0f);
        builder.setActions(getAavailableAction());
        mediaSession.setPlaybackState(builder.build());
        mediaSession.setMetadata(currentMusic);
    }

    /**
     * 获取当前音乐播放状�?�信息当中所能支持的action
     *
     * @return
     */
    private long getAavailableAction() {
        long action = 0;
        // 如果播放队列为空�? �?么都不能�?
        if (mediaMetadataList == null || mediaMetadataList.isEmpty()) {
            return action;
        }
        // 如果当前音乐为空�? play
        if (currentMusic == null) {
            action = action | PlaybackState.ACTION_PLAY;
            action = action | PlaybackState.ACTION_PLAY_FROM_MEDIA_ID;
            return action;
        }
        // 当前音乐的下标，判断是否能上�?首�?�下�?�?
        int curIndex = mediaMetadataList.indexOf(currentMusic);
        if (curIndex > 0) {
            action |= PlaybackState.ACTION_SKIP_TO_PREVIOUS;
        }
        if (curIndex < mediaMetadataList.size() - 1) {
            action |= PlaybackState.ACTION_SKIP_TO_NEXT;
        }
        // 播放的时候才能暂停，暂停的时候才能播�?
        if (localPlayer.isPlaying()) {
            action |= PlaybackState.ACTION_PAUSE;
        } else {
            action |= PlaybackState.ACTION_PLAY;
        }
        return action;
    }
}
