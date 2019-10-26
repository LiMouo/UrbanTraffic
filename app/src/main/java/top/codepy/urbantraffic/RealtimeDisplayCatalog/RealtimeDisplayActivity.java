package top.codepy.urbantraffic.RealtimeDisplayCatalog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

public class RealtimeDisplayActivity extends AppCompatActivity {
    private static final String TAG = "实时显示 (RealtimeDisplay)";
    private LineChart chart;
    private ViewPager viewPager;
    public static List<View> viewData;
    private View view_1, view_2, view_3, view_4, view_5, view_6;
    public static List<TextView> textViewData;
    private TextView retv_1, retv_2, retv_3, retv_4, retv_5, retv_6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_display);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        ToolbarMaster.setTitle("实时显示");
        ToolbarMaster.MenuCreate();
        initTextview();      //找到控件TextView
        initViewPager();     //设置viewPager 渲染

    }

    /*------------------------------------实现圆圈跟随页面而移动-----------------------------------*/

    /**
     * 实现思路:
     * 1. 创建6个 用户需要切换的页面
     * 2. 找到 圆圈的id 存入List<TextView></> 中 好用户渲染圆圈颜色
     * 3. 监听 ViewPager 页面切换事件 onPageSelected 里是 用户切换到那一页了
     * 二. 写个方法用来 设置圆圈跟随效果 大概就是 把当前页面渲染 成 灰色 其他页面保持白色
     * 1.把切换到当前页面的下标 传给 上面 自定义的方法中  然后 循环 不是当前页面的 全不渲染成白色
     * 2. 然后把用户切换到的当前页面 渲染成 灰色
     */
    private void initTextview() {
        textViewData = new ArrayList<>();
        retv_1 = findViewById(R.id.retv_1);
        retv_2 = findViewById(R.id.retv_2);
        retv_3 = findViewById(R.id.retv_3);
        retv_4 = findViewById(R.id.retv_4);
        retv_5 = findViewById(R.id.retv_5);
        retv_6 = findViewById(R.id.retv_6);
        textViewData.add(retv_1);
        textViewData.add(retv_2);
        textViewData.add(retv_3);
        textViewData.add(retv_4);
        textViewData.add(retv_5);
        textViewData.add(retv_6);
    }

    /**
     * 用于设置 页面切换
     */
    private void initViewPager() {
        viewPager = findViewById(R.id.viewPager);
        viewData = new ArrayList<>();
        LayoutInflater inflater = getLayoutInflater();
        view_1 = inflater.inflate(R.layout.realtime_view_1, null);
        view_2 = inflater.inflate(R.layout.realtime_view_2, null);
        view_3 = inflater.inflate(R.layout.realtime_view_3, null);
        view_4 = inflater.inflate(R.layout.realtime_view_4, null);
        view_5 = inflater.inflate(R.layout.realtime_view_5, null);
        view_6 = inflater.inflate(R.layout.realtime_view_6, null);
        viewData.add(view_1);
        viewData.add(view_2);
        viewData.add(view_3);
        viewData.add(view_4);
        viewData.add(view_5);
        viewData.add(view_6);
        viewPager.setAdapter(new RealtimeViewAdapter());
        viewPager.setCurrentItem(0);
        retv_1.setBackgroundResource(R.drawable.realtime_back);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Log.e(TAG, "切换页面" + position);
                setTextView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 设置当滑动到viewpager 圆圈的颜色
     */
    private void setTextView(int pos) {
        for (int i = 0; i < textViewData.size(); i++) {
            if (i != pos) {
                textViewData.get(i).setBackgroundResource(R.drawable.realtime_r);
            }
        }
        textViewData.get(pos).setBackgroundResource(R.drawable.realtime_back);
    }
}
