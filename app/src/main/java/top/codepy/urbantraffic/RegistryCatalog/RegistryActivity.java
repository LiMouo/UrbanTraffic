package top.codepy.urbantraffic.RegistryCatalog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import top.codepy.urbantraffic.LoginCatalog.LoginActivity;
import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.SQLiteCatalog.SQLiteMaster;

public class RegistryActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "RegistryActivity";
    private Toolbar toolbar_registry;
    private Button btn_network;
    private Button btn_registry;
    private EditText registry_user;
    private EditText registry_pass;
    private EditText registry_pass_2;
    private EditText registry_phone;
    private SQLiteMaster masterDB;
    private SQLiteDatabase db;
    private Boolean inUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //状态栏隐藏
//        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
        masterDB = new SQLiteMaster(this,"users.db");
        db = masterDB.getWritableDatabase();
        iniFbiControl();
        btn_registry.setOnClickListener(this);
        iniFUserPass(); /*判断用户为空 用户存在数据库 用户密码为空*/
    }

    /*判断用户为空 用户存在数据库 用户密码为空 电话*/
    private void iniFUserPass() {
        registry_user.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (registry_user.length() == 0) {
                        Toast.makeText(RegistryActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    }
                    Cursor cursor = db.query("Users", null, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            String name = cursor.getString(cursor.getColumnIndex("username"));
                            if (registry_user.getText().toString().equals(name)) {
                                Toast.makeText(RegistryActivity.this, "用户已存在", Toast.LENGTH_SHORT).show();
                                inUser = false;
                            }
                        } while (cursor.moveToNext());
                    }
                }
            }
        });
    }

    private void iniFbiControl() {
        toolbar_registry = findViewById(R.id.toolbar_registry);
        toolbar_registry.setNavigationIcon(R.drawable.back);
        toolbar_registry.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistryActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_network = findViewById(R.id.btn_network);
        btn_registry = findViewById(R.id.btn_registry_1);
        registry_user = findViewById(R.id.registry_user);
        registry_pass = findViewById(R.id.registry_pass);
        registry_pass_2 = findViewById(R.id.registry_pass_2);
        registry_phone = findViewById(R.id.registry_phone);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_registry_1:
                Log.e(TAG, "注册点击");
                registry();
                break;
        }
    }

    private void registry() {
        ContentValues values = new ContentValues();
        if (registry_user.length() > 0 && registry_pass.getText().toString().equals(registry_pass_2.getText().toString())) {
            Log.e(TAG, "输入逻辑没毛病");
            if (inUser) {
                values.put("username", registry_user.getText().toString());
                values.put("password", registry_pass.getText().toString());
                values.put("phone", registry_phone.getText().toString());
                db.insert("Users", null, values);
                values.clear();
                Log.e(TAG, "写入完成");
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegistryActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "我他妈 说了用户已存在了啊 gan", Toast.LENGTH_SHORT).show();
            }
        }
        if (registry_user.length() == 0 || registry_pass.length() == 0 || registry_pass_2.length() == 0) {
            Toast.makeText(this, "用户名 密码 确认密码不能为空", Toast.LENGTH_SHORT).show();
        }
    }
}
