package com.example.leebisheng.studyui;

import android.app.Activity;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

/**
 * Created by leebisheng on 2016/7/25.
 */
public class StudyListViewActivity extends ListActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    int mSelectedItem=-1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.listview); // 这里可以不设，如果要设的话，List名字要取list
        //showListView();
       // showListView1();
        showListView2(); //利用cursor

    }
    private void showListView2()
    {



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},MY_PERMISSIONS_REQUEST_READ_CONTACTS  );
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
        else {

            Cursor cursor = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {

                cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null, null);
            } else {
                cursor = getContentResolver().query(Contacts.Phones.CONTENT_URI, null, null, null, null, null);
            }

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                Log.i("leebisheng", "第" + i + "列：" + cursor.getColumnNames()[i].toString());
            }
            Log.i("leebisheng", "记录数：" + cursor.getCount());
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.i("leebisheng", name + "  " + number);

            }


            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.contact_items, cursor,
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER},
                    new int[]{R.id.textViewName, R.id.textViewNumber}, 0);

            setListAdapter(simpleCursorAdapter);
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode!=MY_PERMISSIONS_REQUEST_READ_CONTACTS)
        {
            Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();

        }
        else
        {
            showListView2();
        }
    }

    private  void showListView()
    {
        String[] countries=getResources().getStringArray(R.array.countries);
        // 注释掉的写法是继承与Activity 下面写法的条件是继承ListActivity 。
        //ListView listView=(ListView)findViewById(R.id.listViewList);
        //ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,countries);
        //listView.setAdapter(arrayAdapter);

        setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,countries));
      //  ListView lv=getListView();
      //  lv.setTextFilterEnabled(true);

    }



    private void showListView1()
    {
        String[] countries=getResources().getStringArray(R.array.countries);
        final List<String> itemList=new ArrayList<String>();
        for(int i=0;i<countries.length;i++)
        {
            itemList.add(countries[i]);
            Log.i("leebisheng","countries="+countries[i]);
        }

        setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,itemList));
        final ArrayAdapter<String> arrayAdapter=(ArrayAdapter<String>)getListAdapter();

        final EditText editText=(EditText)findViewById(R.id.editTextList);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                Log.i("leebisheng","onKey,keyCode:"+keyCode );
                if(keyCode==KeyEvent.KEYCODE_ENTER&& event.getAction()==KeyEvent.ACTION_UP)
                {
                    itemList.add(editText.getText().toString());
                    arrayAdapter.notifyDataSetChanged();
                    editText.setText("");
                }
                return false;
            }
        });

        final ListView listView=getListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i("leebisheng","onItemClick,  position:"+position+"  id:"+ id);
                Log.i("leebisheng","SelectedItemId:"+listView.getCheckedItemPosition());

                if(mSelectedItem==position)
                {
                    itemList.remove(position);
                    arrayAdapter.notifyDataSetChanged();
                    mSelectedItem=-1;
                    listView.setItemChecked(-1,true);
                }
                else
                {
                    mSelectedItem=position;
                }
            }


        });

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("leebisheng","onItemSelected,  position:"+position+"  id:"+ id);
                Log.i("leebisheng","SelectedItemId:"+listView.getSelectedItemId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button button=(Button)findViewById(R.id.buttongetitemid);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("leebisheng","onClick of button,ListView selected:"+listView.getCheckedItemPosition());
            }
        });
    }
}
