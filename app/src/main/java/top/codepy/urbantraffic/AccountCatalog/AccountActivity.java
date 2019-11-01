package top.codepy.urbantraffic.AccountCatalog;

import android.app.AlertDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.ToolsCatalog.OkHttpData;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;

public class AccountActivity extends AppCompatActivity {
    public static List<Map<String, String>> listdata;
    public static Map<String, String> map;
    private static String[] car_id = {"1", "2", "3", "4"};
    private static String[] car_plate = {"辽A10001", "渝A10002", "川A10003", "古A10004"};
    private static String[] car_name = {"张三", "李四", "高亮", "三国"};
    private static Integer[] item_carIog = {R.drawable.car_1, R.drawable.car_2, R.drawable.car_3, R.drawable.car_4};
    private static String getUrl = "http://192.168.3.5:8088/transportservice/action/GetCarAccountBalance.do";
    private static String PutUrl = "http://192.168.3.5:8088/transportservice/action/SetCarAccountRecharge.do";
    private static Handler handler = new Handler();
    private static final String TAG = "账户管理(AccountActivity)";
    private View layout;
    private Button btn_money, btn_quit;
    private EditText edit_num;
    private RecyclerView recycler_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        ToolbarMaster.setTitle("账户管理");
        ToolbarMaster.MenuCreate();
        setButton();
        setRecyclerView();
    }

    private void setRecyclerView() {
        recycler_account = findViewById(R.id.recycler_account);
        recycler_account.setLayoutManager(new LinearLayoutManager(this));
        //recycler_account.setAdapter(new AccountAdapter(this));
        recycler_account.addItemDecoration(new MyDecoration());
        setdata();
        /*回调接口用于获取每个车辆点个点击充值的pos*/
        AccountAdapter.btn(new AccountAdapter.ButtonListener() {
            @Override
            public void btn_OnClick(int pos) {
                setDialog();
            }
        });
    }

    private void setdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                listdata = new ArrayList<>();
                for (int i = 0; i < car_id.length; i++) {
                    map = new HashMap<>();
                    map.put("CarId", car_id[i]);
                    map.put("UserName", "user1");
                    JSONObject jsonObject = new JSONObject(map);
                    OkHttpData.sendConnect(getUrl, jsonObject.toString());
                    try {
                        Log.e(TAG, OkHttpData.JsonObjectRead().toString());
                        map = new HashMap<>();
                        map.put("item_carId", car_id[i]);
                        map.put("item_carIog", String.valueOf(item_carIog[i]));
                        map.put("item_plate", car_plate[i]);
                        map.put("item_carName", car_name[i]);
                        map.put("item_carMoney", OkHttpData.JsonObjectRead().getString("Balance"));
                        listdata.add(map);
                        Thread.sleep(150);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "网络错误");
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "ListData:" + listdata);
                        recycler_account.setAdapter(new AccountAdapter(AccountActivity.this, listdata));
                    }
                });
            }
        }).start();
    }

    private void setButton() {
        ToolbarMaster.btn_Record.setVisibility(View.VISIBLE);
        ToolbarMaster.btn_Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AccountActivity.this, "充值记录", Toast.LENGTH_SHORT).show();
            }

        });
        /*监听 充值按钮并显示他*/
        ToolbarMaster.btn_inMoney.setVisibility(View.VISIBLE);
        ToolbarMaster.btn_inMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog();
            }

        });
    }

    /*dialog 弹窗*/
    public void setDialog() {
        LayoutInflater inflater = getLayoutInflater(); //xml布局文件，并且实例化
        layout = inflater.inflate(R.layout.editdialog, null);
        btn_money = layout.findViewById(R.id.btn_Money);
        btn_quit = layout.findViewById(R.id.btn_quit);
        edit_num = layout.findViewById(R.id.edit_num);
        final AlertDialog builder = new AlertDialog.Builder(AccountActivity.this).create();
        builder.setTitle("车辆账户充值");
        builder.setMessage("车牌号: " + AccountAdapter.car_plate.toString());
        builder.setIcon(R.drawable.logo);
        builder.setView(layout);
        builder.setCancelable(true);
        builder.show();
        btn_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e(TAG, "有好多车: " + AccountAdapter.car_plate.toString().equals("[]"));
                /*判断用户是否多选 或 用户是否没有输入充值金额*/
                if (!AccountAdapter.car_plate.toString().equals("[]") && !edit_num.getText().toString().equals("")) {
                    for (int i = 0; i < AccountAdapter.car_id.size(); i++) {
                        setMoney(AccountAdapter.car_id.get(i), edit_num.getText().toString());
                    }
                    setdata();
                } else {
                    Toast.makeText(AccountActivity.this, "未选择充值车辆  或 未输入金额", Toast.LENGTH_SHORT).show();
                }

                builder.cancel();
            }
        });
        btn_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.cancel(); //清空 Dialog
            }
        });
        /*检测 用户第一个不能输入 0*/
        edit_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edit_num.getText().toString().matches("^0")) { //正则替换 第一个不能为0
                    edit_num.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void setMoney(String pos, String money) {
        map = new HashMap<>();
        map.put("CarId", pos);
        Log.e(TAG, "传入的车辆ID"+pos );
        map.put("UserName", "user1");
        map.put("Money", money);
        final JSONObject jsonObject = new JSONObject(map);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpData.sendConnect(PutUrl, jsonObject.toString());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (OkHttpData.JsonObjectRead().getString("ERRMSG").equals("成功")) {
                                Toast.makeText(AccountActivity.this, "充值成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AccountActivity.this, "充值失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AccountActivity.this, "网络错误请检查网络设置", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    class MyDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 0, 0, 1);
        }
    }
}
