package top.codepy.urbantraffic.ViolationCatalog;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

public class VideosActivity extends Activity {
    private static final String TAG = "VideosActivity";
    private VideoView video_start;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_videos);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        Log.e(TAG, "Intent 得到地址:"+url);
        ToolbarMaster.CreateBackButton();
        ToolbarMaster.setTitle(intent.getStringExtra("title"));

        video_start = findViewById(R.id.video_start);
        mediaController = new MediaController(this);
        video_start.setVideoURI(Uri.parse(url));
        video_start.setMediaController(mediaController);
        mediaController.setMediaPlayer(video_start);
        video_start.requestFocus();
        video_start.start();
    }
}
