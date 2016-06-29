package com.leebisheng.helloworld;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main2);
        this.getWindow().setContentView(this.getLayoutInflater().inflate(R.layout.activity_main2,null));
        Button _button=(Button)this.findViewById(R.id.button1);

        _button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
              //add new button
                final Button _oldButton=(Button)findViewById(R.id.button1);
                _oldButton.setEnabled(false);

                final Button _newButton=new Button(v.getContext());
                _newButton.setText("进入系统");
                RelativeLayout _rl=(RelativeLayout)findViewById(R.id.RelativeLayout1);
                _newButton.setId(10000);

                RelativeLayout.LayoutParams _param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

                 _rl.addView (_newButton,_param);

                _newButton.setOnClickListener(new buttonClickListener());



            }

        });

    }

    class buttonClickListener implements View.OnClickListener {
        public void onClick(View v) {
            //增加自己的代码......
            final Button _button = (Button) findViewById(v.getId());

           switch (_button.getId())
            {
                case 10000:
                    //start activity 启动新的activity
                   // Intent _intent = new Intent();
                   // _intent.setClass(Main2Activity.this,MainActivity.class);
                    Intent _intent = new Intent(Main2Activity.this,MainActivity.class);
                    EditText _et=(EditText) findViewById(R.id.editText);
                    String _strSend= _et.getText().toString();
                    _intent.putExtra("first_data",_strSend);
                    startActivityForResult(_intent,123);

                break;

                default:
                    break;
            }


        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==321)
        {
            String _resultValue=data.getStringExtra("rentun_data");
            Toast.makeText(this,_resultValue,9000).show();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    // 注意 这里没有 @Override 标签
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.buttondail:
                Intent _intent1=new Intent();
               // _intent1.setAction(Intent.ACTION_DIAL);
                _intent1.setAction(Intent.ACTION_CALL);
                _intent1.setData(Uri.parse("tel:10086"));
                startActivity(_intent1);
                break;
            default:
                break;
        }
    }

}
