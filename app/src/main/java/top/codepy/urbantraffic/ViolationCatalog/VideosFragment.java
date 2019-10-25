package top.codepy.urbantraffic.ViolationCatalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import top.codepy.urbantraffic.R;

public class VideosFragment extends Fragment {
    private GridView video_grid;
    /**
     * 违章视频显示 fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.videosfragment,container,false);
        video_grid = view.findViewById(R.id.video_grid);
        //video_grid.setAdapter(new GridAdapter());
        return view;
    }
}
