package top.codepy.urbantraffic.ThresholdCatalog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ThresholdService extends Service {
    private static final String TAG = "阈值服务 (Threshold)";

    public ThresholdService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "阈值服务创建" );
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "阈值服务启动" );
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "阈值服务卸载" );
        super.onDestroy();
    }
}
