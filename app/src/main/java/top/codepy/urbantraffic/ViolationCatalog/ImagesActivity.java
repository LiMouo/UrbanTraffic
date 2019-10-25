package top.codepy.urbantraffic.ViolationCatalog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

public class ImagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        ToolbarMaster.setTitle("违章图片");
        ToolbarMaster.CreateBackButton();
    }
}
