package top.codepy.urbantraffic.ETCCatalog;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.SQLiteCatalog.SQLiteBillMaster;
import top.codepy.urbantraffic.SQLiteCatalog.SQLiteMaster;
import top.codepy.urbantraffic.ToolsCatalog.OkHttpData;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

public class ETCActivity extends Activity {
    private static final String TAG = "ETCActivity";
    private Spinner spinner;
    private TextView show_money;
    private EditText in_money;
    private Button btn_query;
    private Button btn_inmoney;
    private int count = 1;
    private Handler handler = new Handler();
    private SQLiteBillMaster billMaster;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etc);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //状态栏隐藏
        ToolbarMaster.MenuCreate(); /*创建Menu 菜单*/
        ToolbarMaster.setTitle("账户管理");
        billMaster = new SQLiteBillMaster(this,"Bill.db");
        db = billMaster.getWritableDatabase();
        setToolbar();
        queryMoney();
        getDate();
    }

    /*透明状态栏*/
    protected void setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void setToolbar() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        spinner = findViewById(R.id.spinner);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_itme, list);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "选着: " + (i + 1) + " 车");
                count = (i + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        show_money = findViewById(R.id.show_money);
        in_money = findViewById(R.id.in_money);
        btn_query = findViewById(R.id.btn_query);
        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryMoney();
            }
        });
        btn_inmoney = findViewById(R.id.btn_inmoney);
        btn_inmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMoney();
            }
        });
    }

    private void queryMoney() {
        final String url = "http://192.168.3.5:8088/transportservice/action/GetCarAccountBalance.do";
        Map<String, String> map = new HashMap<>();
        map.put("CarId", String.valueOf(count));
        map.put("UserName", "user1");
        JSONObject jsonObject = new JSONObject(map);
        final String json = jsonObject.toString();
        Log.e(TAG, "封装JSON数据用于请求:" + jsonObject.toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpData.sendConnect(url, json);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e(TAG, "返回数据: " + OkHttpData.JsonObjectRead().toString());
                            if (OkHttpData.JsonObjectRead().get("ERRMSG").toString().equals("成功")) {
                                Log.e(TAG, "查询成功");
                                Toast.makeText(ETCActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                                show_money.setText(OkHttpData.JsonObjectRead().get("Balance").toString());
                                in_money.setText("");
                            } else {
                                Toast.makeText(ETCActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ETCActivity.this, "Error 查询失败 请检车是否连接到服务器网络", Toast.LENGTH_SHORT).show();
                            show_money.setText("null");
                        }

                    }
                });
            }
        }).start();
    }

    private void updateMoney() {
        final String url = "http://192.168.3.5:8088/transportservice/action/SetCarAccountRecharge.do";
        Map<String, String> map = new HashMap<>();
        map.put("CarId", String.valueOf(count));
        map.put("Money", in_money.getText().toString());
        map.put("UserName", "user1");
        JSONObject jsonObject = new JSONObject(map);
        final String json = jsonObject.toString();
        Log.e(TAG, "封装JSON数据用于请求:" + jsonObject.toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpData.sendConnect(url, json);
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Log.e(TAG, "返回数据: " + OkHttpData.JsonObjectRead().toString());
                            if (OkHttpData.JsonObjectRead().get("ERRMSG").toString().equals("成功")) {
                                Log.e(TAG, "充值成功");
                                Toast.makeText(ETCActivity.this, "充值成功", Toast.LENGTH_SHORT).show();
                                queryMoney();
                                ContentValues values = new ContentValues();
                                values.put("car_id",count);
                                values.put("money",in_money.getText().toString());
                                values.put("user","user1");
                                values.put("datetime",getDate());
                                db.insert(" bill",null,values);
                                values.clear();
                                db.close();
                                Log.e(TAG, "run: 写入成功" );
                            } else {
                                Toast.makeText(ETCActivity.this, "充值失败 请检查充值金额 或 网络", Toast.LENGTH_SHORT).show();
                            }
                            if (OkHttpData.JsonObjectRead().get("status").toString().equals("500")) {
                                Toast.makeText(ETCActivity.this, "请求失败" + OkHttpData.JsonObjectRead().get("status").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            try {
                                Toast.makeText(ETCActivity.this, "请求失败  " + OkHttpData.JsonObjectRead().get("status").toString(), Toast.LENGTH_SHORT).show();
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } catch (Exception e) {
                            Toast.makeText(ETCActivity.this, "Error 充值失败 请检车是否连接到服务器网络", Toast.LENGTH_SHORT).show();
                            show_money.setText("null");
                        }
                    }
                });
            }
        }).

                start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop 活动完全不可见");
        OkHttpData.jsonData = null;
    }

    private static String getDate(){
        Date date = new Date();
        Log.e(TAG, "getDate: "+date.toString());
        return date.toString();
    }
}
