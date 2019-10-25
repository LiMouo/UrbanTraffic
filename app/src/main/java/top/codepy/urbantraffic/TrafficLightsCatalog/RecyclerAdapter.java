package top.codepy.urbantraffic.TrafficLightsCatalog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import top.codepy.urbantraffic.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private static final String TAG = "RecyclerAdapter";
    private Context context;
    private LayoutInflater inflater;
    private List<Map<String, String>> listData;
    private Map<String, String> map;
    private String[] arr = {"路口", "红灯时长(S)", "绿灯时长(S)", "黄灯时长(S)"};

    public RecyclerAdapter(Context context, List<Map<String, String>> listData) {
        this.context = context;
        this.listData = listData;
        inflater = LayoutInflater.from(context);
        Log.e(TAG, "RecyclerAdapter " + listData.toString());
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_item, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        if (position == 0) { //渲染标题
            holder.tv_1.setText(arr[0]);
            holder.tv_2.setText(arr[1]);
            holder.tv_3.setText(arr[2]);
            holder.tv_4.setText(arr[3]);
        }
        if (position != 0) {
            map = listData.get(position - 1);
            holder.tv_1.setText(String.valueOf(map.get("id")));
            holder.tv_2.setText(map.get("RedTime"));
            holder.tv_3.setText(map.get("GreenTime"));
            holder.tv_4.setText(map.get("YellowTime"));
        }
    }

    @Override
    public int getItemCount() {
        if (listData != null) {
            return listData.size() + 1;
        } else {
            return 24;
        }

    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_1, tv_2, tv_3, tv_4;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_1 = itemView.findViewById(R.id.tv_1);
            tv_2 = itemView.findViewById(R.id.tv_2);
            tv_3 = itemView.findViewById(R.id.tv_3);
            tv_4 = itemView.findViewById(R.id.tv_4);
        }
    }
}
