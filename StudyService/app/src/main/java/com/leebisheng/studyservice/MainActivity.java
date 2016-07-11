package com.leebisheng.studyservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Intent _intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.buttonstartservice:
                _intent=StartMyService();
                break;
            case R.id.buttonstopservice:
                if(_intent !=null)
                {
                    StopMyService(_intent);
                }
                else
                {
                    Toast.makeText(this,"No service is started",3000).show();
                }
                break;
            case R.id.buttonbindservice:
                Intent _intent=new Intent(MainActivity.this,FirstService.class);
                bindService(_intent,conn,BIND_AUTO_CREATE);

            case R.id.buttonunbindservice:
                unbindService(conn);
            default:

                break;

        }
    }

    //建立服务连接
    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("LeeLog","onServiceConnected");
            String myNameString = ((FirstService.MyBinder) service).helloWorld("libs");
            Toast.makeText(MainActivity.this, myNameString, 3000).show();

           ((FirstService.MyBinder) service).getService().printLog();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("LeeLog","onServiceDisconnected");
        }
    };

    private Intent StartMyService()
    {
        Intent _intent=new Intent(MainActivity.this,FirstService.class);
        startService(_intent);
        Toast.makeText(this,"StartService",3000).show();

        return _intent;
    }

    private void StopMyService(Intent _intent)
    {
        stopService(_intent);

    }
}
