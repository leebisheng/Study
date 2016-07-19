package com.example.leebisheng.studyui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by leebisheng on 2016/7/13.
 */
public class CustomView extends View
{
    public CustomView(Context context)
    {
        super(context);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //Matrix
        Matrix matrix=new Matrix();
        matrix.setRotate(45.0f);
   //     canvas.setMatrix(matrix);



        Paint paint=new Paint();
        paint.setColor(Color.BLUE);
        RectF  rect=new RectF();
        rect.left=20;
        rect.top=10;
        rect.right=500;
        rect.bottom=160;
        canvas.drawRoundRect(rect,30,30,paint);
        rect.left+=500;
        rect.top+=0;
        rect.right+=500;
        rect.bottom+=0;
        canvas.drawRoundRect(rect,30,30,paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(100);

        canvas.drawText("Button1",35,120,paint);
        canvas.drawText("Button2",560,120,paint);


        paint.setColor(Color.RED);
        RectF oval=new RectF(100,100,300,600);
        canvas.drawOval(oval,paint);

        //Clip
     //   canvas.clipRect(20,20,120,80);


        Log.i("lee","onDraw");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.i("lee","onTouchEvent:"+event.getAction());
        Log.i("lee" ,"x:"+event.getX()+"  y:"+event.getY());
        return super.onTouchEvent(event);

    }

    /**
     * Default implementation of {@link KeyEvent.Callback#onKeyDown(int, KeyEvent)
     * KeyEvent.Callback.onKeyDown()}: perform press of the view
     * when {@link KeyEvent#KEYCODE_DPAD_CENTER} or {@link KeyEvent#KEYCODE_ENTER}
     * is released, if the view is enabled and clickable.
     * <p/>
     * <p>Key presses in software keyboards will generally NOT trigger this listener,
     * although some may elect to do so in some situations. Do not rely on this to
     * catch software key presses.
     *
     * @param keyCode A key code that represents the button pressed, from
     *                {@link KeyEvent}.
     * @param event   The KeyEvent object that defines the button action.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("lee","keyCode:"+keyCode);
        return super.onKeyDown(keyCode, event);
    }

}
