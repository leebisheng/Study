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
 * 通过系统内容提供者，查询出所有音乐资源
 */
public class MusicProvider {

    //sd卡目录uri访问路径
    public static final Uri MEDIA_URI = Media.EXTERNAL_CONTENT_URI;
    private Context context;

    //MediaMetadata是系统提供存储媒体资源的封装好的类，并且mediaSession框架音乐媒体信息也是用的这个类型的对象
    public List<MediaMetadata> mediaMetadataList = new ArrayList<MediaMetadata>();

    //这是我们要查询的列(字段)信息
    public static final String[] projections = new String[]{Media._ID, Media.TITLE, Media.ALBUM, Media.ARTIST,
            Media.DATA, Media.DURATION};

    public List<MediaMetadata> getMediaMetadataList() {
        return mediaMetadataList;
    }

    public MusicProvider(Context context) {
        this.context = context;
    }

    /**
     * 加载音乐列表
     */
    public List<MediaMetadata> loadMusic() {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MEDIA_URI, projections, null, null, null);

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();

        while (cursor.moveToNext()) {
            //列的对应信息
            // 路径      Media.DATA       MediaMetadata.METADATA_KEY_ART_URI
            // 持续时间  Media.DURATION   MediaMetadata.METADATA_KEY_DURATION
            // 歌曲名称  Media.TITLE      MediaMetadata.METADATA_KEY_DISPLAY_TITLE
            // 专辑名称  Media.ALBUM      MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION
            // 艺术家    Media.ARTIST     MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE
            // id       Media._ID       MediaMetadata.METADATA_KEY_MEDIA_ID
            // 音乐的封面                 MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI
            //取出列信息，封装成MediaMetadata，并加入到集合当中
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
            //我们需要将歌曲的封面图片读取出来，然后存进cache目录,文件名就以为id命名
            //使用MediaMetadataRetriever去读取音乐封面的图片
            File targetFile = new File(context.getCacheDir(), id);
            Uri iconUri = null;
            if (targetFile.exists() || getPicture(metadataRetriever, musicpath, targetFile)) {
                iconUri = Uri.fromFile(targetFile);
            } else {
                //将系统res资源转换成uri路径
                iconUri = Uri.parse("android:resoure//" + context.getPackageName() + "/drawable/" + R.drawable.default_corver);
            }
            builder.putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, iconUri.toString());
            MediaMetadata mediaMetadata = builder.build();
            //过滤一些不想要看到的歌曲信息
            if (duration > 10000) {
                mediaMetadataList.add(mediaMetadata);
            }
        }
        Log.d("zoubo", "mediaMetadataList-size:  " + mediaMetadataList.size());
        return mediaMetadataList;
    }

    ;

    /**
     * 读取音乐封面图片
     *
     * @param metadataRetriever
     * @param musicpath
     * @param targetFile
     */
    private boolean getPicture(MediaMetadataRetriever metadataRetriever, String musicpath, File targetFile) {
        //java7版本之后的一个性特性，这么写的话，就java会自动帮我们进行流关闭的操作
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
     * 扫描音乐列表变化,重新加载变化后的音乐列表
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
         * 当MediaScannerConnection对象连接成功
         */
        @Override
        public void onMediaScannerConnected() {
            //调用msc.scanfile()去扫描已有文件，如果发现文件不在，就会从内容提供者当中删除掉
            if (mediaMetadataList.size() > 0) {
                for (MediaMetadata mediaMetadata : mediaMetadataList) {
                    scanFileCount++;
                    msc.scanFile(Uri.parse(mediaMetadata.getString(MediaMetadata.METADATA_KEY_ART_URI)).getPath(), "audio/*");
                }
            }
            //调用msc.scanfile()去扫描一个文件，当扫描完成后会自动加入到内容提供者当中
            scanFile(Environment.getExternalStorageDirectory());
        }

        /**
         * 扫描sd卡上的所有audio文件
         * msc.scanFile()它只能扫描文件，不能扫描目录
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
                //一直到扫描到文件
                //过滤一下文件(.m4a和.mp3)
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
         * 每发现一个文件，就会执行一次
         * 每扫描完成一次的时候,会自动将扫描出来新的文件加入到mediaStore系统内容提供者中
         * 在一个子线程当中运行的
         *
         * @param path
         * @param uri
         */
        @Override
        public void onScanCompleted(String path, Uri uri) {
            //每一次当扫描完成，会将扫描到的当前文件加入到系统内容提供者当中
            //需要判断全部扫描完毕
            Log.d("zoubo", "onScanCompleted:--" + path);
            scanFileCount--;
            if (scanFileCount == 0) {
                //全部扫描完毕(当前还在子线程当中)
                if (mediaMetadataList != null) {
                    mediaMetadataList.clear();
                    loadMusic();
                    //列表重新加载了，使用runnable回调给调用者
                    reloadFinishRunnale.run();
                }
                msc.disconnect();
            }
        }
    }
}
