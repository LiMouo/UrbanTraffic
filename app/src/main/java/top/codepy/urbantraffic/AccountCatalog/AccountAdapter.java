package top.codepy.urbantraffic.AccountCatalog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import top.codepy.urbantraffic.R;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {
    private static final String TAG = "账户Adapter";
    private Context context;
    public List<Map<String, String>> listdata;
    private Map<String, String> map;
    public static List<String> car_id;
    public static List<String> car_plate;
    private int position = 0;
    public static ButtonListener mListener;

    public AccountAdapter(Context context, List<Map<String, String>> list) {
        this.context = context;
        this.listdata = list;
        car_id = new ArrayList<>();
        car_plate = new ArrayList<>();
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.account_item, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AccountViewHolder holder, final int position) {
        map = listdata.get(position);
        Log.e(TAG, "list: " + map.toString());
        holder.item_carId.setText(map.get("item_carId"));
        holder.item_plate.setText(map.get("item_plate"));
        holder.item_carName.setText(map.get("item_carName"));
        holder.item_carMoney.setText(map.get("item_carMoney"));
        holder.item_carIog.setImageResource(Integer.parseInt(map.get("item_carIog")));
        holder.item_carCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Toast.makeText(context, b ? position + " 选中" : position + "取消选中", Toast.LENGTH_SHORT).show();
                if (b) {
                    car_id.add(String.valueOf(position + 1));
                    car_plate.add(holder.item_plate.getText().toString());
                } else {
                    car_id.remove(String.valueOf(position + 1));
                    car_plate.remove(holder.item_plate.getText().toString());
                }
                Log.e(TAG, "当前选中汽车:" + car_id.toString());
                Log.e(TAG, "当前选中汽车:" + car_plate.toString());
            }
        });

        holder.item_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                car_id.add(String.valueOf(position + 1));
                car_plate.add(holder.item_plate.getText().toString());
                Log.e(TAG, "onClick: " +position +holder.item_plate.getText());
                mListener.btn_OnClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public class AccountViewHolder extends RecyclerView.ViewHolder {
        private TextView item_carId, item_plate, item_carName, item_carMoney;
        private ImageView item_carIog;
        private CheckBox item_carCheckBox;
        private Button item_btn_submit;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            item_carId = itemView.findViewById(R.id.item_carId); /*车辆ID*/
            item_carIog = itemView.findViewById(R.id.item_carIog); /*车主图片*/
            item_plate = itemView.findViewById(R.id.item_plate); /*车牌号*/
            item_carName = itemView.findViewById(R.id.item_carName); /*车主信息*/
            item_carMoney = itemView.findViewById(R.id.item_carMoney); /*车主余额*/
            item_carCheckBox = itemView.findViewById(R.id.item_carCheckBox); /*充值多选*/
            item_btn_submit = itemView.findViewById(R.id.item_btn_submit); /*提交*/
        }
    }

    public static void btn(ButtonListener listener) {
        mListener = listener;
    }

    interface ButtonListener {
        void btn_OnClick(int pos);
    }

}
