package top.codepy.urbantraffic.ToolsCatalog;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityControl {

    public static List<Activity> list = new ArrayList<>();

    public static void addActivity(Activity activity){
        list.add(activity);
    }
    public static void removeActivity(Activity activity){
        list.remove(activity);
    }

    public static void ALlfinsh(){
        for(Activity activity:list){
            list.remove(activity);
        }
    }

}
