package com.example.leebisheng.studyui;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by leebisheng on 2016/7/14.
 */
public class StudyTextViewActivity extends Activity
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.textviewdemo);

        //ShowTextView();
       // ShowTextView1();
    }

    //fromHtml demo
    private void ShowTextView()
    {
        Html.ImageGetter imageGetter=new  Html.ImageGetter()
        {

            /**
             * This method is called when the HTML parser encounters an
             * &lt;img&gt; tag.  The <code>source</code> argument is the
             * string from the "src" attribute; the return value should be
             * a Drawable representation of the image or <code>null</code>
             * for a generic replacement image.  Make sure you call
             * setBounds() on your Drawable if it doesn't already have
             * its bounds set.
             *
             * @param source
             */
            @Override
            public Drawable getDrawable(String source)
            {
                if(source!=null)
                {
                   BitmapDrawable returnDrawable ;

                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                  {
                      returnDrawable= (BitmapDrawable)getResources().getDrawable(R.drawable.lee1,getApplicationContext().getTheme());
                  }
                 else
                 {
                     returnDrawable= (BitmapDrawable)getResources().getDrawable(R.drawable.lee1);
                 }
                    returnDrawable.setBounds(0,0,returnDrawable.getIntrinsicWidth(),returnDrawable.getIntrinsicHeight());
                    return returnDrawable;
                }
                else
                    return null;
            }

        };
        TextView textview1 = (TextView) findViewById(R.id.textView1);
        textview1.setText(Html.fromHtml("<b>北京你好！</b> <h1>日本你好！</h1><font color='#00FF00'>美国你好！</font><img src=http://bbs-static.smartisan.cn/template/smartisan/src/img/apps/icon_bbs.png/>",imageGetter,null));

    }

    //spannable demo
    private  void ShowTextView1()
    {
        TextView textView1=(TextView)findViewById(R.id.textView1);
        SpannableStringBuilder spannableStringBuilder=new SpannableStringBuilder("大家好，  这是我爸爸！");
        ImageSpan imageSpan=new ImageSpan(StudyTextViewActivity.this,R.drawable.lee1);

        ClickableSpan clickableSpan=new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                float x=widget.getX();
                float y=widget.getY();


                Toast.makeText(StudyTextViewActivity.this,"你点击了我！",3000).show();
            }
        };
        spannableStringBuilder.setSpan(imageSpan,4,5, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.setSpan(clickableSpan,0,3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        textView1.setText(spannableStringBuilder);

    }


  }
