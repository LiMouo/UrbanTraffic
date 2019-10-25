package top.codepy.urbantraffic.ViolationCatalog;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.ToolsCatalog.OkHttpData;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

/**
 * 实现思路:
 * 1. RadioGroup 两个 RadioButton 改 shape item state_checked 可以按钮切换 显示不同背景色
 * 2. 下面使用ViewPager 生成两个layout 一个用于 （违章视屏） 一个用于显示 （违章图片） 布局里都采用 GridView
 * 3. 逻辑实现 先是播放: 我把数据 视屏 都上传到服务器上 做一个 json 文件存储 视屏信息
 * 4. get 服务器上的数据 处理成list 好用于提取信息
 * 5. 创建一个视屏播放的activity VideosActivity.java
 * 6. 在 GridView adapter 中 用intent 传入 需要播放的视屏地址 并跳转 然后在 VideosActivity中 提取数据 并播放
 */
public class ViolationActivity extends AppCompatActivity {
    private View view1, view2;
    private static final String TAG = "ViolationActivity";
    private GridView video_grid;
    private GridView images_grid;
    private ViewPager viewpager_view;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private RadioButton radio1;
    private RadioButton radio2;
    private LinearLayout danger;
    private LinearLayout loging;
    private Handler handler = new Handler();
    public static List<View> views;
    public static Context context;
    List<Map<String, String>> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        this.context = this;
        ToolbarMaster.MenuCreate(); /*创建Menu 菜单*/
        ToolbarMaster.setTitle("车辆违章");
        iniGetData();
        iniButton();
        /*--------------上面别动------------------*/
        radio1 = findViewById(R.id.bottom_1);
        radio2 = findViewById(R.id.bottom_2);
        viewpager_view = findViewById(R.id.viewpager_view);
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.viewpager_video, null);
        view2 = inflater.inflate(R.layout.viewpager_images, null);
        views = new ArrayList<>();
        views.add(view1);
        views.add(view2);
        viewpager_view.setAdapter(new ViewPagerVideo());
        viewpager_view.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    radio1.setChecked(true);
                } else {
                    radio2.setChecked(true);
                }
                Log.e(TAG, "切换页面" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //viewpager_view.setCurrentItem(0); /*注意: 这个是设置跳转到你第几个 viewpager*/
        video_grid = view1.findViewById(R.id.video_grid);
        images_grid = view2.findViewById(R.id.images_grid);
        danger = view1.findViewById(R.id.danger);
        loging = view1.findViewById(R.id.loging);
        images_grid.setAdapter(new ImagesAdapter(this));
        /*--------------Viewpager上面------------------*/
    }

    /*得到视频地址*/
    private void iniGetData() {
        //video_grid = findViewById(R.id.video_grid);
        final String url = "http://192.168.3.3:81/Viode/videos.json";
        new Thread(new Runnable() {

            @Override
            public void run() {
                OkHttpData.sendConnect(url, "");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e(TAG, "返回视频数据:" + OkHttpData.JsonArrayRead().toString());
                            listData = OkHttpData.JsonArrayRead();
                            if (OkHttpData.JsonArrayRead().size() == 0) {
                                Log.e(TAG, "run: 执行了");
                                danger.setVisibility(View.VISIBLE);
                            } else {
                                video_grid.setAdapter(new GridAdapter(ViolationActivity.this, listData));
                                danger.setVisibility(View.GONE);
                                loging.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            danger.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

    /**/
    private void iniButton() {
        radioGroup = findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup radioGroup, int i) {
                radioButton = radioGroup.findViewById(i);
                Log.e(TAG, "" + radioButton.getText());
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (radioButton == radio1) {
                            viewpager_view.setCurrentItem(0);
                        } else {
                            viewpager_view.setCurrentItem(1);
                        }
                     //   Toast.makeText(ViolationActivity.this, radioButton.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop 活动完全不可见");
        OkHttpData.jsonData = null;
    }
}
