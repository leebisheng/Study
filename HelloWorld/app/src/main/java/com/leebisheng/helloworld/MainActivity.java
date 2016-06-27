package com.leebisheng.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //手动增加代码开始
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new myOnClickListener());

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on click
                //增加自己的代码......
                final TextView text = (TextView) findViewById(R.id.textview1);
                text.setText("OnClick. " + " TEST2");
            }
        }
        );

        Intent _intent=this.getIntent();
        String _strInto;
        if(_intent!=null)
        {
            _strInto = _intent.getStringExtra("first_data");
            Log.i("libs", _strInto);

            Toast.makeText(this, _strInto, 9000).show();
        }
        else
        {
            Toast.makeText(this, "intent is null ", 3000).show();
        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //手动增加代码开始
    class myOnClickListener implements View.OnClickListener {
        public void onClick(View v) {
            //增加自己的代码......
            Button button1 = (Button) findViewById(v.getId());
            if(button1.getText()=="1")
            {
                button1.setText("2 ");
            }
            else button1.setText("1");

        }
    };
    //手动增加代码结束

    // 注意 这里没有 @Override 标签
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.button3:
             Toast.makeText(this,"buuton3 clicked",3000).show();
                break;
            case R.id.button4:
                Log.i("libs","button4 clicked");
                break;
            case R.id.button5:
                //System.exit(0);
                Process.killProcess(Process.myPid());
                break;

            case R.id.button6:


            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
//test

        return super.onOptionsItemSelected(item);
    }
}

