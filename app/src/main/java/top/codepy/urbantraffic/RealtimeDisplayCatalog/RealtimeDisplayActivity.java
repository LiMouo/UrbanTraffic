package top.codepy.urbantraffic.RealtimeDisplayCatalog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

public class RealtimeDisplayActivity extends AppCompatActivity {

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
    }
}
