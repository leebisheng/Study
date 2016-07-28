package cn.itcast.wh.mdmusic2.enigee;

import android.media.MediaMetadata;

import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2015/9/4.
 * <p/>
 * 当正在播放的音乐播放完毕的时，即将要播放的是哪一首音乐
 */
public enum PlayMode {

    REPETA_ALL {
        @Override
        public int getNext(int total, int curIndex) {
            if (curIndex == total - 1) {
                return 0;
            }
            return curIndex + 1;
        }
    }, REPETA_ONE {
        @Override
        public int getNext(int total, int curIndex) {
            return curIndex;
        }
    }, RANDOM {
        @Override
        public int getNext(int total, int curIndex) {
            return new Random().nextInt(total);
        }
    };

    public abstract int getNext(int total, int curIndex);


    /**
     * 根据当前播放模式获取下一首要播放的曲目
     *
     * @param mediaMetadataList
     * @param currentMusic
     * @return
     */
    public MediaMetadata getNext(List<MediaMetadata> mediaMetadataList, MediaMetadata currentMusic) {
        int total = mediaMetadataList.size();
        int curIndex = mediaMetadataList.indexOf(currentMusic);
        int next = getNext(total, curIndex);
        return mediaMetadataList.get(next);
    }
}
