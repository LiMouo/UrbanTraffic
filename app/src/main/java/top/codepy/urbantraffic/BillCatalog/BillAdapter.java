package top.codepy.urbantraffic.BillCatalog;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import top.codepy.urbantraffic.R;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private Context mContext;
    List<ContentValues> listData;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH.mm");

    public BillAdapter(Context context, List<ContentValues> listData) {
        this.mContext = context;
        this.listData = listData;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bill_recycler_item, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
               if (position == 0){/*控制第一行显示Title*/
                   holder.tv_1.setText("序号");
                   holder.tv_2.setText("车号");
                   holder.tv_3.setText("充值金额");
                   holder.tv_4.setText("操作人");
                   holder.tv_5.setText("充值时间");
               }else {
                   ContentValues values = listData.get(position-1);
                   holder.tv_1.setText(values.get("id").toString());
                   holder.tv_2.setText(values.get("car_id").toString());
                   holder.tv_3.setText(values.get("money").toString());
                   holder.tv_4.setText((String) values.get("user"));
                   Date date = new Date((String) values.get("datetime"));
                   holder.tv_5.setText(dateFormat.format(date));
               }
    }
    @Override
    public int getItemCount() {
        return  listData.size()+1; /*第一行被占用了 被用来标题了*/
    }

    public class BillViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_1;
        private TextView tv_2;
        private TextView tv_3;
        private TextView tv_4;
        private TextView tv_5;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_1 = itemView.findViewById(R.id.tv_1);
            tv_2 = itemView.findViewById(R.id.tv_2);
            tv_3 = itemView.findViewById(R.id.tv_3);
            tv_4 = itemView.findViewById(R.id.tv_4);
            tv_5 = itemView.findViewById(R.id.tv_5);
        }
    }
}
