package cn.itcast.wh.mdmusic2.enigee;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Administrator on 2015/9/3.
 * <p/>
 * ͨ����MediaPlayer�ķ�װ�����ж�������Ŀ�Ĳ��ſ���
 */
public class LocalPlayer {

    MediaPlayer mediaPlayer;

    private IMusicCompletedListener iMusicCompletedListener;

    //����һ���ص������ṩ��service,��service֪�������������֮�󣬸ü���������һ�׸���
    public interface IMusicCompletedListener {
        public void OnMusicCompleted(LocalPlayer localPlayer);
    }

    public void setOnMusicCompletedListener(IMusicCompletedListener iMusicCompletedListener) {
        this.iMusicCompletedListener = iMusicCompletedListener;
    }

    /**
     * ��������
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
     * ��ͣ����
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
     * ������������
     *
     * @return
     */
    public long start() {
        MediaPlayer mp = getMediaPlayer();
        mp.start();
        return mp.getCurrentPosition();
    }

    /**
     * �ж��Ƿ����ڲ���
     *
     * @return
     */
    public boolean isPlaying() {
        return getMediaPlayer().isPlaying();
    }

    /**
     * ����ģʽ:ȡ��һ��MediaPlayer����
     *
     * @return
     */
    private MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        //����һ������Ļص�����
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
