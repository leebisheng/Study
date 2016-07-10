package com.leebisheng.studyservice;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by lee on 2016/7/7.
 */
public class FirstService extends IntentService
{
    public FirstService()
    {
        super("FirstService");
    }
    public FirstService(String name) {
        super(name);
    }
    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.i("LeeLog","onHandleIntent");
        printLog();
    }
    public void printLog ()
    {
        Log.i("LeeLog","in printLog");
        for(int i=0;i<100;i++)
        {
            try
            {
                Thread.sleep(1000);
                Log.i("LeeLog",i+"");
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i("LeeLog","onStart");
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate()
    {

        Log.i("LeeLog","onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("LeeLog","onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy()
    {
        Log.i("LeeLog","onDestroy");
        super.onDestroy();
    }
    //------------------------------------------------------------
    //接口对象
    IBinder mMyBinder=new MyBinder();
    //定义IBinder接口实现
    class MyBinder extends Binder
    {
        public String helloWorld(String name)
        {
            return "Your nams is "+name;
        }

        public FirstService getService()
        {
            return FirstService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        //暴露IBinder接口定义
        Log.i("LeeLog","onBind");
        return mMyBinder;
        //return super.onBind(intent);
    }


}
