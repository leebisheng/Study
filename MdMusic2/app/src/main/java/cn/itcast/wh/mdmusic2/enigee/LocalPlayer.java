package cn.itcast.wh.mdmusic2.enigee;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Administrator on 2015/9/3.
 * <p/>
 * 通过对MediaPlayer的封装，进行对音乐曲目的播放控制
 */
public class LocalPlayer {

    MediaPlayer mediaPlayer;

    private IMusicCompletedListener iMusicCompletedListener;

    //设置一个回调监听提供给service,让service知道歌曲播放完毕之后，该继续播放哪一首歌曲
    public interface IMusicCompletedListener {
        public void OnMusicCompleted(LocalPlayer localPlayer);
    }

    public void setOnMusicCompletedListener(IMusicCompletedListener iMusicCompletedListener) {
        this.iMusicCompletedListener = iMusicCompletedListener;
    }

    /**
     * 播放音乐
     *
     * @param path
     */
    public long play(String path) {
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
     * 暂停音乐
     *
     * @return
     */
    public long pause() {
        MediaPlayer mp = getMediaPlayer();
        mp.pause();
        return mp.getCurrentPosition();
    }

    public long seekTo(int pos) {
        MediaPlayer mp = getMediaPlayer();
        mp.seekTo(pos);
        return mp.getCurrentPosition();
    }


    /**
     * 继续播放音乐
     *
     * @return
     */
    public long start() {
        MediaPlayer mp = getMediaPlayer();
        mp.start();
        return mp.getCurrentPosition();
    }

    /**
     * 判断是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return getMediaPlayer().isPlaying();
    }

    /**
     * 单例模式:取出一个MediaPlayer对象
     *
     * @return
     */
    private MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        //设置一个对外的回调监听
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(iMusicCompletedListener!=null){
                    iMusicCompletedListener.OnMusicCompleted(LocalPlayer.this);
                }
            }
        });
        return mediaPlayer;
    }
}
