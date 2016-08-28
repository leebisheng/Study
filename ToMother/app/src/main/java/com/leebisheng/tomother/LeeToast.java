package com.leebisheng.tomother;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by leebisheng on 2016/8/27.
 */
public class LeeToast {
    private  Toast toast;
    private Field field;
    private Object obj;
    private Method showMethod,hideMethod;
    private int mDuration=0;
    private int mTextSize=16;
    private int mTextColor= Color.WHITE;
    private int mImageID =0;

    public LeeToast(int mTextColor, int mTextSize, int mImageID) {
        this.mTextColor = mTextColor;
        this.mTextSize = mTextSize;
        this.mImageID = mImageID;
    }

    public LeeToast() {
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public void setmImageID(int mImageID) {
        this.mImageID = mImageID;
    }

    public void setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public void setmTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }

    public  LeeToast makeText(Context context, CharSequence text, int duration) {

        toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        //利用反射技术拿到mTN对象
        reflectionTN();
        mDuration=duration;

        //设置字体 颜色
        LinearLayout linearLayout=(LinearLayout) toast.getView();
        TextView messageTextView = (TextView) linearLayout.getChildAt(0);
        messageTextView.setTextSize(mTextSize);
        messageTextView.setTextColor(mTextColor);
        //设置背景
        if (mImageID !=0) {
            linearLayout.setBackgroundResource(mImageID);
        }

        return this;
    }

    public void show()
    {
        try {
            showMethod.invoke(obj, null);//调用TN对象的show()方法，显示toast
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mDuration!=0)
        {
            final Thread thread=new Thread(new Runnable()
            {
             @Override
             public void run()
              {
                Log.i("leebisheng", "@thread");
                // TODO Auto-generated method stub
                  try {
                      Thread.sleep(mDuration);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
                  close();
                }
             });
            thread.start();

        }

    }
    public void close() {
        try {
            hideMethod.invoke(obj, null);//调用TN对象的hide()方法，关闭toast
        } catch (Exception e)

        {
            e.printStackTrace();
        }
    }
    private void reflectionTN() {
        try {
            field = toast.getClass().getDeclaredField("mTN");
            field.setAccessible(true);
            obj = field.get(toast);
            showMethod = obj.getClass().getDeclaredMethod("show", null);
            hideMethod = obj.getClass().getDeclaredMethod("hide", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
