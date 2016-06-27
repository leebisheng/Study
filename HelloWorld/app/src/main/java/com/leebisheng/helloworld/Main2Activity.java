package com.leebisheng.helloworld;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

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
                    //start activity
                   // Intent _intent = new Intent();
                   // _intent.setClass(Main2Activity.this,MainActivity.class);
                    Intent _intent = new Intent(Main2Activity.this,MainActivity.class);
                    EditText _et=(EditText) findViewById(R.id.editText);
                    String _strSend= _et.getText().toString();
                    _intent.putExtra("first_data",_strSend);
                    startActivity(_intent);

                break;
                default:
                    break;
            }


        }
    };
}
