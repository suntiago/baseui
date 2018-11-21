package com.suntiago.baseui.activity.dev;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;

import com.suntiago.baseui.activity.SlothActivity;
import com.suntiago.baseui.utils.date.DateUtils;
import com.suntiago.baseui.utils.log.Slog;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by Jeremy on 2018/6/14.
 */

public abstract class MonitorActivity extends SlothActivity {

    public static final String BROADCAST_ACTION = "com.viroyal.permission.capture_complete";
    public static final String REGISTER_BROADCAST_ACTION = "com.viroyal.permission.capture";
    public static final String CRASH_BROADCAST_ACTION = "com.viroyal.permission.crash";

    private String mCaptureId;
    MyBroadcastReceiver mMyBroadcastReceiver = new MyBroadcastReceiver();
    CrashBroadcastReceiver mCrashBroadcastReceiver = new CrashBroadcastReceiver();

    //截图
    private void capture() {
        viewSaveToImage(getRootView());
    }

    //截图并保存
    private void viewSaveToImage(View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);

        // 把一个View转换成图片
        Bitmap cachebmp = loadBitmapFromView(view);

        // 添加水印
        Bitmap bitmap = Bitmap.createBitmap(cachebmp);

        FileOutputStream fos;
        String time = DateUtils.format(new Date(), "yyyy-MM-dd-HH-mm-ss");
        long timestamp = System.currentTimeMillis();
        String path = "/sdcard/capture/";
        String fileName = "capture-" + time + "-" + timestamp + ".png";
        String ret = "";
        String msg = "";
        try {
            // 判断手机设备是否有SD卡
            boolean isHasSDCard = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
            if (isHasSDCard) {
                // SD卡根目录

                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                fos = new FileOutputStream(path + fileName);
//                fos = new FileOutputStream(file);
            } else {
                msg = "创建文件失败!";
                throw new Exception("创建文件失败!");
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);

            fos.flush();
            fos.close();
        } catch (Exception e) {
            ret = "0";
            e.printStackTrace();
        }

        Slog.d(TAG, BROADCAST_ACTION);
        Intent intent = new Intent();
        intent.setAction(BROADCAST_ACTION);
        intent.putExtra("path", path + fileName);
        intent.putExtra("pkgName", getPackageName());
        intent.putExtra("ret", ret);
        intent.putExtra("msg", msg);
        intent.putExtra("id", mCaptureId);
        sendBroadcast(intent);
        view.destroyDrawingCache();
    }

    //获取根view
    private View getRootView() {
        return ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMyBroadcastReceiver.register(this);
        mCrashBroadcastReceiver.register(this);
    }


    @Override
    protected void onPause() {
        mMyBroadcastReceiver.unRegister(this);
        mCrashBroadcastReceiver.unRegister(this);
        super.onPause();
    }

    @Override
    public void handleToast(int code, String msg) {
        showToast(msg);
    }


    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Slog.d(TAG, REGISTER_BROADCAST_ACTION);
            mCaptureId = intent.getStringExtra("id");
            capture();
        }

        public void register(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(REGISTER_BROADCAST_ACTION);
            registerReceiver(this, filter);
        }

        public void unRegister(Context context) {
            unregisterReceiver(this);
        }
    }

    class CrashBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Slog.d(TAG, CRASH_BROADCAST_ACTION);
            String str = null;
            if (str.equals("1")) {

            }
        }

        public void register(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(CRASH_BROADCAST_ACTION);
            registerReceiver(this, filter);

        }

        public void unRegister(Context context) {
            unregisterReceiver(this);
        }
    }
}
