package cn.itcast.wh.mdmusic3.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.itcast.wh.mdmusic3.R;

/**
 * Created by Administrator on 2015/9/7.
 * <p/>
 * 加载音乐播放列表
 * <p/>
 * 路径、持续时间、歌曲名称、专辑名称、演唱者、id、歌曲封面
 * <p/>
 * 将信息封装到MediaMetadata类型
 */
public class MusicProvider {

    private Context context;
    private Uri MEDIA_URI = Media.EXTERNAL_CONTENT_URI;

    private List<MediaMetadata> mediaMetadataList = new ArrayList<MediaMetadata>();

    //这是我们要查询的列(字段)信息
    public static final String[] projections = new String[]{Media._ID, Media.TITLE, Media.ALBUM, Media.ARTIST,
            Media.DATA, Media.DURATION};

    public MusicProvider(Context context) {
        this.context = context;
    }

    /**
     * 加载EXTERNAL_CONTENT_URI(sdcard)音乐列表
     *
     * @return
     */
    public List<MediaMetadata> loadMusic() {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MEDIA_URI, projections, null, null, null);

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

        //把获取出来的音乐封面图片存储在data/data/../cache
        while (cursor.moveToNext()) {
            //列的对应信息
            // 路径      Media.DATA       MediaMetadata.METADATA_KEY_ART_URI
            // 持续时间  Media.DURATION   MediaMetadata.METADATA_KEY_DURATION
            // 歌曲名称  Media.TITLE      MediaMetadata.METADATA_KEY_DISPLAY_TITLE
            // 专辑名称  Media.ALBUM      MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION
            // 艺术家    Media.ARTIST     MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE
            // id       Media._ID         MediaMetadata.METADATA_KEY_MEDIA_ID
            // 音乐的封面                 MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI
            MediaMetadata.Builder builder = new MediaMetadata.Builder();

            //取出我们需要的歌曲信息，然后存入一个mediaMetadata对象中即可
            String id = cursor.getString(cursor.getColumnIndex(Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(Media.TITLE));
            String album = cursor.getString(cursor.getColumnIndex(Media.ALBUM));
            String artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
            String musicPath = cursor.getString(cursor.getColumnIndex(Media.DATA));
            long duration = cursor.getLong(cursor.getColumnIndex(Media.DURATION));
            //设置一下信息
            builder.putString(MediaMetadata.METADATA_KEY_MEDIA_ID, id);
            builder.putLong(MediaMetadata.METADATA_KEY_DURATION, duration);
            builder.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, title);
            builder.putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION, album);
            builder.putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, artist);
            Uri uriPath = Uri.parse(musicPath);
            builder.putString(MediaMetadata.METADATA_KEY_ART_URI, uriPath.toString());
            File file = new File(context.getCacheDir(), id);
            Uri iconUri = null;
            if(file.exists() || getPicture(mediaMetadataRetriever, musicPath, file)){
                //用一个字段存进MediaMetadata对象中
                iconUri = Uri.fromFile(file);
            }else{
                //将系统res资源转换成uri路径
                iconUri = Uri.parse("android:resoure//" + context.getPackageName() + "/drawable/" + R.drawable.default_corver);
            }
            builder.putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI,iconUri.toString());
            MediaMetadata metadata = builder.build();
            mediaMetadataList.add(metadata);
        }
        cursor.close();
        mediaMetadataRetriever.release();
        Log.d("zb","mediaMetadataList-size: "+mediaMetadataList.size());
        return mediaMetadataList;
    }

    /**
     * 读取音乐资源包含的图片信息
     *
     * @param retriever
     * @param musicPath
     * @param targetFile
     * @return
     */
    public boolean getPicture(MediaMetadataRetriever retriever, String musicPath, File targetFile) {
        //java7版本之后有这么一个新的特性
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            retriever.setDataSource(musicPath);
            byte[] bytes = retriever.getEmbeddedPicture();
            if (bytes == null || bytes.length == 0) {
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
}
