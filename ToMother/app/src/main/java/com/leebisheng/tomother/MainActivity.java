package com.leebisheng.tomother;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends Activity {
    public int iDispAll=0 ;
    public List<Long> times = new ArrayList<Long>();
    private String[][] mImages;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private android.hardware.Camera mCamera;
    private android.hardware.Camera.Parameters mParameters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//关闭窗口的标签，即全屏显示
        setContentView(R.layout.activity_main);
        try {
            Do(); //程序主体
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                Log.i("leebisheng","@addPermission");
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission)) {
                    Log.i("leebisheng","@shouldShowRequestPermissionRationale");
                    return false;
                }
            }
        }
        return true;
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private  void Do() throws InterruptedException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //先申请权限 (支持多权限）
            List<String> permissionsNeeded = new ArrayList<String>();

            final List<String> permissionsList = new ArrayList<String>();

            if (!addPermission(permissionsList, Manifest.permission.WRITE_CONTACTS))
                permissionsNeeded.add("Write Contacts");
            if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
                permissionsNeeded.add("Call Phone");

            Log.i("leebisheng","@Do");

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String message = "You need to grant access to " + permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message = message + ", " + permissionsNeeded.get(i);
                    showMessageOKCancel
                            (message,
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                        Log.i("leebisheng","requestPermissions1");
                                    }
                                }
                            );
                    return;
                }
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                Log.i("leebisheng","requestPermissions2");
                return;
            }
        }
        // 权限申请成功后才执行程序主体
        init();
        showPicture();
        showContacts();

    }

    private void init() throws InterruptedException {
            //初始化 mImages
            //获取家人的数目
            //   获取"家人"组ID
            Cursor cursor1 = null;
            Cursor cursor2 = null;
            cursor1 = getContentResolver().query(ContactsContract.Groups.CONTENT_URI,
                    new String[]{ContactsContract.Groups._ID, ContactsContract.Groups.TITLE},
                    ContactsContract.Groups.TITLE + " like '%家人%' or "+ContactsContract.Groups.TITLE + " like '%family%' ", null, null);

            String groupId = "";
            while (cursor1.moveToNext()) {
                groupId = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Groups._ID));

            }


            //通过分组的id从data表读"家人"的raw_contact_id ，name，photo_uri
            String[] RAW_PROJECTION = new String[]{ContactsContract.Data.CONTACT_ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data.PHOTO_ID};
            String RAW_CONTACTS_WHERE = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
                    + "=?"
                    + " and "
                    + ContactsContract.Data.MIMETYPE
                    + "="
                    + "'"
                    + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
                    + "'";

                if(cursor1.getCount()==0)
                {
                    cursor1 = getContentResolver().query(ContactsContract.Data.CONTENT_URI, RAW_PROJECTION,
                            null, null, "data1 asc");
                    Log.i("leebisheng","true");
                }
                else
                {
                    cursor1 = getContentResolver().query(ContactsContract.Data.CONTENT_URI, RAW_PROJECTION,
                            RAW_CONTACTS_WHERE, new String[]{groupId + ""}, "data1 asc");
                    Log.i("leebisheng","false");
                }

            mImages = new String[cursor1.getCount()][4];
            int i = 0;
            while (cursor1.moveToNext()) {
                String contactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                String name = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                String photoID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Data.PHOTO_ID));

                cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactID, null, null);
                String number = "";
                while (cursor2.moveToNext())//有多个号码时只取第一个
                {
                    number = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    break;
                }

                mImages[i][0] = contactID;
                mImages[i][1] = name;
                mImages[i][2] = number;
                mImages[i][3] = photoID;
                i++;
                Log.i("leebisheng@init", cursor1.getCount() + "    " + name + "  " + " contactID:" + contactID + " photoID:" + photoID + " number:" + number);
            }
    }  ;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i("leebisheng","@onRequestPermissionsResult1");
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Log.i("leebisheng","@onRequestPermissionsResult2");
                String grantResurt="";
                for (int i = 0; i < permissions.length; i++)
                {  // Check for ACCESS_FINE_LOCATION
                    if (grantResults[i]!=PackageManager.PERMISSION_GRANTED)
                        grantResurt+=permissions[i]+" is Denied;";

                }
                if (grantResults.length>0)
                    Toast.makeText(MainActivity.this, grantResurt, Toast.LENGTH_SHORT).show();

                try {
                    Do();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            break;
            default:

        }

    }

    private void showPicture()
    {
        GridView gridView1=(GridView)findViewById(R.id.gridView1);
        //创建数据源
        //mImanges
        //创建APDATER
        //绑定UI
        gridView1.setAdapter(new ImageAdapter());

        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //判断是否是双击
                times.add(SystemClock.uptimeMillis());
                if (times.size() == 2) {

                    if (times.get(times.size()-1)-times.get(0) < 500) {
                        times.clear();//已经完成了一次双击，list可以清空了
                        //-------------------- do ------------------------


                        String _strPhone=mImages[i][2];
                        Intent _intent1=new Intent();
                        _intent1.setAction(Intent.ACTION_CALL);
                        _intent1.setData(Uri.parse("tel:"+_strPhone));
                        startActivity(_intent1);
                        //-------------------- do ------------------------
                    } else {
                        //这种情况下，第一次点击的时间已经没有用处了，第二次就是“第一次”
                        times.remove(0);
                    }
                }

            }
        });
    }
    class ImageAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return mImages.length;
        }

        public Object getItem(int position) {
            return mImages[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

         @Override
        public View getView(int position, View convertView, ViewGroup parent) {
             ImageView _imageView = new ImageView(MainActivity.this);
             if(mImages[position][3]==null)
             {//没有图像用文字代替

                 Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(),R.drawable.nophoto).copy(Bitmap.Config.ARGB_8888,true);

                 Canvas canvas = new Canvas(bitmap);
                 Paint paint = new Paint();
                 paint.setColor(Color.RED);
                 paint.setTextSize(32);
                 paint.setFakeBoldText(true);
                 //canvas.rotate(45);
                 canvas.drawText(mImages[position][1],0,60,paint);
                 //save all clip
                 canvas.save( Canvas.ALL_SAVE_FLAG );//保存
                 //store
                 canvas.restore();//存储
                 _imageView.setImageBitmap(bitmap);


             }
             else {//从通讯录读头像
                 Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                         Long.parseLong(mImages[position][0]));
                 // 打开头像图片的InputStream
                 InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), uri);
                 // 从InputStream获得bitmap
                 Bitmap bitmap = BitmapFactory.decodeStream(input);

                 _imageView.setImageBitmap(bitmap);
             }
             return _imageView;
         }
    }


    private void showContacts()
    {
        Cursor cursor = null;
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone._ID,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED}, null, null,
                ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED+" DESC");


        Log.i("leebisheng","record cnt:"+cursor.getCount());

        SimpleCursorAdapter simpleCursorAdapter  =new SimpleCursorAdapter(this, R.layout.contact_items, cursor,
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER},
                    new int[]{R.id.textViewName, R.id.textViewNumber});

        ListView listView=(ListView)findViewById(R.id.listView1);
        listView.setAdapter(simpleCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            //判断是否是双击
                times.add(SystemClock.uptimeMillis());
                if (times.size() == 2) {

                    if (times.get(times.size()-1)-times.get(0) < 500) {
                        times.clear();//已经完成了一次双击，list可以清空了
                        //-------------------- do ------------------------

                        TextView textView=(TextView) view.findViewById(R.id.textViewNumber);
                        String _strPhone=textView.getText().toString();
                        Intent _intent1=new Intent();
                        _intent1.setAction(Intent.ACTION_CALL);
                        _intent1.setData(Uri.parse("tel:"+_strPhone));
                        startActivity(_intent1);
                        //-------------------- do ------------------------
                    } else {
                        //这种情况下，第一次点击的时间已经没有用处了，第二次就是“第一次”
                        times.remove(0);
                    }
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.shoudian:
                TurnOnOff();

                break;
            case R.id.calendar:
                //显示日期时间
                DispDateInfo();
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }
    private void TurnOnOff()
    {
        if(mCamera==null) {
            //开闪光灯
            try {
                mCamera = android.hardware.Camera.open();
                mParameters = mCamera.getParameters();
                mParameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(mParameters);


            } catch (Exception ex) {
            }
        }
        else {
            //关闭闪光灯

            mParameters = mCamera.getParameters();
            mParameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParameters);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void DispDateInfo()
    {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int iXQ=c.get(Calendar.DAY_OF_WEEK);
        String sXQ="" ;
       if(iXQ==1){
           sXQ ="天";
           }else if(iXQ==2){
           sXQ ="一";
           }else if(iXQ==3){
           sXQ ="二";
            }else if(iXQ==4){
           sXQ ="三";
              }else if(iXQ==5){
           sXQ ="四";
              }else if(iXQ==6){
           sXQ ="五";
              }else if(iXQ==7){
           sXQ ="六";
              }

        LeeLunar lunar= new LeeLunar();
        String s;
        s=" "+hour+"点"+minute+"分"+"\n";
        s+=" "+month+"月"+day+"日"+"\n";
        s+="  星期"+sXQ+"\n\n";
        s+=lunar.getLunar1(String.valueOf(year),String.valueOf(month),String.valueOf(day));
        LeeToast leeToast=new LeeToast();

        leeToast.setmTextSize(64);
        leeToast.setmImageID(R.drawable.lxt);
        leeToast.makeText(MainActivity.this, s, 0).show();
    }
}
