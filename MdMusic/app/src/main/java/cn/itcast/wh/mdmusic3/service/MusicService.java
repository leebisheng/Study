package cn.itcast.wh.mdmusic3.service;

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
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.wh.mdmusic3.engine.LocalPlayer;
import cn.itcast.wh.mdmusic3.engine.MusicProvider;
import cn.itcast.wh.mdmusic3.util.BeanHelper;

/**
 * Created by Administrator on 2015/9/7.
 * <p/>
 * 1:维护音乐播放控制
 */
public class MusicService extends Service {

    private LocalPlayer localPlayer;

    private MusicProvider musicProvider;

    private MediaSession mediaSession;

    private List<MediaMetadata> mediaMetadataList = new ArrayList<MediaMetadata>();

    //当前正在播放的歌曲
    private MediaMetadata currentMusic;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSession(this, getClass().getSimpleName());
        mediaSession.setCallback(sessionCallback);
        localPlayer = new LocalPlayer();
        musicProvider = new MusicProvider(this);
        new MusicQueryTask().execute();
    }


    private class MusicQueryTask extends AsyncTask<Void, Void, List<MediaMetadata>> {

        @Override
        protected List<MediaMetadata> doInBackground(Void... voids) {
            return musicProvider.loadMusic();
        }


        @Override
        protected void onPostExecute(List<MediaMetadata> list) {
            MusicService.this.mediaMetadataList = list;
            //歌曲列表加载完毕之后，通知界面加载歌曲列表
            mediaSession.setQueue(BeanHelper.convertMMtoMQ(list));
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new MyTokenBinder();
    }


    public class MyTokenBinder extends Binder {
        /**
         * 获取到由受控端产生的token
         *
         * @return
         */
        public MediaSession.Token getToken() {
            return mediaSession.getSessionToken();
        }
    }


    private android.media.session.MediaSession.Callback sessionCallback = new MediaSession.Callback() {
        @Override
        public void onPlay() {
            Log.d("bobo", "onPlay...");
//            String path = "/mnt/sdcard/Music/01.m4a";
//            localPlayer.play(path);
            long position = localPlayer.continuPlay();
            PlaybackState.Builder builder = new PlaybackState.Builder();
            builder.setState(PlaybackState.STATE_PLAYING, position, 1.0f);
            mediaSession.setPlaybackState(builder.build());
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            MediaMetadata music = BeanHelper.findMusicById(mediaId, mediaMetadataList);
            if (music != null) {
                play(music);
            }
        }

        @Override
        public void onPause() {
            long position = localPlayer.pause();
            PlaybackState.Builder builder = new PlaybackState.Builder();
            builder.setState(PlaybackState.STATE_PAUSED, position, 1.0f);
            mediaSession.setPlaybackState(builder.build());
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
        }
    };

    /**
     * 播放指定音乐
     *
     * @param mediaMetadata
     */
    public void play(MediaMetadata mediaMetadata) {
        this.currentMusic = mediaMetadata;
        Uri uriPath = Uri.parse(mediaMetadata.getString(MediaMetadata.METADATA_KEY_ART_URI));
        long position = localPlayer.play(uriPath.getPath());
        PlaybackState.Builder builder = new PlaybackState.Builder();
        builder.setState(PlaybackState.STATE_PLAYING, position, 1.0f);
        mediaSession.setPlaybackState(builder.build());
        //通知一下控制端歌曲播放变化了，歌曲的播放状态也变化了
        mediaSession.setMetadata(mediaMetadata);
    }
}
