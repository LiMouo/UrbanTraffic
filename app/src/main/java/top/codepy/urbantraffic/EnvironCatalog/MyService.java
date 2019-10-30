package top.codepy.urbantraffic.EnvironCatalog;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import top.codepy.urbantraffic.SQLiteCatalog.SQLiteEnvironMaster;
import top.codepy.urbantraffic.ToolsCatalog.OkHttpData;

public class MyService extends Service {
    private static final String TAG = "MyService";
    int i = 1;
    private JSONObject j1;
    private Handler handler = new Handler();
    private SQLiteEnvironMaster environMaster;
    private SQLiteDatabase db;
    private Context context;
    private Boolean isService = true;

    public MyService() {
//        Log.e(TAG, "MyService: "+this );
//        Log.e(TAG, "MyService: "+context );
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate 服务创建");
        environMaster = new SQLiteEnvironMaster(this, "Environ.db");
        db = environMaster.getWritableDatabase();
        getData();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand 服务运行中" + i);
        i++;
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        isService = false;
        Log.e(TAG, "onDestroy 服务销毁");

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
                    long startTime1 = System.currentTimeMillis();//获取开始时间
                    OkHttpData.sendConnect(url, jsonObject.toString());
                    if (OkHttpData.JsonObjectRead() != null) {
                        Log.e(TAG, "环境指标:" + OkHttpData.JsonObjectRead().toString());
                        j1 = OkHttpData.JsonObjectRead();
                        OkHttpData.sendConnect(url1, jsonObject1.toString());
                        Log.e(TAG, "道路指标:" + OkHttpData.JsonObjectRead().toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                EnvironActivity.recycler_environ.setAdapter(new EnvironAdapter(context, json(OkHttpData.JsonObjectRead())));
                                Log.e(TAG, "------数据写入完------");
                            }
                        });
                        try {
                            long endTime1 = System.currentTimeMillis();//获取结束时间
                            Log.e(TAG, "代码运行时间：" + (endTime1 - startTime1) + "ms");//输出程序运行时间
                          if ((endTime1-startTime1) > 3000){
                          }else {
                            Thread.sleep(3000 - (endTime1 - startTime1));}
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(TAG, "网络错误");
                        isService = false;
                    }
                } while (isService);
            }


        }).start();
    }

    public ContentValues json(JSONObject j2) {
        ContentValues values = new ContentValues();
        try {
            values.put("pm25", j1.getString("pm2.5"));
            values.put("co2", j1.getString("co2"));
            values.put("LightIntensity", j1.getString("LightIntensity"));
            values.put("humidity", j1.getString("humidity"));
            values.put("temperature", j1.getString("temperature"));
            values.put("Status", j2.getString("Status"));
            values.put("datetime", getdate());
            long count = db.insert("environ", null, values);
            Log.e(TAG, "写入数据库 第: " + count + " 条");
            if (count > 20) {
                db.execSQL("delete from environ where id = (select id from environ limit 1)");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return values;
    }

    private static String getdate() {
        Date date = new Date();
        SimpleDateFormat st = new SimpleDateFormat("mm:ss");
        String datetime = st.format(date);
        Log.e(TAG, "等待时间: " + st.format(date));
        return datetime;
    }
}
