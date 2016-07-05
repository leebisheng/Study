package com.leebisheng.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by lee on 2016/7/4.
 */
public class ButtonBActivity extends AppCompatActivity {
    private static int mIndex=1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twobutton);

        EditText _edit=(EditText)findViewById(R.id.editTextStatus);
        _edit.setText("第"+mIndex+"个Activity B");
        mIndex++;

        Button _button_a=(Button) findViewById(R.id.buttonsatrta);
        _button_a.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent _intent_start_a =new Intent(ButtonBActivity.this,ButtonAActivity.class);
                startActivity(_intent_start_a);
            }
        });

        Button _button_b=(Button) findViewById(R.id.buttonstartb);
        _button_b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent _intent_start_b =new Intent(ButtonBActivity.this,ButtonBActivity.class);
                startActivity(_intent_start_b);
            }
        });
    }
}
