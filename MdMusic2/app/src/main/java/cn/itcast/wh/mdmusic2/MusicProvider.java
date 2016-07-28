package cn.itcast.wh.mdmusic2;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.provider.MediaStore.Audio.Media;
import android.util.Log;

/**
 * Created by Administrator on 2015/9/3.
 * <p/>
 * ͨ��ϵͳ�����ṩ�ߣ���ѯ������������Դ
 */
public class MusicProvider {

    //sd��Ŀ¼uri����·��
    public static final Uri MEDIA_URI = Media.EXTERNAL_CONTENT_URI;
    private Context context;

    //MediaMetadata��ϵͳ�ṩ�洢ý����Դ�ķ�װ�õ��࣬����mediaSession�������ý����ϢҲ���õ�������͵Ķ���
    public List<MediaMetadata> mediaMetadataList = new ArrayList<MediaMetadata>();

    //��������Ҫ��ѯ����(�ֶ�)��Ϣ
    public static final String[] projections = new String[]{Media._ID, Media.TITLE, Media.ALBUM, Media.ARTIST,
            Media.DATA, Media.DURATION};

    public List<MediaMetadata> getMediaMetadataList() {
        return mediaMetadataList;
    }

    public MusicProvider(Context context) {
        this.context = context;
    }

    /**
     * ���������б�
     */
    public List<MediaMetadata> loadMusic() {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MEDIA_URI, projections, null, null, null);

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();

