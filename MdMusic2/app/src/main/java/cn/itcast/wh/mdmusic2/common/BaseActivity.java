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
 * 1:�ڶ�Ӧ���������ڷ����ڣ��������󶨡�������ǵ����ַ���
 * 2������Ϊ����activity�Ļ��࣬��Ҫ��mediaSession��ܵ�mediaController����������еĽ����������������ǽ��в��ſ���
 *
 *    ---activity��service�Ľ�����ͬʱOnbinder����
 *    ---mediaSession��mediaController��Ҫһ��Token������һһ��ϵ�Ķ�Ӧ
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
     * �󶨷���
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
         * �����ǵķ���󶨳ɹ���ʱ��
         * @param componentName
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MyTokenBinder myTokenBinder = (MusicService.MyTokenBinder) iBinder;
            MediaSession.Token token = myTokenBinder.getToken();
            //����token�����ǾͿ����õ�MediController������
            MediaController mediaController = new MediaController(BaseActivity.this, token);
            //1����mediaController�������protected�ĳ�Ա��������ô���е�����Ϳ���ʹ��mediaController�����˰�..
            //���ǣ����ַ�ʽ��������һ����һ���������������������࣬��Ҳ���õ�mediaController���ǲ��ǾͲ��ð��˰�..
            //2: ���������ģʽ��ͨ���ӿڳ���ķ�ʽ����������Ҫ�õ�mediaController�Ľ�����������Ϊ�ӿ�ʵ�������Ȼ����baseActivity��
            //ʹ�ü���ȫ��������������
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
