package top.codepy.urbantraffic.AccountCatalog;

import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
import top.codepy.urbantraffic.BillCatalog.BillActivity;
import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.ToolsCatalog.OkHttpData;
import top.codepy.urbantraffic.ToolsCatalog.SendHTTP;
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
        listdata = new ArrayList<>();
        setdata();
        /*回调接口用于获取每个车辆点个点击充值的pos*/
        AccountAdapter.btn(new AccountAdapter.ButtonListener() {
            @Override
            public void btn_OnClick(final int pos, String name) {
                LayoutInflater inflater = getLayoutInflater(); //xml布局文件，并且实例化
                layout = inflater.inflate(R.layout.editdialog, null);
                btn_money = layout.findViewById(R.id.btn_Money);
                btn_quit = layout.findViewById(R.id.btn_quit);
                edit_num = layout.findViewById(R.id.edit_num);
                final AlertDialog builder = new AlertDialog.Builder(AccountActivity.this).create();
                builder.setTitle("车辆账户充值");
                builder.setMessage("车牌号: [" + name +"]");
                builder.setIcon(R.drawable.logo);
                builder.setView(layout);
                builder.setCancelable(true);
                builder.show();
                btn_money.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "onClick: "+ AccountAdapter.car_id.toString().equals("[]"));
                        if (!edit_num.getText().toString().equals("")) {
                                money(car_id[pos], edit_num.getText().toString());
                            Toast.makeText(AccountActivity.this, "充值成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AccountActivity.this, "未输入金额", Toast.LENGTH_SHORT).show();
                        }
                        setdata();
                        builder.cancel();
                    }
                });
                btn_quit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //setdata();
                        builder.cancel(); //关闭 dialog
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
                    } catch (Exception e) {
                        e.printStackTrace();
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
                Intent intent = new Intent(AccountActivity.this, BillActivity.class);
                startActivity(intent);
            }

        });
        /*监听 充值按钮并显示他*/
        ToolbarMaster.btn_inMoney.setVisibility(View.VISIBLE);
        ToolbarMaster.btn_inMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!AccountAdapter.car_id.toString().equals("[]")){
                    setDialog(0);
                }else {
                    Toast.makeText(AccountActivity.this, "未选择充值车辆", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*dialog 弹窗*/
    public void setDialog(final int pos) {
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
                        money(AccountAdapter.car_id.get(i), edit_num.getText().toString());
                    }
                    Toast.makeText(AccountActivity.this, "充值成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AccountActivity.this, "未选择充值车辆  或 未输入金额", Toast.LENGTH_SHORT).show();
                }
                setdata();
                builder.cancel();
            }
        });
        btn_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // setdata();
                builder.cancel(); //关闭 dialog
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

    public void money(final String id, String money) {
        try {
            final JSONObject json = new JSONObject();
            json.put("CarId", id);
            json.put("Money", money);
            json.put("UserName", "user1");
            SendHTTP.sendHttpRequest(PutUrl, json, new okhttp3.Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                    try {
                        int code = response.code();
                        JSONObject data =  new JSONObject(response.body().string());
                        Log.e(TAG, "得到数据: " + data);
                        if (code == 200) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        } else {
                            final String error = data.getString("message");
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AccountActivity.this, "请求状态: " + response.code() + "  错误代码: " + error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class MyDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 0, 0, 1);
        }
    }
}
