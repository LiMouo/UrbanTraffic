package top.codepy.urbantraffic.ThresholdCatalog;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.ToolsCatalog.OkHttpData;

public class ThresholdService extends Service {
    private static final String TAG = "阈值服务 (Threshold)";
    private NotificationManager manager;
    private SharedPreferences preferences;
    private List<Map<String, String>> list;
    private Map<String, String> map;
    private Handler handler = new Handler();
    private Boolean isTrue = true;

    public ThresholdService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "阈值服务创建");
        preferences = getSharedPreferences("Threshold", MODE_PRIVATE);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "阈值服务 启动空体 => " + (startId - 1));
        if (startId == 1) {
            Log.e(TAG, "阈值服务 启动实体 => " + startId);
            String temperature = preferences.getString("temperature", null);
            String humidity = preferences.getString("humidity", null);
            String LightIntensity = preferences.getString("LightIntensity", null);
            String co2 = preferences.getString("co2", null);
            String pm25 = preferences.getString("pm25", null);
            String Status = preferences.getString("Status", null);
            map = new HashMap<>();
            map.put("temperature", preferences.getString("temperature", null));
            map.put("humidity", preferences.getString("humidity", null));
            map.put("LightIntensity", preferences.getString("LightIntensity", null));
            map.put("co2", preferences.getString("co2", null));
            map.put("pm2.5", preferences.getString("pm25", null));
            map.put("Status", preferences.getString("Status", null));
            String t = null;
            Log.e(TAG, "读取的数据: " + map);
            Log.e(TAG, "onStartCommand: " + (map.get("Status") == null ? -1 : 1));
            Log.e(TAG, "onStartCommand: " + temperature.equals(""));
            Log.e(TAG, "onStartCommand: " + (t == null ? -1 : 1));
            final String url1 = "http://192.168.3.5:8088/transportservice/action/GetAllSense.do";
            final Map<String, String> map1 = new HashMap<>();
            map1.put("UserName", "user1");
            final JSONObject jsonObject1 = new JSONObject(map1);

            final String url2 = "http://192.168.3.5:8088/transportservice/action/GetRoadStatus.do";
            Map<String, String> map2 = new HashMap<>();
            map2.put("RoadId", "1");
            map2.put("UserName", "user1");
            final JSONObject jsonObject2 = new JSONObject(map2);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isTrue) {
                        long startTime = System.currentTimeMillis();
                        OkHttpData.sendConnect(url1, jsonObject1.toString());
                        Log.e(TAG, "环境检查: " + OkHttpData.JsonObjectRead());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ifThreshold();
                            }
                        });
                        try {
                            long stopTime = System.currentTimeMillis();
                            if (stopTime - startTime >= 0) {
                                Thread.sleep(10000 - (stopTime - startTime));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isTrue) {
                        long startTime = System.currentTimeMillis();
                        OkHttpData.sendConnect(url2, jsonObject2.toString());
                        Log.e(TAG, "道路状态: " + OkHttpData.JsonObjectRead());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if ((map.get("Status").equals("") ? -1 : Integer.parseInt(map.get("Status"))) < Integer.parseInt(OkHttpData.JsonObjectRead().getString("Status"))) {
                                        Log.e(TAG, "道路阈值: " + map.get("Status") + "  当前道路: " + OkHttpData.JsonObjectRead().getString("Status"));
                                        createNotification(ThresholdService.this, 6, manager, "道路", map.get("Status").equals("") ? null : (map.get("Status")), OkHttpData.JsonObjectRead().getString("Status"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        try {
                            long stopTime = System.currentTimeMillis();
                            if (stopTime - startTime >= 0) {
                                Thread.sleep(10000 - (stopTime - startTime));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
        //manager.notify(1, createNotification(this, manager));
        // ThresholdActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        return super.onStartCommand(intent, flags, startId);
    }

    private void ifThreshold() {
        try {
            if ((map.get("temperature").equals("") ? -1 : Integer.parseInt(map.get("temperature"))) < Integer.parseInt(OkHttpData.JsonObjectRead().getString("temperature"))) {
                Log.e(TAG, "温度阈值: " + map.get("temperature") + "  当前温度: " + OkHttpData.JsonObjectRead().getString("temperature"));
                createNotification(this, 1, manager, "温度", map.get("temperature").equals("") ? null : (map.get("temperature") + " ℃"), OkHttpData.JsonObjectRead().getString("temperature") + " ℃");
            }
            if ((map.get("humidity").equals("") ? -1 : Integer.parseInt(map.get("humidity"))) < Integer.parseInt(OkHttpData.JsonObjectRead().getString("humidity"))) {
                Log.e(TAG, "湿度阈值: " + map.get("humidity") + "  当前湿度: " + OkHttpData.JsonObjectRead().getString("humidity"));
                createNotification(this, 2, manager, "湿度", map.get("humidity").equals("") ? null : (map.get("humidity") + " hPa"), OkHttpData.JsonObjectRead().getString("humidity") + " hPa");
            }
            if ((map.get("LightIntensity").equals("") ? -1 : Integer.parseInt(map.get("LightIntensity"))) < Integer.parseInt(OkHttpData.JsonObjectRead().getString("LightIntensity"))) {
                Log.e(TAG, "光照阈值: " + map.get("LightIntensity") + "  当前光照: " + OkHttpData.JsonObjectRead().getString("LightIntensity"));
                createNotification(this, 3, manager, "光照", map.get("LightIntensity").equals("") ? null : (map.get("LightIntensity") + " Lux"), OkHttpData.JsonObjectRead().getString("LightIntensity") + " Lux");
            }
            if ((map.get("co2").equals("") ? -1 : Integer.parseInt(map.get("co2"))) < Integer.parseInt(OkHttpData.JsonObjectRead().getString("co2"))) {
                Log.e(TAG, "C02阈值: " + map.get("co2") + "  当前C02: " + OkHttpData.JsonObjectRead().getString("co2"));
                createNotification(this, 4, manager, "CO₂", map.get("co2").equals("") ? null : (map.get("co2") + " mg/m3"), OkHttpData.JsonObjectRead().getString("co2") + " mg/m3");
            }
            if ((map.get("pm2.5").equals("") ? -1 : Integer.parseInt(map.get("pm2.5"))) < Integer.parseInt(OkHttpData.JsonObjectRead().getString("pm2.5"))) {
                Log.e(TAG, "PM2.5阈值: " + map.get("pm2.5") + "  当前PM2.5: " + OkHttpData.JsonObjectRead().getString("pm2.5"));
                createNotification(this, 5, manager, "PM2.5", map.get("pm2.5").equals("") ? null : (map.get("pm2.5") + " μg/m3"), OkHttpData.JsonObjectRead().getString("pm2.5") + " μg/m3");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*任务栏通知*/
    private void createNotification(Context context, int id, NotificationManager manager, String name, String n1, String n2) {
        String CHANNEL_ID = "Threshold"; /*频道ID*/
        /*判断API Android 版本 大于26 则执行*/
        Notification notification = null;
        if (n1 != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.e(TAG, "当前SDK_INT=> " + Build.VERSION.SDK_INT);
                Log.e(TAG, "目标VERSION_CODES=> " + Build.VERSION_CODES.O);
                /* 渠道ID                通知状态等级*/
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "1", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("阈值通知组");   /* 设置渠道描述*/
                channel.canBypassDnd();                 /* 是否绕过勿扰模式*/
                channel.setBypassDnd(true);             /* 设置绕过勿扰模式*/
                channel.canShowBadge();                 /* 桌面Launcher的消息角标*/
                channel.setShowBadge(true);             /* 桌面Launcher的消息角标*/
                channel.setSound(null, null); /*设置通知出现时的声音,默认是有声音*/
                channel.enableLights(true);             /*设置通知时出现时闪烁呼吸灯*/
                channel.setLightColor(Color.BLUE);
                channel.enableVibration(false);         /*设置通知 是否出现震动*/
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                manager.createNotificationChannel(channel);
            }
            Intent intent = new Intent(context, ThresholdActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0); /*设置跳转 点击通知跳转页面*/
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(name + "报警")
                    .setContentText("阈值" + name + ": " + n1 + "  当前" + name + ": " + n2)
                    .setAutoCancel(true)              /*点击通知后 关闭通知*/
                    .setContentIntent(pendingIntent) /*设置跳转页面*/
                    .setSmallIcon(R.drawable.logo)   /*设置显示图标*/
                    .setWhen(System.currentTimeMillis())
                    .build();
            manager.notify(id, notification);
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "阈值服务关闭");
        isTrue = false;
        super.onDestroy();
        // ThresholdActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }
}
