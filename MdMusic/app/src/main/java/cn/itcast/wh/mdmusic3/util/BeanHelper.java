package cn.itcast.wh.mdmusic3.util;

import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.session.MediaSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/7.
 */
public class BeanHelper {

    /**
     * 根据MediaMetadata集合可以获取到一个MediaSession.QueueItem的集合，而这个集合就是我们mediasession所需要的
     *
     * @param list
     * @return
     */
    public static List<MediaSession.QueueItem> convertMMtoMQ(List<MediaMetadata> list) {
        List<MediaSession.QueueItem> result = new ArrayList<MediaSession.QueueItem>();
        for (MediaMetadata mediaMetadata : list) {
            MediaDescription description = mediaMetadata.getDescription();
            long id = Long.parseLong(description.getMediaId());
            MediaSession.QueueItem item = new MediaSession.QueueItem(description, id);
            result.add(item);
        }
        return result;
    }


    /**
     * 根据曲目id查找对应的音乐资源
     * @param mediaId
     * @param list
     * @return
     */
    public static MediaMetadata findMusicById(String mediaId,List<MediaMetadata> list){
        MediaMetadata result = null;
        for (MediaMetadata mediaMetadata : list) {
             if(mediaMetadata.getDescription().getMediaId().equals(mediaId)){
                 result = mediaMetadata;
                 break;
             }
        }
        return result;
    }

    /**
     * 查找指定音乐在集合当中的索引位置
     * @param queueItems
     * @param mediaMetadata
     * @return
     */
    public static int findIndexByMusic(List<MediaSession.QueueItem> queueItems,MediaMetadata mediaMetadata){
        int index = -1;
        for (int i = 0; i < queueItems.size(); i++) {
            MediaSession.QueueItem queueItem = queueItems.get(i);
            if(queueItem.getDescription().getMediaId().equals(mediaMetadata.getDescription().getMediaId())){
                index = i;
                break;
            }
        }
        return index;
    }
}
