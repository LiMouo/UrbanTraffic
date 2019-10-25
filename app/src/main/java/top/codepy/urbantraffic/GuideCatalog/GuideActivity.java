package top.codepy.urbantraffic.GuideCatalog;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import top.codepy.urbantraffic.LoginCatalog.LoginActivity;
import top.codepy.urbantraffic.R;

public class GuideActivity extends Activity {
    private static final String TAG = "GuideActivity";
    Handler handler = new Handler();
    private TextView tv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //状态栏隐藏
        tv_logo = findViewById(R.id.tv_logo);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 70; i++) {
                    final float progress1 = i / 60f;
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("yan", progress1 + "");
                                tv_logo.setBackgroundColor(evaluateColor(0xFF009688, 0x00342000, progress1));
                                //tv_logo.setText("加载中");
                            }
                        });
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).start();
    }

    private int evaluateColor(int startValue, int endValue, float fraction) {
        if (fraction <= 0) {
            return startValue;
        }
        if (fraction >= 1) {
            return endValue;
        }
        int startInt = startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return ((startA + (int) (fraction * (endA - startA))) << 24) | ((startR + (int) (fraction * (endR - startR))) << 16) | ((startG + (int) (fraction * (endG - startG))) << 8) | ((startB + (int) (fraction * (endB - startB))));
    }
}
