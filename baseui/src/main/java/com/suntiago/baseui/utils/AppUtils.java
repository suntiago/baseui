package com.suntiago.baseui.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import java.util.List;

/**
 * Created by viroyal-android01 on 2016/7/21.
 * 跟App相关的辅助类
 */
public class AppUtils {


  /**
   * 不能实例化
   */
  private AppUtils() {
        /* cannot be instantiated */
    throw new UnsupportedOperationException("cannot be instantiated");
  }

  /**
   * 获取应用程序名称
   *
   * @param context
   * @return
   */
  public static String getAppName(Context context) {
    try {
      PackageManager packageManager = context.getPackageManager();
      PackageInfo packageInfo = packageManager.getPackageInfo(
          context.getPackageName(), 0);
      int labelRes = packageInfo.applicationInfo.labelRes;
      return context.getResources().getString(labelRes);
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 获取应用程序版本名称信息
   *
   * @param context
   * @return 当前应用的版本名称
   */
  public static String getVersionName(Context context) {
    try {
      PackageManager packageManager = context.getPackageManager();
      PackageInfo packageInfo = packageManager.getPackageInfo(
          context.getPackageName(), 0);
      return packageInfo.versionName;

    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 获取当前线程名称
   *
   * @param context
   * @return
   */
  public static String getCurProcessName(Context context) {
    int pid = android.os.Process.myPid();
    ActivityManager activityManager = (ActivityManager) context
        .getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
        .getRunningAppProcesses()) {
      if (appProcess.pid == pid) {
        return appProcess.processName;
      }
    }
    return null;
  }

  /**
   * 判断当前app是否在后台运行
   *
   * @param context
   * @return
   */
  public static boolean isApplicationInBackground(final Context context) {
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
    if (!tasks.isEmpty()) {
      ComponentName topActivity = tasks.get(0).topActivity;
      if (!topActivity.getPackageName().equals(context.getPackageName())) {
        return true;
      }
    }
    return false;
  }
}