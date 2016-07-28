package com.example.leebisheng.studyui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StudySpinnerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_spinner);
        //showSpinner();
        showSpinner2();
        showProcessBar();
        showSeekBar();
        showAutoCompleteTextView();


    }
    private  void showAutoCompleteTextView()
    {
        AutoCompleteTextView autoCompleteTextView=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        String[] countries=getResources().getStringArray(R.array.countries_array);   //数据源
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,countries);//建立adapter，并绑定数据
        autoCompleteTextView.setAdapter(arrayAdapter);//绑定UI




    }
    private  void showSeekBar()
    {
        SeekBar seekBar=(SeekBar)findViewById(R.id.seekBar);
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.i("leebisheng","onProgressChanged,progress:"+progress);
                    TextView textView=(TextView) findViewById(R.id.textViewProcess);
                    textView.setText(progress+"");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.i("leebisheng","onStopTrackingTouch");
                }
            });
        }
    }
    private void showProcessBar()
    {
        final EditText editText=(EditText)findViewById(R.id.editTextSeek);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.i("leebisheng","onEditorAction,actionId:"+actionId);
                ProgressBar progressBar=(ProgressBar)findViewById(R.id.progressBar);
                String str=editText.getText().toString();
                int i=Integer.parseInt(str);
                progressBar.setProgress(i);
                return false;
            }
        });
    }
    private void showSpinner()
    {
        final Spinner spinner=(Spinner)findViewById(R.id.spinner1);
        //1.建立数据源
        String[] myItemsStrings=getResources().getStringArray(R.array.countries);
        // 2.建立adapter
        // 3.建立adapter - source 的连接
        ArrayAdapter arrayAdapter= new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,myItemsStrings);
        // 4.绑定adapter到界面组件
        spinner.setAdapter(arrayAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               // Toast.makeText(StudySpinnerActivity.this,spinner.getSelectedItem().toString() ,1000).show();
                Toast.makeText(StudySpinnerActivity.this,((TextView)view).getText().toString() ,1000).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showSpinner2()
    {
        final Spinner spinner=(Spinner)findViewById(R.id.spinner1);
        //建立数据源
        final ArrayList<Country> countries=new ArrayList<Country>();
        countries.add(new Country(R.drawable.cn0,"中国"));
        countries.add(new Country(R.drawable.de,"德国"));
        //建立adapter  并且指定数据源
        final CoutryAdapter coutryadapter= new CoutryAdapter(this,countries);

        //绑定
        spinner.setAdapter(coutryadapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Toast.makeText(StudySpinnerActivity.this,spinner.getSelectedItem().toString() ,1000).show();

                String str;
                //获取数据的方法
//                1.从view上取
                str=((TextView)(view.findViewById(R.id.textViewCountryName))).getText().toString();
                //2.根据下标取
                str+=countries.get(position).getmCountryName();

                Toast.makeText(StudySpinnerActivity.this,str ,1000).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
}
