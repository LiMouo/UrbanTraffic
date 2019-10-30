package top.codepy.urbantraffic.ThresholdCatalog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

public class ThresholdActivityback extends AppCompatActivity {
    private static final String TAG = "阈值设置(Threshold)";
    private List<EditText> editTextList;
    private ToggleButton toggle;
    private Button btn_threshold;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private Handler handler = new Handler();
    public static Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threshold);
        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        ToolbarMaster.setTitle("阈值设置");
        ToolbarMaster.MenuCreate();
        editor = getSharedPreferences("Threshold", MODE_PRIVATE).edit();
        preferences = getSharedPreferences("Threshold", MODE_PRIVATE);
        /*------------------------------------------------*/
        initEditText();    //控件EditText 设置
        initButton();      //控件Button   设置
        setService();      //设置服务是否启动 通过 开关按钮自动启动或不启动
        listenerToggle();  //开关按钮的监听
    }

    private void setService() {
        if (preferences.getBoolean("isThreshold", true)) {
            Intent startIntent = new Intent(ThresholdActivityback.this, ThresholdService.class);
            startService(startIntent); /*启动服务*/
        }
    }

    private void initEditText() {
        editTextList = new ArrayList<>();
        editTextList.add((EditText) findViewById(R.id.et_temperature));   //温度
        editTextList.add((EditText) findViewById(R.id.et_humidity));      //湿度
        editTextList.add((EditText) findViewById(R.id.et_LightIntensity));//光照
        editTextList.add((EditText) findViewById(R.id.et_co2));           //cq2
        editTextList.add((EditText) findViewById(R.id.et_pm25));          //pm2.5
        editTextList.add((EditText) findViewById(R.id.et_Status));        //道路
        editTextList.get(0).setText(preferences.getString("temperature", null));
        editTextList.get(0).setSelection(editTextList.get(0).length());
        editTextList.get(1).setText(preferences.getString("humidity", null));
        editTextList.get(2).setText(preferences.getString("LightIntensity", null));
        editTextList.get(3).setText(preferences.getString("co2", null));
        editTextList.get(4).setText(preferences.getString("pm25", null));
        editTextList.get(5).setText(preferences.getString("Status", null));
    }

    /*监听Button 事件*/
    private void initButton() {
        btn_threshold = findViewById(R.id.btn_threshold);
        btn_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveThreshold();
            }
        });
    }

    /*监听ToggleButton 事件*/
    private void listenerToggle() {
        toggle = findViewById(R.id.toggle);
        toggle.setChecked(preferences.getBoolean("isThreshold", true));
        /*按钮不可编辑*/
        if (toggle.isChecked()) {
            editor.putBoolean("isThreshold", true);
            for (EditText e : editTextList) {
                e.setFocusable(false);
                e.setFocusableInTouchMode(false);
                btn_threshold.setEnabled(false);
                e.requestFocus();
            }
        }
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.e(TAG, "ToggleButton开关 " + b);
                if (b) {
                    editor.putBoolean("isThreshold", b);
                    editor.apply();
                    saveThreshold();
                    Intent startIntent = new Intent(ThresholdActivityback.this, ThresholdService.class);
                    startService(startIntent); /*启动服务*/
                    for (EditText e : editTextList) {
                        e.setFocusable(false);              //不可获取 焦点
                        e.setFocusableInTouchMode(false);   //
                        btn_threshold.setEnabled(false);
                    }
                } else {
                    Intent stopIntent = new Intent(ThresholdActivityback.this, ThresholdService.class);
                    stopService(stopIntent); /*关闭服务*/
                    editor.putBoolean("isThreshold", b);
                    editor.apply();
                    saveThreshold();
                    for (int i = editTextList.size() - 1; i >= 0; i--) {
                        editTextList.get(i).setFocusable(true);
                        editTextList.get(i).setFocusableInTouchMode(true);
                        editTextList.get(i).requestFocus();
                        btn_threshold.setEnabled(true);
                    }
                }

            }
        });
    }

    /*保存数据方法*/
    private void saveThreshold() {
        String temperature = editTextList.get(0).getText().toString();
        String humidity = editTextList.get(1).getText().toString();
        String LightIntensity = editTextList.get(2).getText().toString();
        String co2 = editTextList.get(3).getText().toString();
        String pm25 = editTextList.get(4).getText().toString();
        String Status = editTextList.get(5).getText().toString();
        editor.putBoolean("isThreshold", toggle.isChecked());
        editor.putString("temperature", temperature);
        editor.putString("humidity", humidity);
        editor.putString("LightIntensity", LightIntensity);
        editor.putString("co2", co2);
        editor.putString("pm25", pm25);
        editor.putString("Status", Status);
        editor.apply();
        Log.e(TAG, "本地文件写入成功");
        editor.clear();
    }
}
