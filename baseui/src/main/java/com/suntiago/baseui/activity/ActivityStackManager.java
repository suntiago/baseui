/*
    ShengDao Android Client, ActivityPageManager
    Copyright (c) 2014 ShengDao Tech Company Limited
 */

package com.suntiago.baseui.activity;

import android.app.Activity;
import android.content.Context;

import java.util.Stack;

import com.suntiago.baseui.utils.log.Slog;


/**
 * Activity堆栈管理
 * 从AndroidOne借鉴过来
 *
 * @author devin.hu
 * @version 1.0
 * @date 2013-9-23
 **/
@SuppressWarnings("ALL")
public class ActivityStackManager {
    private static final String TAG = ActivityStackManager.class.getSimpleName();

    private static Stack<Activity> activityStack;
    private static ActivityStackManager instance;

    /**
     * constructor
     */
    private ActivityStackManager() {

    }

    /**
     * get the AppManager instance, the AppManager is singleton.
     */
    public static ActivityStackManager getInstance() {
        if (instance == null) {
            instance = new ActivityStackManager();
        }
        return instance;
    }

    /**
     * add Activity to Stack
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
        Slog.d(TAG, "add :" + activity);
    }


    /**
     * remove Activity from Stack
     */
    public void removeActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.remove(activity);
        Slog.d(TAG, "remove :" + activity);
    }

    /**
     * get current activity from Stack
     */
    public Activity currentActivity() {
        if (activityStack.size() == 0) {
            return null;
        }
        return activityStack.lastElement();
    }

    public int activityCount() {
        if (activityStack == null) {
            return 0;
        }
        return activityStack.size();
    }

    /**
     * finish current activity from Stack
     */
    public void finishActivity() {
        Activity activity = currentActivity();
        finishActivity(activity);
    }

    /**
     * finish the Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * finish the Activity
     */
    public void finishOtherActivity(String name) {
        Activity main = null;
        for (Activity activity : activityStack) {
            if (activity.getClass().getName().contains(name)) {
                main = activity;
            } else {
                activity.finish();
            }
        }
        activityStack.clear();
        activityStack.add(main);
    }

    /**
     * finish the Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * finish the Activity for
     */
    public void finishActivityForResult(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                activity.setResult(Activity.RESULT_OK);
                finishActivity(activity);
            }
        }
    }

    /**
     * finish all Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * exit System
     *
     * @param context
     */
    public void exit(Context context) {
        exit(context, true);
    }

    /**
     * exit System
     *
     * @param context
     * @param isClearCache
     */
    @SuppressWarnings("deprecation")
    public void exit(Context context, boolean isClearCache) {
        try {
            finishAllActivity();
            /* 需要KILL_BACKGROUND_PROCESS权限
            if (mContext != null) {
                ActivityManager activityMgr = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                activityMgr.restartPackage(mContext.getPackageName());
            }
            */
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
