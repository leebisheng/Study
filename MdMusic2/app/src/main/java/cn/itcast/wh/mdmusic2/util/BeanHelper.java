package cn.itcast.wh.mdmusic2.util;

import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.session.MediaSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/3.
 */
public class BeanHelper {

    /**
     * ��MediaMetadata���ݼ���ת����mediasession��Ҫ��QueueItem����
     *
     * @param list
     * @return
     */
    public static List<MediaSession.QueueItem> convertMMtoQM(List<MediaMetadata> list) {
        List<MediaSession.QueueItem> result = new ArrayList<MediaSession.QueueItem>();
        for (MediaMetadata mediaMetadata : list) {
            MediaDescription description = mediaMetadata.getDescription();
            long id = Long.parseLong(description.getMediaId());
            MediaSession.QueueItem queueItem = new MediaSession.QueueItem(description, id);
            result.add(queueItem);
        }
        return result;
    }

    ;

    /**
     * ����id����ָ������
     *
     * @param list
     * @param mediaId
     * @return
     */
    public static MediaMetadata findMusicById(List<MediaMetadata> list, String mediaId) {
        MediaMetadata target = null;
        for (MediaMetadata mediaMetadata : list) {
            if (mediaId.equals(mediaMetadata.getDescription().getMediaId())) {
                target = mediaMetadata;
                break;
            }
        }
        return target;
    }

    /**
     * ����������queue���ϵ��е�����
     *
     * @param queueItems
     * @param mediaMetadata
     * @return
     */
    public static int findIndexInQueue(List<MediaSession.QueueItem> queueItems, MediaMetadata mediaMetadata) {
        int index = -1;
        for (int i = 0; i < queueItems.size(); i++) {
            MediaSession.QueueItem queueItem = queueItems.get(i);
            if (queueItem.getDescription().getMediaId().equals(mediaMetadata.getDescription().getMediaId())) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * ���غ���ʱ��ֵת���ġ�00:00�����ַ�����ʽ
     *
     * @param time
     * @return
     */
    public static String getTime(long time) {
        int m = (int) (time / 1000 / 60);
        int s = (int) (time / 1000 % 60);
        return String.format("%02d:%02d", m, s);
    }

    ;


    /**
     * 查找上一首曲目
     *
     * @param list
     * @param currentMusic
     * @return
     */
    public static MediaMetadata findPreMusic(List<MediaMetadata> list, MediaMetadata currentMusic) {
        MediaMetadata result = null;
        int curIndex = list.indexOf(currentMusic);
        if (curIndex > 0) {
            result = list.get(curIndex - 1);
        }
        return result;
    }

    /**
     * 查找下一首曲目
     *
     * @param list
     * @param currentMusic
     * @return
     */
    public static MediaMetadata findNextMusic(List<MediaMetadata> list, MediaMetadata currentMusic) {
        MediaMetadata result = null;
        int curIndex = list.indexOf(currentMusic);
        if (curIndex < list.size() - 1) {
            result = list.get(curIndex + 1);
        }
        return result;
    }
}
