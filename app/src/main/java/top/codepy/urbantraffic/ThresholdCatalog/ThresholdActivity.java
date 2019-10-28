package top.codepy.urbantraffic.ThresholdCatalog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

public class ThresholdActivity extends AppCompatActivity {
    private static final String TAG = "阈值设置(Threshold)";
    private List<EditText> editTextList;
    private ToggleButton toggle;
    private Button btn_threshold;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threshold);
        ActionBar actionBar = getSupportActionBar();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (actionBar != null) {
            actionBar.hide();
        }
        ToolbarMaster.setTitle("阈值设置");
        ToolbarMaster.MenuCreate();
        editor = getSharedPreferences("Threshold", MODE_PRIVATE).edit();
        preferences = getSharedPreferences("Threshold", MODE_PRIVATE);
        /*------------------------------------------------*/
        initEditText();    //控件EditText找到
        initButton();
        listenerToggle();
    }

    private void initEditText() {
        editTextList = new ArrayList<>();
        editTextList.add((EditText) findViewById(R.id.et_temperature));   //温度
        editTextList.add((EditText) findViewById(R.id.et_humidity));      //湿度
        editTextList.add((EditText) findViewById(R.id.et_LightIntensity));//光照
        editTextList.add((EditText) findViewById(R.id.et_co2));           //cq2
        editTextList.add((EditText) findViewById(R.id.et_pm25));          //pm2.5
        editTextList.add((EditText) findViewById(R.id.et_Status));        //道路

    }

    /*监听Button 事件*/
    private void initButton() {
        btn_threshold = findViewById(R.id.btn_threshold);
        btn_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temperature = editTextList.get(0).getText().toString();
                String humidity = editTextList.get(1).getText().toString();
                String LightIntensity = editTextList.get(2).getText().toString();
                String co2 = editTextList.get(3).getText().toString();
                String pm25 = editTextList.get(4).getText().toString();
                String Status = editTextList.get(5).getText().toString();

                editor.putBoolean("isThreshold", true);
                editor.putString("temperature", temperature);
                editor.putString("humidity", humidity);
                editor.putString("LightIntensity", LightIntensity);
                editor.putString("co2", co2);
                editor.putString("pm25", pm25);
                editor.putString("Status", Status);
                editor.apply();
            }
        });
    }

    /*监听ToggleButton 事件*/
    private void listenerToggle() {
        toggle = findViewById(R.id.toggle);
//        if (preferences.getBoolean("isThreshold", false)) {
//            toggle.setChecked(true);
//            /*按钮不可编辑*/
//            if (toggle.isChecked()) {
//                for (EditText e : editTextList) {
//                    e.setFocusable(false);
//                    e.setFocusableInTouchMode(false);
//                    btn_threshold.setEnabled(false);
//                    e.requestFocus();
//                }
//            }
//        } else {
//            Toast.makeText(this, "未设置初始阈值 请设置初始阈值", Toast.LENGTH_SHORT).show();
//            toggle.setChecked(false);
//            if (toggle.isChecked()) {
//                for (EditText e : editTextList) {
//                    e.setFocusable(true);
//                    e.setFocusableInTouchMode(true);
//                    btn_threshold.setEnabled(true);
//                    e.requestFocus();
//                }
//            }
//        }
        toggle.setChecked(true);
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
                    for (EditText e : editTextList) {
                        e.setFocusable(false);              //不可获取 焦点
                        e.setFocusableInTouchMode(false);   //
                        btn_threshold.setEnabled(false);
                    }
                } else {
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

}
