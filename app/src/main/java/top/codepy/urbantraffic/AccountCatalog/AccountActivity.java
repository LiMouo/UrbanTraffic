package top.codepy.urbantraffic.AccountCatalog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ToolbarMaster.setTitle("账户管理");
        ToolbarMaster.MenuCreate();
    }
}
