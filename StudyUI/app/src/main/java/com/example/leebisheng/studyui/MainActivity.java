package com.example.leebisheng.studyui;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        LinearLayout linearLayout=new LinearLayout(this );
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Button _button1=new Button(this);
        _button1.setText("Lee Button");

        linearLayout.addView(_button1);

       // setContentView(linearLayout);
        CustomView customview=new CustomView(this);
        Log.i("lee","1");
        //setContentView(new CustomView(this));
        setContentView(customview);;


        customview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Log.i("lee","onClick");
                int[] viewLocation = new int[2];
                v.getLocationInWindow(viewLocation);
                int viewX = viewLocation[0]; // x 坐标
                int viewY = viewLocation[1]; // y 坐标

                //Toast.makeText(MainActivity.this,"You click at "+viewX+","+viewY,500).show();


            }


        });

       // new Thread(new MyFirstThread()).start();

        _button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               // Toast.makeText(MainActivity.this,"Hello Lee!",3000).show();
            }
        });




    }



    class  MyFirstThread implements Runnable
    {

        @Override
        public void run()
        {
          Log.i("MyFirstThread","into run");
            while (!Thread.currentThread().isInterrupted())
            {
                try {
                    Thread.sleep(1000);
                    Log.i("MyFirstThread","loop run");
                }
                catch (     Exception e    )
                {

                }
              ;
            }

        }
    }
}
