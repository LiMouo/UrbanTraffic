package top.codepy.urbantraffic.EnvironCatalog;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONObject;

import java.util.Map;
import top.codepy.urbantraffic.R;


public class EnvironAdapter extends RecyclerView.Adapter<EnvironAdapter.EnvironViewHolder> {
    private static final String TAG = "EnvironAdapter";
    private Context context;
    private String[] arr = {"温度", "湿度", "光照", "CQ2", "PM2.5", "道路状态"};
    private String[] arr_1 = new String[6];


    public EnvironAdapter(Context context, ContentValues values) {
        this.context = context;
        arr_1[0] = values.getAsString("temperature");
        arr_1[1] = values.getAsString("humidity");
        arr_1[2] = values.getAsString("LightIntensity");
        arr_1[3] = values.getAsString("co2");
        arr_1[4] = values.getAsString("pm25");
        arr_1[5] = values.getAsString("Status");
    }
    @NonNull
    @Override
    public EnvironViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EnvironViewHolder viewHolder = new EnvironViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.environ_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EnvironViewHolder holder, int position) {
        holder.environ_title.setText(arr[position]);
        holder.environ_number.setText(arr_1[position]);
        if(Integer.parseInt(arr_1[5])>=4 && position == 5){
         holder.re_red.setBackgroundResource(R.drawable.envicron_red);
        }
    }
    @Override
    public int getItemCount() {
        return arr.length;
    }
    public class EnvironViewHolder extends RecyclerView.ViewHolder {
        private TextView environ_title;
        private TextView environ_number;
        private RelativeLayout re_red;

        public EnvironViewHolder(@NonNull View itemView) {
            super(itemView);
            environ_title = itemView.findViewById(R.id.environ_title);
            environ_number = itemView.findViewById(R.id.environ_number);
            re_red = itemView.findViewById(R.id.re_red);
        }
    }
}
