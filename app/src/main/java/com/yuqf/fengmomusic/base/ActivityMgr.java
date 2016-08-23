package com.yuqf.fengmomusic.base;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ActivityMgr {

    private final String LogTag = "ActivityMgr";
    private static ActivityMgr activityMgr;
    private List<Activity> activityList;

    public static ActivityMgr getActivityMgr() {
        if (activityMgr == null) {
            activityMgr = new ActivityMgr();
        }
        return activityMgr;
    }

    private ActivityMgr() {
        if (activityList == null)
            activityList = new ArrayList<>();
    }

    public boolean addActivity(Activity activity) {
        if (activity == null)
            return false;
        if (!activityList.contains(activity))
            return activityList.add(activity);
        return false;
    }

    public boolean removeActivity(Activity activity) {
        if (activity == null) return false;
        return activityList.remove(activity);
    }

    public boolean finishActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            activityList.remove(activity);
            activity.finish();
            activity = null;
            return true;
        }
        return false;
    }

    public void exit() {
        try {
            int size = activityList.size();
            for (int i = size; i >= 0; i--) {
                Activity activity = activityList.get(i);
                activityList.remove(activity);
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
            }
            activityList.clear();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception ex) {
            Log.e(LogTag, ex.getMessage());
            Log.e(LogTag, ex.getStackTrace().toString());
        }
    }
}
