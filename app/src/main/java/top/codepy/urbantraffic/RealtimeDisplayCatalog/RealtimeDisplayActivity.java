package top.codepy.urbantraffic.RealtimeDisplayCatalog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.SQLiteCatalog.SQLiteEnvironMaster;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

/**
 * 实时更新图表实现思路:
 *  1.
 */

public class RealtimeDisplayActivity extends AppCompatActivity {
    private static final String TAG = "实时显示 (RealtimeDisplay)";
    private ViewPager viewPager;
    public static List<View> viewData;
    public static List<TextView> textViewData;
    private List<LineChart> lineCharts;
    private SQLiteEnvironMaster environMaster;
    private SQLiteDatabase db;
    private Cursor cursor;
    private LineDataSet set;
    private Boolean isTrue = true;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_display);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        environMaster = new SQLiteEnvironMaster(this, "Environ.db");
        db = environMaster.getReadableDatabase();
        ToolbarMaster.setTitle("实时显示");
        ToolbarMaster.MenuCreate();
        initTextView();      //找到控件TextView
        initViewPager();     //设置viewPager 渲染
        //getSQLiteDate(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getSQLiteDate(0);
                            getSQLiteDate(1);
                            getSQLiteDate(2);
                            getSQLiteDate(3);
                            getSQLiteDate(4);
                            getSQLiteDate(5);
                        }
                    });
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (isTrue);
            }
        }).start();
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
    private void initTextView() {
        textViewData = new ArrayList<>();
        textViewData.add((TextView) findViewById(R.id.retv_1));
        textViewData.add((TextView) findViewById(R.id.retv_2));
        textViewData.add((TextView) findViewById(R.id.retv_3));
        textViewData.add((TextView) findViewById(R.id.retv_4));
        textViewData.add((TextView) findViewById(R.id.retv_5));
        textViewData.add((TextView) findViewById(R.id.retv_6));
    }

    /**
     * 用于设置 页面切换
     */
    private void initViewPager() {
        viewPager = findViewById(R.id.viewPager);
        viewData = new ArrayList<>();
        LayoutInflater inflater = getLayoutInflater();
        viewData.add(inflater.inflate(R.layout.realtime_view_1, null));
        viewData.add(inflater.inflate(R.layout.realtime_view_2, null));
        viewData.add(inflater.inflate(R.layout.realtime_view_3, null));
        viewData.add(inflater.inflate(R.layout.realtime_view_4, null));
        viewData.add(inflater.inflate(R.layout.realtime_view_5, null));
        viewData.add(inflater.inflate(R.layout.realtime_view_6, null));
        viewPager.setAdapter(new RealtimeViewAdapter());
        viewPager.setCurrentItem(0);
        textViewData.get(0).setBackgroundResource(R.drawable.realtime_back);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Log.e(TAG, "切换页面" + position);
                setTextView(position);
                //getSQLiteDate(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        /*找到每个viewPager 里的 lineChart*/
        lineCharts = new ArrayList<>();
        lineCharts.add((LineChart) viewData.get(0).findViewById(R.id.lineChart_1));
        lineCharts.add((LineChart) viewData.get(1).findViewById(R.id.lineChart_2));
        lineCharts.add((LineChart) viewData.get(2).findViewById(R.id.lineChart_3));
        lineCharts.add((LineChart) viewData.get(3).findViewById(R.id.lineChart_4));
        lineCharts.add((LineChart) viewData.get(4).findViewById(R.id.lineChart_5));
        lineCharts.add((LineChart) viewData.get(5).findViewById(R.id.lineChart_6));
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
    /*-------------------------------------------实现图表-----------------------------------------*/
    /**
     * 取得数据库数据
     *
     * @param pos 用来取到集合里每个 LineChart
     */
    private void getSQLiteDate(int pos) {
        cursor = db.query("environ", null, null, null, null, null, null);
        ArrayList<Entry> temperature_data = new ArrayList<>();
        ArrayList<Entry> humidity_data = new ArrayList<>();
        ArrayList<Entry> LightIntensity_data = new ArrayList<>();
        ArrayList<Entry> cq2_data = new ArrayList<>();
        ArrayList<Entry> pm25_data = new ArrayList<>();
        ArrayList<Entry> Status_data = new ArrayList<>();
        int count = 0;
        if (cursor.moveToFirst()) {
            do {
                String datetime = cursor.getString(cursor.getColumnIndex("datetime"));       //时间
                int temperature = cursor.getInt(cursor.getColumnIndex("temperature"));       //温度
                int humidity = cursor.getInt(cursor.getColumnIndex("humidity"));             //湿度
                int LightIntensity = cursor.getInt(cursor.getColumnIndex("LightIntensity")); //光照
                int cq2 = cursor.getInt(cursor.getColumnIndex("cq2"));                       //CQ2
                int pm25 = cursor.getInt(cursor.getColumnIndex("pm25"));                     //PM2.5
                int Status = cursor.getInt(cursor.getColumnIndex("Status"));                 //道路状态
                if (count == 0) {
                    temperature_data.add(new Entry(count, 0, "0"));
                    humidity_data.add(new Entry(count, 0, "0"));
                    LightIntensity_data.add(new Entry(count, 0, "0"));
                    cq2_data.add(new Entry(count, 0, "0"));
                    pm25_data.add(new Entry(count, 0, "0"));
                    Status_data.add(new Entry(count, 0, "0"));
                }
                temperature_data.add(new Entry(count += 3, temperature, datetime));
                humidity_data.add(new Entry(count, humidity, datetime));
                LightIntensity_data.add(new Entry(count, LightIntensity, datetime));
                cq2_data.add(new Entry(count, cq2, datetime));
                pm25_data.add(new Entry(count, pm25, datetime));
                Status_data.add(new Entry(count, Status, datetime));
            } while (cursor.moveToNext());
        }
        cursor.close();
        switch (pos) {
            case 0:
                setChartData(temperature_data, lineCharts.get(0));
                break;
            case 1:
                setChartData(humidity_data, lineCharts.get(1));
                break;
            case 2:
                setChartData(LightIntensity_data, lineCharts.get(2));
                break;
            case 3:
                setChartData(cq2_data, lineCharts.get(3));
                break;
            case 4:
                setChartData(pm25_data, lineCharts.get(4));
                break;
            case 5:
                setChartData(Status_data, lineCharts.get(5));
                break;
        }
    }

    private void setChartData(ArrayList<Entry> values, LineChart lineChart) {
        set = new LineDataSet(values, null);
        set.setFormLineWidth(1);
        set.setCircleColor(Color.BLACK); //设置顶点圆圈颜色
        set.setLineWidth(1);      //设置线条粗细
        set.setColor(Color.BLACK);  //设置线条颜色
        set.setDrawCircleHole(false);
        set.setFormLineWidth(1);
        set.setDrawValues(true);          //设置文子是否显示
        set.setValueTextColor(Color.WHITE); //文字显示颜色
        set.setValueTextSize(9f);         //设置文字显示大小
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        //添加数据集
        dataSets.add(set);
        //创建一个数据集的数据对象
        LineData lineData = new LineData(dataSets);
        setLincChart(lineChart, values); //设置整个图标的样式
        lineChart.setData(lineData);
        Log.e(TAG, "加载数据完成 " + lineChart);
    }

    private void setLincChart(LineChart lincChart, final ArrayList<Entry> values) {
        lincChart.setTouchEnabled(false);           /*设置不可触摸 不显示坐标辅助线*/
        lincChart.setLogEnabled(false);             /*设置不显示日志*/
        lincChart.setDrawBorders(false);            /*设置不显示表格边框线*/
        lincChart.setDescription(null);             /*设置图标的描述文子为空*/
        lincChart.notifyDataSetChanged(); // let the chart know it's data changed
        lincChart.invalidate(); // refresh
        /*--------------------------------------设置 X 坐标--------------------------------------*/
        XAxis xAxis = lincChart.getXAxis();
        xAxis.setAvoidFirstLastClipping(true);          /* 设置 true,将避免图表或屏幕的边缘的第一个和最后一个轴中的标签条目被裁剪*/
        xAxis.setAxisMinimum(3);                        /* 设置 最小值 从3 开始*/
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  /* 设置 X轴底部显示 */
        xAxis.setAxisLineWidth(1);                      /* 设置 显示宽度*/
        //xAxis.setDrawAxisLine(true);                  /* 设置 图标里的横线显示*/
        xAxis.setAxisLineColor(Color.WHITE);            /* 设置 X轴 线条颜色*/
        xAxis.setDrawGridLines(false);                  /* 设置 X轴 表格线 竖线 不显示 */
        xAxis.setEnabled(true);                         /* 设置 X轴 是否显示*/
        xAxis.setLabelCount(22);                        /* 设置 X轴显示的个数*/
        xAxis.setDrawLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                //Log.e("GG", "getFormattedValue: " + value);
                return values.get((int) value / 3).getData() + "";
            }
        });
        /*-------------------------------万恶的分割线 设置Y轴 Left--------------------------------*/
        YAxis LyAxis = lincChart.getAxisLeft();           /* 获得 最左边的Y轴*/
        LyAxis.setDrawGridLines(true);                   /* 设置 Y轴表格线 不显示 */
        LyAxis.setGridColor(Color.WHITE);
        LyAxis.setDrawAxisLine(true);                     /* 设置 Y轴轴线 是否显示*/
        LyAxis.setEnabled(true);                          /* 设置 Y轴 是否显示*/
        LyAxis.setAxisLineWidth(1);                       /* 设置 Y轴 显示宽度*/
        LyAxis.setAxisLineColor(Color.WHITE);             /* 设置 Y轴 线条颜色*/
        //LyAxis.setAxisMinimum(0);                         /* 设置 Y轴 初始值*/
        //LyAxis.setAxisMaximum(100);                       /* 设置 Y轴 最大值*/
        /*-------------------------------万恶的分割线 设置Y轴 Right--------------------------------*/
        YAxis RyAxis = lincChart.getAxisRight();          /* 获得 最右边的Y轴*/
        RyAxis.setDrawGridLines(false);                   /* 设置 Y轴表格线 不显示 */
        RyAxis.setDrawAxisLine(true);                     /* 设置 Y轴轴线 是否显示*/
        RyAxis.setEnabled(false);                         /* 设置 Y轴R 是否显示*/
        RyAxis.setAxisLineWidth(1);                       /* 设置 Y轴 显示宽度*/
        //RyAxis.setAxisMinimum(0);                         /* 设置 Y轴 初始值*/
        //RyAxis.setAxisMaximum(100);                      /* 设置 Y轴 最大值*/
    }
    @Override
    protected void onStop() {
        super.onStop();
        isTrue = false;
    }
}

