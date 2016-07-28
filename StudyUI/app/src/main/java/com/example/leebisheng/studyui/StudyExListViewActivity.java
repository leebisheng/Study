package com.example.leebisheng.studyui;

import android.app.ExpandableListActivity;
import android.app.ListActivity;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by leebisheng on 2016/7/28.
 */
public class StudyExListViewActivity extends ExpandableListActivity {
    /**
     * Called when the activity is starting.  This is where most initialization
     * should go: calling {@link #setContentView(int)} to inflate the
     * activity's UI, using {@link #findViewById} to programmatically interact
     * with widgets in the UI, calling
     * {@link #managedQuery(Uri, String[], String, String[], String)} to retrieve
     * cursors for data being displayed, etc.
     * <p/>
     * <p>You can call {@link #finish} from within this function, in
     * which case onDestroy() will be immediately called without any of the rest
     * of the activity lifecycle ({@link #onStart}, {@link #onResume},
     * {@link #onPause}, etc) executing.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see #onStart
     * @see #onSaveInstanceState
     * @see #onRestoreInstanceState
     * @see #onPostCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayExListView();
    }

    public void  DisplayExListView()
    {
        // 建立数据源
        List<HashMap<String,String>> groupData=new ArrayList<HashMap<String, String>>();
        HashMap<String,String> _map1=new HashMap<String, String>();
        _map1.put("Name","Lee");
        _map1.put("Address","新亚洲体育城");
        groupData.add(_map1);
        HashMap<String,String> _map2=new HashMap<String, String>();
        _map2.put("Name","Lee1");
        _map2.put("Address","新亚洲体育城1");
        groupData.add(_map2);
        HashMap<String,String> _map3=new HashMap<String, String>();
        _map3.put("Name","Lee2");
        _map3.put("Address","新亚洲体育城2");
        groupData.add(_map3);

        List<List<HashMap<String,String>>> childData=new ArrayList<List<HashMap<String,String>>>();
        List<HashMap<String,String>> childItem=new ArrayList<HashMap<String, String>>();
        HashMap<String,String> _map11=new HashMap<String, String>();
        _map11.put("Name","lxt1");
        _map11.put("Address","新亚洲体育城");
        childItem.add(_map11);
        HashMap<String,String> _map12=new HashMap<String, String>();
        _map12.put("Name","lxt2");
        _map12.put("Address","新亚洲体育城");
        childItem.add(_map12);
        HashMap<String,String> _map13=new HashMap<String, String>();
        _map13.put("Name","lxt3");
        _map13.put("Address","新亚洲体育城");
        childItem.add(_map13);
        childData.add(childItem);
        childData.add(childItem);
        childData.add(childItem);
        // 建立adapter 绑定数据
        ExpandableListAdapter adapter=new SimpleExpandableListAdapter(this,groupData,android.R.layout.simple_expandable_list_item_2,
                new String[]{"Name","Address"},new int[]{android.R.id.text1,android.R.id.text2},
                childData,android.R.layout.simple_expandable_list_item_2,
                new String[]{"Name","Address"},new int[]{android.R.id.text1,android.R.id.text2});
        //绑定adapter到UI
        setListAdapter(adapter);

        ExpandableListView expandableListView=getExpandableListView();
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(StudyExListViewActivity.this,"groupPosition:"+groupPosition+"  childPosition:"+childPosition,3000).show();
                return false;
            }
        });
    }
}
