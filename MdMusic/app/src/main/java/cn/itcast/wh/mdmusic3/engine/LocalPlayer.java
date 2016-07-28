package cn.itcast.wh.mdmusic3.engine;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Administrator on 2015/9/7.
 *
 * 需求：维护音乐播放控制,通过封装mediaPlayer进行播放控制的
 *
 * 分析：播放、暂停、定位播放、判断当前是否正在播放
 *
 */
public class LocalPlayer {

    private MediaPlayer mediaPlayer = null;

    /**
     * 播放
     * @param path
     * @throws IOException
     */
    public long play(String path)  {
        MediaPlayer mp = getMediaPlayer();
        mp.reset();
        try {
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mp.getCurrentPosition();
    }

    /**
     * 暂停播放
     */
    public long pause(){
        MediaPlayer mp = getMediaPlayer();
        mp.pause();
        return mp.getCurrentPosition();
    }

    /**
     * 定位播放
     * @param pos
     */
    public long seekTo(int pos){
        MediaPlayer mp = getMediaPlayer();
        mp.seekTo(pos);
        return mp.getCurrentPosition();
    }


    /**
     * 继续播放
     */
    public long continuPlay(){
        MediaPlayer mp = getMediaPlayer();
        mp.start();
        return mp.getCurrentPosition();
    }


    /***
     * 是否正在播放
     * @return
     */
    public boolean isPlaying(){
        MediaPlayer mp = getMediaPlayer();
        return mp.isPlaying();
    }

    /**
     * 获取MediaPlayer对象
     * @return
     */
    public MediaPlayer getMediaPlayer(){
        if(mediaPlayer==null){
            mediaPlayer = new MediaPlayer();
        }
        return mediaPlayer;
    }
}
