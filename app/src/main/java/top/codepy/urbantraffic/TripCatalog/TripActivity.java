package top.codepy.urbantraffic.TripCatalog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

public class TripActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = "出行管理 (TripActivity)";
    private Handler handler = new Handler();
    private List<ToggleButton> listToggleButton; //红绿灯变换
    private List<TextView> listTextView; //红绿灯变换

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        ToolbarMaster.setTitle("出行管理");
        ToolbarMaster.MenuCreate();
        initTextView();
        initToggle();
    }

    private void initTextView() {
        listTextView = new ArrayList<>();
        listTextView.add((TextView) findViewById(R.id.carId_1));
        listTextView.add((TextView) findViewById(R.id.carId_2));
        listTextView.add((TextView) findViewById(R.id.carId_3));
        listTextView.add((TextView) findViewById(R.id.date)); //时间显示
        listTextView.get(3).setOnClickListener(this);
        listTextView.get(3).setText(getdate());
    }

    private void initToggle() {
        listToggleButton = new ArrayList<>();
        listToggleButton.add((ToggleButton) findViewById(R.id.text_red));
        listToggleButton.add((ToggleButton) findViewById(R.id.text_yellow));
        listToggleButton.add((ToggleButton) findViewById(R.id.text_green));
        listToggleButton.add((ToggleButton) findViewById(R.id.tog_one)); //按钮ID
        listToggleButton.add((ToggleButton) findViewById(R.id.tog_two));
        listToggleButton.add((ToggleButton) findViewById(R.id.tog_three));
        setDate(getdate());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    for (int i = 0; i < 3; i++) {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        final int finalI = i;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                setToggle(finalI);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void setToggle(int x) {
        switch (x) {
            case 0:
                listToggleButton.get(0).setChecked(true);
                listToggleButton.get(1).setChecked(false);
                listToggleButton.get(2).setChecked(false);
                break;
            case 1:
                listToggleButton.get(0).setChecked(false);
                listToggleButton.get(1).setChecked(true);
                listToggleButton.get(2).setChecked(false);
                break;
            case 2:
                listToggleButton.get(0).setChecked(false);
                listToggleButton.get(1).setChecked(false);
                listToggleButton.get(2).setChecked(true);
                break;

        }
    }

    private static String getdate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        String time = format.format(date);
        Log.e(TAG, "得到时间: " + time);
        return time;
    }

    private void setDate(String date) {
        listTextView.get(3).setText(date);
        int day = Integer.parseInt(date.substring(date.length() - 3, date.length() - 1));
        Log.e(TAG, "日期: " + day); //当前日期
        if (day % 2 == 0) {
            Log.e(TAG, "双号");
            listToggleButton.get(3).setChecked(false);
            listToggleButton.get(3).setClickable(false);
            listToggleButton.get(4).setChecked(true);
            listToggleButton.get(4).setClickable(true);
            listToggleButton.get(4).setOnCheckedChangeListener(this);
            listToggleButton.get(5).setChecked(false);
            listToggleButton.get(5).setClickable(false);
            listTextView.get(0).setVisibility(View.GONE);
            listTextView.get(1).setVisibility(View.VISIBLE);
            listTextView.get(2).setVisibility(View.GONE);
        } else {
            Log.e(TAG, "单号");
            listToggleButton.get(3).setChecked(true);
            listToggleButton.get(3).setClickable(true);
            listToggleButton.get(3).setOnCheckedChangeListener(this);
            listToggleButton.get(4).setChecked(false);
            listToggleButton.get(4).setClickable(false);
            listToggleButton.get(5).setChecked(true);
            listToggleButton.get(5).setClickable(true);
            listToggleButton.get(5).setOnCheckedChangeListener(this);
            listTextView.get(0).setVisibility(View.VISIBLE);
            listTextView.get(1).setVisibility(View.GONE);
            listTextView.get(2).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.tog_one:
                if (b) {
                    listTextView.get(0).setVisibility(View.VISIBLE);
                } else {
                    listTextView.get(0).setVisibility(View.GONE);
                }
                break;
            case R.id.tog_two:
                if (b) {
                    listTextView.get(1).setVisibility(View.VISIBLE);
                } else {
                    listTextView.get(1).setVisibility(View.GONE);
                }
                break;
            case R.id.tog_three:
                if (b) {
                    listTextView.get(2).setVisibility(View.VISIBLE);
                } else {
                    listTextView.get(2).setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date:
                Calendar calendar = Calendar.getInstance(); //得到系统时间
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Log.e(TAG, "用户输入日期: " + i + "年" + i1 + "月" + i2 + "日");
                        String date = i + "年" + i1 + "月" + i2 + "日";
                        setDate(date);
                    }
                }, mYear, mMonth, mDay);
                pickerDialog.show();
                break;
        }
    }
}