        while (cursor.moveToNext()) {
            //�еĶ�Ӧ��Ϣ
            // ·��      Media.DATA       MediaMetadata.METADATA_KEY_ART_URI
            // ����ʱ��  Media.DURATION   MediaMetadata.METADATA_KEY_DURATION
            // ��������  Media.TITLE      MediaMetadata.METADATA_KEY_DISPLAY_TITLE
            // ר������  Media.ALBUM      MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION
            // ������    Media.ARTIST     MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE
            // id       Media._ID       MediaMetadata.METADATA_KEY_MEDIA_ID
            // ���ֵķ���                 MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI
            //ȡ������Ϣ����װ��MediaMetadata�������뵽���ϵ���
            String id = cursor.getString(cursor.getColumnIndex(Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(Media.TITLE));
            String album = cursor.getString(cursor.getColumnIndex(Media.ALBUM));
            String artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
            String musicpath = cursor.getString(cursor.getColumnIndex(Media.DATA));
            long duration = cursor.getLong(cursor.getColumnIndex(Media.DURATION));

            MediaMetadata.Builder builder = new MediaMetadata.Builder();
            builder.putString(MediaMetadata.METADATA_KEY_MEDIA_ID, id);
            builder.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, title);
            builder.putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION, album);
            builder.putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, artist);
            Uri uripath = Uri.parse(musicpath);
            builder.putString(MediaMetadata.METADATA_KEY_ART_URI, uripath.toString());
            builder.putLong(MediaMetadata.METADATA_KEY_DURATION, duration);
            //������Ҫ�������ķ���ͼƬ��ȡ������Ȼ����cacheĿ¼,�ļ�������Ϊid����
            //ʹ��MediaMetadataRetrieverȥ��ȡ���ַ����ͼƬ
            File targetFile = new File(context.getCacheDir(), id);
            Uri iconUri = null;
            if (targetFile.exists() || getPicture(metadataRetriever, musicpath, targetFile)) {
                iconUri = Uri.fromFile(targetFile);
            } else {
                //��ϵͳres��Դת����uri·��
                iconUri = Uri.parse("android:resoure//" + context.getPackageName() + "/drawable/" + R.drawable.default_corver);
            }
            builder.putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, iconUri.toString());
            MediaMetadata mediaMetadata = builder.build();
            //����һЩ����Ҫ�����ĸ�����Ϣ
            if (duration > 10000) {
                mediaMetadataList.add(mediaMetadata);
            }
        }
        Log.d("zoubo", "mediaMetadataList-size:  " + mediaMetadataList.size());
        return mediaMetadataList;
    }

    ;

    /**
     * ��ȡ���ַ���ͼƬ
     *
     * @param metadataRetriever
     * @param musicpath
     * @param targetFile
     */
    private boolean getPicture(MediaMetadataRetriever metadataRetriever, String musicpath, File targetFile) {
        //java7�汾֮���һ�������ԣ���ôд�Ļ�����java���Զ������ǽ������رյĲ���
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            metadataRetriever.setDataSource(musicpath);
            byte[] bytes = metadataRetriever.getEmbeddedPicture();
            if (bytes == null && bytes.length == 0) {
                return false;
            } else {
                fos.write(bytes);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    ;

    private MediaScannerConnection msc;

    /**
     * ɨ�������б�仯,���¼��ر仯��������б�
     */
    public void reloadMusic(Runnable reloadFinishRunnale) {
        msc = new MediaScannerConnection(context, new MyMediascannerConnectionCient(reloadFinishRunnale));
        msc.connect();
    }

    private class MyMediascannerConnectionCient implements MediaScannerConnection.MediaScannerConnectionClient {

        private Runnable reloadFinishRunnale;
        private int scanFileCount = 0;

        public MyMediascannerConnectionCient(Runnable reloadFinishRunnale) {
            this.reloadFinishRunnale = reloadFinishRunnale;
        }

        /**
         * ��MediaScannerConnection�������ӳɹ�
         */
        @Override
        public void onMediaScannerConnected() {
            //����msc.scanfile()ȥɨ�������ļ�����������ļ����ڣ��ͻ�������ṩ�ߵ���ɾ����
            if (mediaMetadataList.size() > 0) {
                for (MediaMetadata mediaMetadata : mediaMetadataList) {
                    scanFileCount++;
                    msc.scanFile(Uri.parse(mediaMetadata.getString(MediaMetadata.METADATA_KEY_ART_URI)).getPath(), "audio/*");
                }
            }
            //����msc.scanfile()ȥɨ��һ���ļ�����ɨ����ɺ���Զ����뵽�����ṩ�ߵ���
            scanFile(Environment.getExternalStorageDirectory());
        }

        /**
         * ɨ��sd���ϵ�����audio�ļ�
         * msc.scanFile()��ֻ��ɨ���ļ�������ɨ��Ŀ¼
         *
         * @param file
         */
        private void scanFile(File file) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File file1 : files) {
                    scanFile(file1);
                }
            } else {
                //һֱ��ɨ�赽�ļ�
                //����һ���ļ�(.m4a��.mp3)
                if (file.getPath().lastIndexOf(".") != -1) {
                    String houzui = file.getPath().substring(file.getPath().lastIndexOf("."));
                    Log.d("zoubo", "file type:" + houzui);
                    if(".mp3".equals(houzui) || ".m4a".equals(houzui)){
                        scanFileCount++;
                        msc.scanFile(file.getPath(), "audio/*");
                    }
                }
            }
        }

        /**
         * ÿ����һ���ļ����ͻ�ִ��һ��
         * ÿɨ�����һ�ε�ʱ��,���Զ���ɨ������µ��ļ����뵽mediaStoreϵͳ�����ṩ����
         * ��һ�����̵߳������е�
         *
         * @param path
         * @param uri
         */
        @Override
        public void onScanCompleted(String path, Uri uri) {
            //ÿһ�ε�ɨ����ɣ��Ὣɨ�赽�ĵ�ǰ�ļ����뵽ϵͳ�����ṩ�ߵ���
            //��Ҫ�ж�ȫ��ɨ�����
            Log.d("zoubo", "onScanCompleted:--" + path);
            scanFileCount--;
            if (scanFileCount == 0) {
                //ȫ��ɨ�����(��ǰ�������̵߳���)
                if (mediaMetadataList != null) {
                    mediaMetadataList.clear();
                    loadMusic();
                    //�б����¼����ˣ�ʹ��runnable�ص���������
                    reloadFinishRunnale.run();
                }
                msc.disconnect();
            }
        }
    }
}
