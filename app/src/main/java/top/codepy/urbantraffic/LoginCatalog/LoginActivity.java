package top.codepy.urbantraffic.LoginCatalog;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import top.codepy.urbantraffic.ETCCatalog.ETCActivity;
import top.codepy.urbantraffic.NetworkSettingCatalog.NetworkSettingActivity;
import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.RegistryCatalog.RegistryActivity;
import top.codepy.urbantraffic.SQLiteCatalog.SQLiteMaster;

public class LoginActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private Toolbar toolbar_title;
    private Button btn_network;     /*网络设置*/
    private EditText edit_user;     /*用户*/
    private EditText edit_pass;     /*密码*/
    private CheckBox save_user;     /*记住用户*/
    private CheckBox save_login;    /*自动登录*/
    private Button btn_login;       /*登录按钮*/
    private Button btn_registry;    /*注册按钮*/
    private SQLiteMaster masterDB;  /**/
    private SQLiteDatabase db;
    private Boolean isToast = true;

    private static final String SaveUserFile = "SaveUserFile"; /*保存用户信息的 文件名*/
    private static final String SaveUserName = "SaveUserName"; /*保存用户信息的 Key*/
    private static final String SaveUserPass = "SaveUserPass"; /*保存用户信息的 Key*/
    private static final String SaveIsBoolen = "SaveIsBoolen"; /*保存用户信息的 记住状态*/
    private static final String SaveLoginBoolen = "SaveLoginBoolen"; /*保存自动登陆 记住状态*/
    private SharedPreferences.Editor editor;
    private SharedPreferences readuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //状态栏隐藏
        masterDB = new SQLiteMaster(this, "users.db");
        db = masterDB.getReadableDatabase();
        readuser = getSharedPreferences(SaveUserFile, MODE_PRIVATE);
        editor = getSharedPreferences(SaveUserFile, MODE_PRIVATE).edit();
        inFindControl();
        setToolbarTitle(); /*设置标题栏 */
    }

    private void inFindControl() {
        edit_user = findViewById(R.id.edit_user);
        edit_pass = findViewById(R.id.edit_pass);
        save_user = findViewById(R.id.save_user);
        save_login = findViewById(R.id.save_login);
        btn_login = findViewById(R.id.btn_login);
        btn_registry = findViewById(R.id.btn_registr);
        btn_login.setOnClickListener(this);
        btn_registry.setOnClickListener(this);
    }

    private void iFLogin() {
        Cursor cursor = db.query("Users", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String password = cursor.getString(cursor.getColumnIndex("password"));
                Log.e(TAG, "username: " + username);
                Log.e(TAG, "password: " + password);
                if (edit_user.getText().toString().equals(username) && edit_pass.getText().toString().equals(password)) {
                    Intent intent = new Intent(LoginActivity.this, ETCActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    isToast = false;
                }
            } while (cursor.moveToNext());
        }
        if (!isToast || edit_pass.getText().toString().equals("") || edit_user.getText().toString().equals("")) {
            Toast.makeText(this, "用户或密码错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void setToolbarTitle() {
        toolbar_title = findViewById(R.id.toolbar_title);
        btn_network = findViewById(R.id.btn_network);
        // toolbar_title.setTitle("标题");
        toolbar_title.setTitleTextColor(Color.WHITE);
        btn_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "网络设置", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, NetworkSettingActivity.class);
                startActivity(intent);
            }
        });

//        toolbar_title.inflateMenu(R.menu.setting); /*设置Menu*/
//        toolbar_title.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Toast.makeText(LoginActivity.this, "网络设置", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (readuser.getBoolean(SaveIsBoolen, false)) {
            edit_user.setText(readuser.getString(SaveUserName, ""));/*渲染用户名*/
            save_user.setChecked(true);
        }
        if (readuser.getBoolean(SaveLoginBoolen, false)) {
            edit_user.setText(readuser.getString(SaveUserName, ""));
            edit_pass.setText(readuser.getString(SaveUserPass, ""));
            save_login.setChecked(true);
            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
            dialog.setTitle("自动登陆");
            dialog.setMessage("是否自动的登陆");
            dialog.setCancelable(false);
            dialog.setPositiveButton("登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.e(TAG, "自动登录");
                    iFLogin();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.e(TAG, "取消自动登录");
                    edit_user.setSelection(edit_user.length()); /*设置光标在文字最后*/
                    edit_pass.setText("");
                    save_login.setChecked(false);
                }
            });
            dialog.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause:user " + save_user.isChecked());
        Log.e(TAG, "onPause:pass " + save_login.isChecked());
        SaveFile();
    }

    /**
     * 用户记住实现思路:
     * 1.生成一个本地xml文件 保存用户信息 用户名 和 是否勾选记住用户名(CheckBox 的状态)
     * 2.当用户点击记住用户名  开始向本地写入 用户名 和 当前状态 true (CheckBox 的状态)
     * 3.当用户取消记住用户名  开始向本地写入 空用户名 和 当前状态 false (CheckBox 的状态)
     * 4.页面准备和用户交互时候 判断是否记住用户名 向存储文件 取到是否记住用户的状态 (CheckBox 的状态)
     * 5.判断 如果记住 向页面加载 用户名 和 checkbox 的状态
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                SaveFile();
                iFLogin();
                break;
            case R.id.btn_registr:
                //Toast.makeText(this, "注册", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, RegistryActivity.class);
                startActivity(intent);
                finish();
        }
    }

    private void SaveFile() {
        /*记住用户*/
        if (save_user.isChecked()) {
            editor.putString(SaveUserName, edit_user.getText().toString());
            editor.putBoolean(SaveIsBoolen, true);
        } else {
            editor.clear();
        }
        /*自动登录*/
        if (save_login.isChecked()) {
            editor.putString(SaveUserName, edit_user.getText().toString());
            editor.putString(SaveUserPass, edit_pass.getText().toString());
            editor.putBoolean(SaveLoginBoolen, true);
        } else {
            editor.putBoolean(SaveLoginBoolen, false);
            editor.apply();
        }
        editor.apply();
    }

}
