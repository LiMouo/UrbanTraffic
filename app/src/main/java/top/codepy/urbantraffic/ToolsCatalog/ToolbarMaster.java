package top.codepy.urbantraffic.ToolsCatalog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.Nullable;

import top.codepy.urbantraffic.AccountCatalog.AccountActivity;
import top.codepy.urbantraffic.BillCatalog.BillActivity;
import top.codepy.urbantraffic.ETCCatalog.ETCActivity;
import top.codepy.urbantraffic.EnvironCatalog.EnvironActivity;
import top.codepy.urbantraffic.LoginCatalog.LoginActivity;
import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.RealtimeDisplayCatalog.RealtimeDisplayActivity;
import top.codepy.urbantraffic.RegistryCatalog.RegistryActivity;
import top.codepy.urbantraffic.ThresholdCatalog.ThresholdActivity;
import top.codepy.urbantraffic.TrafficLightsCatalog.TrafficLightsActivity;
import top.codepy.urbantraffic.TripCatalog.TripActivity;
import top.codepy.urbantraffic.ViolationCatalog.ViolationActivity;

public class ToolbarMaster extends LinearLayout {
    private static final String TAG = "ToolbarMaster";
    private static Context mContext;
    private static Toolbar toolbar;
    private static TextView title;
    private static Button btn_account;

    public ToolbarMaster(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.toolbar_layout, this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.menu);
        title = findViewById(R.id.toolbar_title);
        btn_account = findViewById(R.id.btn_account);
    }

    public static void MenuCreate() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.etc_options, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Intent intent;
                        Activity activity;
                        switch (menuItem.getItemId()) {
                            case R.id.menu_user:
                                Log.e(TAG, "我的账户 ");
                                intent = new Intent(mContext, ETCActivity.class);
                                mContext.startActivity(intent);
                                break;
                            case R.id.menu_green:
                                Log.e(TAG, "红绿灯管理 ");
                                intent = new Intent(mContext, TrafficLightsActivity.class);
                                mContext.startActivity(intent);
                                break;
                            case R.id.menu_order:
                                Log.e(TAG, "账单管理 ");
                                intent = new Intent(mContext, BillActivity.class);
                                mContext.startActivity(intent);
                                break;
                            case R.id.menu_violation:
                                Log.e(TAG, "车辆违章 ");
                                intent = new Intent(mContext, ViolationActivity.class);
                                mContext.startActivity(intent);
                                break;
                            case R.id.menu_environ:
                                Log.e(TAG, "环境指标 ");
                                intent = new Intent(mContext, EnvironActivity.class);
                                mContext.startActivity(intent);
                                break;
                            case R.id.menu_Threshold:
                                Log.e(TAG, "阈值设置 ");
                                intent = new Intent(mContext, ThresholdActivity.class);
                                mContext.startActivity(intent);
                                break;
                            case R.id.menu_Trip:
                                Log.e(TAG, "实时显示 ");
                                intent = new Intent(mContext, TripActivity.class);
                                mContext.startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    public static void CreateBackButton() {
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) mContext;
                activity.finish();
            }
        });

    }

    public static void setAccount() {
        btn_account.setVisibility(VISIBLE);
        btn_account.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AccountActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    public static void setTitle(String T) {
        title.setText(T); /*设置Title*/
    }
}
