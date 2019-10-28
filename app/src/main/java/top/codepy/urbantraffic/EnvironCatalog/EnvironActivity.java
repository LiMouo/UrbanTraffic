package top.codepy.urbantraffic.EnvironCatalog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.SQLiteCatalog.SQLiteEnvironMaster;
import top.codepy.urbantraffic.ToolsCatalog.OkHttpData;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

public class EnvironActivity extends AppCompatActivity {
    private static final String TAG = "环境指标(EnvironActivity)";
    public static RecyclerView recycler_environ;
    private Handler handler = new Handler();
    private Boolean isGet = true;
    private JSONObject j1;
    private SQLiteEnvironMaster environMaster;
    private SQLiteDatabase db;
    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environ);
        context = this;
        environMaster = new SQLiteEnvironMaster(this, "Environ.db");
        db = environMaster.getWritableDatabase();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        ToolbarMaster.setTitle("环境指标");
        ToolbarMaster.MenuCreate();
        setRecycler();
        //getData();
       // new MyService(this,recycler_environ,db);

        Intent intent = new Intent(this, MyService.class);
        Log.e(TAG, "GG"+this );
        startService(intent);
        Log.e(TAG, "onCreate: 服务启动了" );
    }

    private void getData() {
        final String url = "http://192.168.3.5:8088/transportservice/action/GetAllSense.do";
        Map<String, String> map = new HashMap<>();
        map.put("UserName", "user1");
        final JSONObject jsonObject = new JSONObject(map);

        final String url1 = "http://192.168.3.5:8088/transportservice/action/GetRoadStatus.do";
        Map<String, String> map1 = new HashMap<>();
        map1.put("RoadId", "1");
        map1.put("UserName", "user1");
        final JSONObject jsonObject1 = new JSONObject(map1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    OkHttpData.sendConnect(url, jsonObject.toString());
                    Log.e(TAG, "环境指标:" + OkHttpData.JsonObjectRead().toString());
                    j1 = OkHttpData.JsonObjectRead();
                    OkHttpData.sendConnect(url1, jsonObject1.toString());
                    Log.e(TAG, "道路指标:" + OkHttpData.JsonObjectRead().toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recycler_environ.setAdapter(new EnvironAdapter(EnvironActivity.this, json(OkHttpData.JsonObjectRead())));
                            Log.e(TAG, "------数据写入完------");
                        }
                    });
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                } while (isGet);
            }


        }).start();
    }

    public ContentValues json(JSONObject j2) {
        // Log.e(TAG, "j1: " + j1);
        // Log.e(TAG, "j2: " + j2);
        ContentValues values = new ContentValues();
        try {
            values.put("pm25", j1.getString("pm2.5"));
            values.put("co2", j1.getString("co2"));
            values.put("LightIntensity", j1.getString("LightIntensity"));
            values.put("humidity", j1.getString("humidity"));
            values.put("temperature", j1.getString("temperature"));
            values.put("Status", j2.getString("Status"));
            long count = db.insert("environ", null, values);
            Log.e(TAG, "写入数据库 第: " + count + " 条");
            if (count > 20) {
                db.execSQL("delete from environ where id = (select id from environ limit 1)");
            }
            getdate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return values;
    }
    private static void getdate() {
        Date date = new Date();
        SimpleDateFormat st = new SimpleDateFormat("ss");
        Log.e(TAG, "等待时间: " + st.format(date));
    }

    private void setRecycler() {
        recycler_environ = findViewById(R.id.recycler_environ);
        recycler_environ.setLayoutManager(new GridLayoutManager(this, 3));
        recycler_environ.addItemDecoration(new MyDecoration());
    }


    class MyDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(1, 10, 10, 1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //isGet = false;
        //finish();
    }
}
