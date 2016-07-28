package cn.itcast.wh.mdmusic3.common;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.wh.mdmusic3.service.MusicService;

/**
 * Created by Administrator on 2015/9/7.
 * <p/>
 * 启动服务、绑定服务、解绑服务
 */
public class BaseActivity extends Activity {

    private Intent serviceIntent;

    private List<IMediaControllerConsumer> consumers = new ArrayList<IMediaControllerConsumer>();

    /**
     * 将一个IMediaControllerConsumer实现类对象添加进list
     * @param mediaControllerConsumer
     */
    public void addConsumer(IMediaControllerConsumer mediaControllerConsumer){
        consumers.add(mediaControllerConsumer);
    }

    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //启动服务
        serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //绑定服务
        bindService(serviceIntent, conn, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //解绑服务
        for (IMediaControllerConsumer consumer : consumers) {
            consumer.onReleasedMediaController(mediaController);
        }
        unbindService(conn);
    }


    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MyTokenBinder myTokenBinder = (MusicService.MyTokenBinder) iBinder;
            MediaSession.Token token = myTokenBinder.getToken();
            mediaController = new MediaController(BaseActivity.this, token);
            //就拿到了所有的控制台
            for (IMediaControllerConsumer consumer : consumers) {
                consumer.onObtainMediaController(mediaController);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
}
