package top.codepy.urbantraffic.ThresholdCatalog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
        editTextList.add((EditText) findViewById(R.id.et_cq2));           //cq2
        editTextList.add((EditText) findViewById(R.id.et_pm25));          //pm2.5
        editTextList.add((EditText) findViewById(R.id.et_Status));        //道路

    }
    /*监听Button 事件*/
    private void initButton() {
        btn_threshold = findViewById(R.id.btn_threshold);
        btn_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    /*监听ToggleButton 事件*/
    private void listenerToggle() {
        toggle = findViewById(R.id.toggle);
        toggle.setChecked(true);
        /*按钮不可编辑*/
        if (toggle.isChecked()) {
            for (EditText e : editTextList) {
                e.setFocusable(false);
                e.setFocusableInTouchMode(false);
                btn_threshold.setEnabled(false);
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
