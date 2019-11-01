package top.codepy.urbantraffic.BillCatalog;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import top.codepy.urbantraffic.R;
import top.codepy.urbantraffic.SQLiteCatalog.SQLiteBillMaster;
import top.codepy.urbantraffic.ToolsCatalog.ToolbarMaster;


/**
 * 具体实现思路:
 * 一.在用户充值界面 即:ETCHome -> ETCActivity 里完成对数据的写入 （使用SQLIite 保存用户的充值信息 id car_id money user datetime）
 * 1. 创建一个SQLiteMaster 类用来创建数据库
 * 2. 处理我们要向数据写入的数据
 * 3. 我们向服务器发送充值数据 服务器返回数据 请求成功 OR 请求失败 在充值成功里写入当前充值信息到数据库
 * <p>
 * 二.提取存储的SQLite 数据对 充值信息进行界面渲染（要求时间排序）
 * 1. 创造充值信息 BillMaster 完成对界面的布局 需要知识 Spinner RecyclerView
 * 2. 取出数据里存储的数据 向RecyclerView 渲染
 * 3. 通过Spinner 选择的排序方式对RecyclerView 重新渲染
 */

public class BillActivity extends Activity {
    private static final String TAG = "BillMaster";
    private SQLiteBillMaster sqLiteMaster;
    private Spinner sp_bill;
    private List<String> list;
    private RecyclerView bill_recycler;
    private Button btn_query;
    Map<String, Object> map;
    List<ContentValues> listData;
    private boolean isOrder = true; /*用来判断是时间升序还是时间降序*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_master);
        ToolbarMaster.setTitle("账单管理");
        ToolbarMaster.MenuCreate();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sqLiteMaster = new SQLiteBillMaster(this, "Bill.db");
        getSqlBill();
        sp_bill = findViewById(R.id.sp_bill);
        list = new ArrayList<>();
        list.add("时间升序");
        list.add("时间降序");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.bill_item, list);
        sp_bill.setAdapter(arrayAdapter);
        sp_bill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(BillMaster.this, "当前选着"+ i, Toast.LENGTH_SHORT).show();
                isOrder = (i == 0 ? true : false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        btn_query = findViewById(R.id.btn_query);
        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecsetAdapter(isOrder);
            }
        });
        bill_recycler = findViewById(R.id.bill_recycler);
        bill_recycler.setLayoutManager(new GridLayoutManager(this, 1));
        RecsetAdapter(isOrder);
        bill_recycler.addItemDecoration(new MyDecoration());
    }

    private void RecsetAdapter(boolean isOrder) {
        if (isOrder) {
            Collections.sort(listData, new Comparator<ContentValues>() {
                @Override
                public int compare(ContentValues t0, ContentValues t1) {
                    Date date0 = new Date((String) t0.get("datetime"));
                    Date date1 = new Date((String) t1.get("datetime"));
                    if (date0.getTime() > date1.getTime()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            bill_recycler.setAdapter(new BillAdapter(this, listData));
        } else {
            Collections.sort(listData, new Comparator<ContentValues>() {
                @Override
                public int compare(ContentValues t0, ContentValues t1) {
                    Date date0 = new Date((String) t0.get("datetime"));
                    Date date1 = new Date((String) t1.get("datetime"));
                    if (date0.getTime() < date1.getTime()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            bill_recycler.setAdapter(new BillAdapter(this, listData));
        }
    }

    public void getSqlBill() {
        listData = new ArrayList<>();
        SQLiteDatabase db = sqLiteMaster.getWritableDatabase();
        Cursor cursor = db.query("bill", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int car_id = cursor.getInt(cursor.getColumnIndex("car_id"));
                int money = cursor.getInt(cursor.getColumnIndex("money"));
                String user = cursor.getString(cursor.getColumnIndex("user"));
                String datetime = cursor.getString(cursor.getColumnIndex("datetime"));
//                Log.e(TAG, "id:" + id);
//                Log.e(TAG, "car_id:" + car_id);
//                Log.e(TAG, "money:" + money);
//                Log.e(TAG, "user:" + user);
//                Log.e(TAG, "datetime:" + datetime);
                ContentValues values = new ContentValues();
                values.put("id", id);
                values.put("car_id", car_id);
                values.put("money", money);
                values.put("user", user);
                values.put("datetime", datetime);
                listData.add(values);
            } while (cursor.moveToNext());
            Log.e(TAG, "listData: " + listData);
        }
    }

    class MyDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 0, 1, 1);
        }
    }
}

