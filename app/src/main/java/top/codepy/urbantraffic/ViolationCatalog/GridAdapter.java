package top.codepy.urbantraffic.ViolationCatalog;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;


import java.util.List;
import java.util.Map;

import top.codepy.urbantraffic.R;

public class GridAdapter extends BaseAdapter {
    private static final String TAG = "GridAdapter";
    private Context mContext;
    private LayoutInflater layoutInflater;
    List<Map<String, String>> listData;

    public GridAdapter(Context mContext, List<Map<String, String>> listData) {
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
        this.listData = listData;
        Log.e(TAG, listData.toString());
    }

    @Override
    public int getCount() {
        int count = 8;
        if (listData != null) {
            count = listData.size();
        }
        return count;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    class GridViewHolder {
        private ImageView iv_1;
        private TextView tv_1;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        GridViewHolder holder = null;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.violation_gridview, null);
            holder = new GridViewHolder();
            holder.iv_1 = view.findViewById(R.id.iv_1);
            holder.tv_1 = view.findViewById(R.id.tv_1);
            view.setTag(holder);
        } else {
            holder = (GridViewHolder) view.getTag();
        }
        /*渲染页面title*/
        if (listData != null) {
            holder.tv_1.setText(listData.get(i).get("title"));
            holder.iv_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Toast.makeText(mContext, "点击了" + i, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, VideosActivity.class);
                    intent.putExtra("url", listData.get(i).get("url"));
                    intent.putExtra("title", listData.get(i).get("title"));
                    mContext.startActivity(intent);
                }
            });
        }
        return view;
    }
}
