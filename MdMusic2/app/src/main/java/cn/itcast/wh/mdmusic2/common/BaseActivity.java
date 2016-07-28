package cn.itcast.wh.mdmusic2.common;

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

import cn.itcast.wh.mdmusic2.service.MusicService;

/**
 * Created by Administrator on 2015/9/3.
 *
 * 1:在对应的声明周期方法内，启动、绑定、解绑我们的音乐服务
 * 2：它作为所有activity的基类，需要将mediaSession框架的mediaController方传输给所有的界面容器，方便他们进行播放控制
 *
 *    ---activity与service的交互，同时Onbinder来的
 *    ---mediaSession和mediaController需要一个Token来进行一一关系的对应
 *
 */
public class BaseActivity extends Activity{

    private Intent serviceIntent = null;

    private List<IMediaControllerConsumer> consumers = new ArrayList<IMediaControllerConsumer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);
    }

    protected void addConsumer(IMediaControllerConsumer consumer){
        consumers.add(consumer);
    }


    /**
     * 绑定服务
     */
    @Override
    protected void onStart() {
        super.onStart();
        bindService(serviceIntent,conn,0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(conn);
    }

    private ServiceConnection conn = new ServiceConnection() {

        /**
         * 当我们的服务绑定成功的时候
         * @param componentName
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MyTokenBinder myTokenBinder = (MusicService.MyTokenBinder) iBinder;
            MediaSession.Token token = myTokenBinder.getToken();
            //有了token，我们就可以拿到MediController对象了
            MediaController mediaController = new MediaController(BaseActivity.this, token);
            //1：讲mediaController对象定义成protected的成员变量，那么所有的子类就可以使用mediaController对象了啊..
            //但是：这种方式不够灵活，万一我有一个界面容器不是它的子类，我也想拿到mediaController，是不是就不好办了啊..
            //2: 建造者设计模式：通过接口抽象的方式，将所有需要拿到mediaController的界面容器都作为接口实现类对象，然后在baseActivity中
            //使用集合全部集中起来管理。
            setMediaController(mediaController);
            for (IMediaControllerConsumer consumer : consumers) {
                 consumer.OnObtainMediaController(mediaController);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            for (IMediaControllerConsumer consumer : consumers) {
                consumer.OnObtainMediaController(getMediaController());
            }
        }
    };
}
