package top.codepy.urbantraffic.ViolationCatalog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import top.codepy.urbantraffic.R;

public class ImagesAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;

    public ImagesAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    class ImagesViewHolder {
        private RelativeLayout images;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImagesViewHolder holder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.violation_item_images, null);
            holder = new ImagesViewHolder();
            holder.images = view.findViewById(R.id.images);
            view.setTag(holder);
        } else {
            holder = (ImagesViewHolder) view.getTag();
        }
        holder.images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ImagesActivity.class);
                mContext.startActivity(intent);
            }
        });
        return view;
    }
}
