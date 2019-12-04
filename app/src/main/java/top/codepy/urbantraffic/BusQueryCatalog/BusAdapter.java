package top.codepy.urbantraffic.BusQueryCatalog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import top.codepy.urbantraffic.R;

public class BusAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "BusAdapter";
    private Context context;
    private String[] GroupText = {"中医院站", "联想大厦站"};
    private String[][] ChildText = {{"1", "2"}, {"1", "2"}};
    private List<JSONArray> list;

    public BusAdapter(Context context, List<JSONArray> list) {
        this.context = context;
        this.list = list;
        Log.e(TAG, "BusAdapter:" + list);
    }

    /*一级分组显示条数*/
    @Override
    public int getGroupCount() {
        return GroupText.length;
    }

    /*二级分组显示条数*/
    @Override
    public int getChildrenCount(int i) {
        //Log.e(TAG, "二级分组显示条数 "+ ChildText[i].length);
        return ChildText[i].length;
    }

    /*渲染每个一级分组数据*/
    @Override
    public Object getGroup(int i) {
        return null;
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    class GroupViewHolder {
        private TextView one_item;
    }

    class ChildViewHolder {
        private TextView exp_carID;
        private TextView exp_People;
        private TextView exp_time;
        private TextView exp_distance;
    }


    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GroupViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.expandable_one_item, null);
            holder = new GroupViewHolder();
            holder.one_item = view.findViewById(R.id.one_item);
            view.setTag(holder);
        } else {
            holder = (GroupViewHolder) view.getTag();
        }
        holder.one_item.setText(GroupText[i]);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.expandable_two_item, null);
            holder = new ChildViewHolder();
            holder.exp_carID = view.findViewById(R.id.exp_carID);
            holder.exp_People = view.findViewById(R.id.exp_People);
            holder.exp_time = view.findViewById(R.id.exp_time);
            holder.exp_distance = view.findViewById(R.id.exp_distance);
            view.setTag(holder);
        } else {
            holder = (ChildViewHolder) view.getTag();
        }
        try {
            JSONArray array = list.get(i);
            JSONObject jsonObject = array.getJSONObject(i1);
            holder.exp_carID.setText(jsonObject.getString("BusId"));
            int Distance = Integer.parseInt(jsonObject.getString("Distance"));
            holder.exp_distance.setText(String.valueOf(Distance / 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


}
