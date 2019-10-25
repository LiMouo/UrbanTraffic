package top.codepy.urbantraffic.TrafficLightsCatalog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.ToolsCatalog.OkHttpData;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

public class TrafficLightsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TrafficLightsActivity";
    private QNumberPicker picker;
    private Button btn_query;
    private View view;
    private int[] index = {1, 2, 3, 4, 5};
    private String[] arr = {"路口升序", "路口降序", "红灯升序", "红灯降序", "绿灯升序", "绿灯降序", "黄灯升序", "黄灯降序"};
    private RecyclerView recyclerView;
    private int count = 0; /*排序下标*/
    private Map<String, String> map;
    private Handler handler = new Handler();
    private List<Map<String, String>> mListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_lights);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        ToolbarMaster.setTitle("红灯绿管理");
        ToolbarMaster.MenuCreate();
        SetNumberPicker();
        view = findViewById(R.id.view);
        btn_query = findViewById(R.id.btn_query);
        recyclerView = findViewById(R.id.recycler_show);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        //   recyclerView.setAdapter(new RecyclerAdapter(this, mListData));
        recyclerView.addItemDecoration(new MyDecoration());
        btn_query.setOnClickListener(this);
        getDate();
    }

    private void getDate() {
        final String url = "http://192.168.3.5:8088/transportservice/action/GetTrafficLightConfigAction.do";
        final List<Map<String, String>> listData = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {//循环取得路灯数据
                for (int j = 0; j < index.length; j++) {
                    map = new HashMap<>();
                    map.put("TrafficLightId", String.valueOf(index[j]));
                    map.put("UserName", "user1");
                    JSONObject jsonObject = new JSONObject(map);
                    Log.e(TAG, "每个请求的Json: " + jsonObject.toString());
                    final String json = jsonObject.toString();
                    OkHttpData.sendConnect(url, json);
                    Log.e(TAG, "每行数据: " + OkHttpData.JsonObjectRead());
                    map = new HashMap<>();
                    try {
                        map.put("id", String.valueOf(j + 1));
                        map.put("YellowTime", OkHttpData.JsonObjectRead().getString("YellowTime"));
                        map.put("GreenTime", OkHttpData.JsonObjectRead().getString("GreenTime"));
                        map.put("RedTime", OkHttpData.JsonObjectRead().getString("RedTime"));
                        listData.add(map);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "线程取到的值: " + listData.toString());
                        mListData = listData;
                        view.setVisibility(View.VISIBLE);
                        setListData(count);

                    }
                });
            }
        }).start();
    }

    private void SetNumberPicker() {
        picker = findViewById(R.id.picker);
        picker.setDisplayedValues(arr);
        picker.setMinValue(0); //最开始的那个下标
        picker.setMaxValue(arr.length - 1);
        picker.setValue(0); //默认显示位置
        picker.setWrapSelectorWheel(false); /*不循环显示 false  循环显示 */
        setNumberPickerDividerColor(picker);
        //    picker.setDescendantFocusability(NumberPicker.FOCUS_AFTER_DESCENDANTS); /*不可编辑*/
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.e(TAG, "onValueChange " + i1 + " " + arr[i1]);
                count = i1;
            }
        });
    }

    private void setNumberPickerDividerColor(NumberPicker numberPicker) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(numberPicker, new ColorDrawable(getResources().getColor(R.color.transparent)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_query) {
            setListData(count);
            view.setVisibility(View.VISIBLE);
        }
    }

    private void setListData(int count) {
        switch (count) {
            case 0:/*路口升序*/
                Toast.makeText(this, "路口升序", Toast.LENGTH_SHORT).show();
                Collections.sort(mListData, new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> t0, Map<String, String> t1) {
                        if (Integer.parseInt(t0.get("id")) > Integer.parseInt(t1.get("id"))) {
                            return 1;
                        }
                        return -1;
                    }
                });
                recyclerView.setAdapter(new RecyclerAdapter(this, mListData));
                break;
            case 1:/*路口降序*/
                Toast.makeText(this, "路口降序", Toast.LENGTH_SHORT).show();
                Collections.sort(mListData, new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> t0, Map<String, String> t1) {
                        if (Integer.parseInt(t0.get("id")) < Integer.parseInt(t1.get("id"))) {
                            return 1;
                        }
                        return -1;
                    }
                });
                recyclerView.setAdapter(new RecyclerAdapter(this, mListData));
                break;
            case 2:/*红灯升序*/
                Toast.makeText(this, "红灯升序", Toast.LENGTH_SHORT).show();
                Collections.sort(mListData, new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> t0, Map<String, String> t1) {
                        if (Integer.parseInt(t0.get("RedTime")) > Integer.parseInt(t1.get("RedTime"))) {
                            return 1;
                        }
                        return -1;
                    }
                });
                recyclerView.setAdapter(new RecyclerAdapter(this, mListData));
                break;
            case 3:/*红灯降序*/
                Toast.makeText(this, "红灯降序", Toast.LENGTH_SHORT).show();
                Collections.sort(mListData, new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> t0, Map<String, String> t1) {
                        if (Integer.parseInt(t0.get("RedTime")) < Integer.parseInt(t1.get("RedTime"))) {
                            return 1;
                        }
                        return -1;
                    }
                });
                recyclerView.setAdapter(new RecyclerAdapter(this, mListData));
                break;
            case 4:/*绿灯升序*/
                Toast.makeText(this, "绿灯升序", Toast.LENGTH_SHORT).show();
                Collections.sort(mListData, new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> t0, Map<String, String> t1) {
                        if (Integer.parseInt(t0.get("GreenTime")) > Integer.parseInt(t1.get("GreenTime"))) {
                            return 1;
                        }
                        return -1;
                    }
                });
                recyclerView.setAdapter(new RecyclerAdapter(this, mListData));
                break;
            case 5:/*绿灯降序*/
                Toast.makeText(this, "绿灯降序", Toast.LENGTH_SHORT).show();
                Collections.sort(mListData, new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> t0, Map<String, String> t1) {
                        if (Integer.parseInt(t0.get("GreenTime")) < Integer.parseInt(t1.get("GreenTime"))) {
                            return 1;
                        }
                        return -1;
                    }
                });
                recyclerView.setAdapter(new RecyclerAdapter(this, mListData));
                break;
            case 6:/*黄灯升序*/
                Toast.makeText(this, "黄灯升序", Toast.LENGTH_SHORT).show();
                Collections.sort(mListData, new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> t0, Map<String, String> t1) {
                        if (Integer.parseInt(t0.get("YellowTime")) > Integer.parseInt(t1.get("YellowTime"))) {
                            return 1;
                        }
                        return -1;
                    }
                });
                recyclerView.setAdapter(new RecyclerAdapter(this, mListData));
                break;
            case 7:/*黄灯降序*/
                Toast.makeText(this, "黄灯降序", Toast.LENGTH_SHORT).show();
                Collections.sort(mListData, new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> t0, Map<String, String> t1) {
                        if (Integer.parseInt(t0.get("YellowTime")) < Integer.parseInt(t1.get("YellowTime"))) {
                            return 1;
                        }
                        return -1;
                    }
                });
                recyclerView.setAdapter(new RecyclerAdapter(this, mListData));
                break;
        }
    }

    class MyDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 0, 1, 1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        OkHttpData.jsonData = null;
    }
}
