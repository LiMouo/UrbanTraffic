package top.codepy.urbantraffic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import top.codepy.urbantraffic.GuideCatalog.GuideActivity;
import top.codepy.urbantraffic.LoginCatalog.LoginActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String isFirstName = "isFirstName"; /*存储本地 key*/
    private SharedPreferences.Editor editor;    /*写入*/
    private SharedPreferences read;             /*读取*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //状态栏隐藏
        iniShard();
    }

    /*作用判断 第一次登陆*/
    private void iniShard() {
        Intent intent;
        read = getSharedPreferences(isFirstName, MODE_PRIVATE);
        Log.e(TAG, "跳转前isFirstIn: " + read.getBoolean(isFirstName, true));
        if (read.getBoolean(isFirstName, true)) {
            editor = getSharedPreferences(isFirstName, MODE_PRIVATE).edit();
            editor.putBoolean(isFirstName, false);
            editor.apply();
            intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
            finish();
        } else {
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
