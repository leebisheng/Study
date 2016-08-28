package com.leebisheng.tomother;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class TestToastActivity extends Activity {
private Button showtoast,closetoast;
private Toast toast;
private Field field;
private Object obj;
private Method showMethod,hideMethod;

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //初始化按钮组件
        showtoast = (Button)this.findViewById(R.id.showtoast);
        closetoast = (Button)this.findViewById(R.id.closetoast);
        
        //设置组件监听
        showtoast.setOnClickListener(new MyOnClickListener());
        closetoast.setOnClickListener(new MyOnClickListener());
        
       /* //创建Toast对象
        toast = Toast.makeText(this, "Toast自定义显示时间测试", 1);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
       
        //利用反射技术拿到mTN对象    
        reflectionTN();*/


    }
    
    
class MyOnClickListener implements View.OnClickListener{
@Override
public void onClick(View v) {
switch (v.getId()) {
case R.id.showtoast:
/*try {
showMethod.invoke(obj, null);//调用TN对象的show()方法，显示toast
} catch (Exception e) {
e.printStackTrace();
}*/
    LeeToast leeToast=new LeeToast();
    leeToast.setmTextSize(24);
    leeToast.setmImageID(R.drawable.lxt);
    leeToast.makeText(TestToastActivity.this, "Toast自定义显示时间测试", 9900).show();
break;
case R.id.closetoast:
try {
hideMethod.invoke(obj, null);//调用TN对象的hide()方法，关闭toast
} catch (Exception e) {
e.printStackTrace();
}
break;
default:
break;
} 
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