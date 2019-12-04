package top.codepy.urbantraffic.BusQueryCatalog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.OkHttpClient;
import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.ToolsCatalog.OkHttpData;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

public class BusQueryActivity extends AppCompatActivity {
    private static final String TAG = "公交查询 (BusQueryActivity)";
    private ExpandableListView expanded_menu;
    private Handler handler = new Handler();
    private boolean isRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_query);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        ToolbarMaster.setTitle("公交查询");
        ToolbarMaster.MenuCreate();
        setExpandable();
        getdata();
    }

    private void setExpandable() {
        expanded_menu = findViewById(R.id.expanded_menu);
        //expanded_menu.setAdapter(new BusAdapter(this));
    }

    private void getdata() {
        //  final String url = "http://192.168.3.5:8088/transportservice/action/GetBusStationInfo.do";
        final String url = "http://192.168.1.137/json.json";
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRun) {
                    final List<JSONArray> list = new ArrayList<>();
                    final long startTime = System.currentTimeMillis();
                    try {
                        for (int id = 1; id <= 2; id++) {
                            final JSONObject json = new JSONObject();
                            List<JSONObject> temp = new ArrayList<>();
                            json.put("BusStationId", id);
                            json.put("UserName", "user1");
                            OkHttpData.sendConnect(url, json.toString());
                            JSONArray array = OkHttpData.JsonObjectRead().getJSONArray("ROWS_DETAIL");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                temp.add(jsonObject);
                            }
                            Log.e(TAG, "原始(JSONObject)数据: " + temp);
                            Collections.sort(temp, new Comparator<JSONObject>() {
                                @Override
                                public int compare(JSONObject t0, JSONObject t1) {
                                    int i0, i1;
                                    try {
                                        i0 = t0.getInt("Distance");
                                        i1 = t1.getInt("Distance");
                                        return  i0 > i1 ? 1 : -1;
                                    } catch (Exception e) {
                                    }
                                    return 0;
                                }
                            });
                            JSONArray array1 = new JSONArray(temp);
                            list.add(array1);
                            Log.e(TAG, "排序(JSONObject)数据: " + temp.toString());
                            Log.e(TAG, "---------------------------------------------------------------- ");
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "原始数据:" + list);
                                expanded_menu.setAdapter(new BusAdapter(BusQueryActivity.this, list));
                                int groupCount = expanded_menu.getCount();
                                for (int i = 0; i < groupCount; i++) {
                                    expanded_menu.expandGroup(i);
                                }
                            }
                        });
                        long stopTime = System.currentTimeMillis();
                        Thread.sleep(15000 - (stopTime - startTime));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "可能是网络错误 ");
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRun = false;
    }
}
