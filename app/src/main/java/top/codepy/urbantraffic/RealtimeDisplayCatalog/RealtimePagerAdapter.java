package top.codepy.urbantraffic.RealtimeDisplayCatalog;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class RealtimePagerAdapter extends PagerAdapter {


    @Override
    public int getCount() {
        return RealtimeDisplayActivity.viewData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(RealtimeDisplayActivity.viewData.get(position));
        return RealtimeDisplayActivity.viewData.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        // super.destroyItem(container, position, object);
        container.removeView(RealtimeDisplayActivity.viewData.get(position));
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }
}
