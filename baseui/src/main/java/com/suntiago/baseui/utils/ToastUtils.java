package com.suntiago.baseui.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.suntiago.baseui.R;


/**
 * Created by viroyal-android01 on 2016/7/22.
 */
public class ToastUtils {
    private static Toast mToast;

    /**
     * 取消吐司
     */
    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    /**
     * 显示吐司
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = new Toast(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_layout, null);
        TextView t = (TextView) layout.findViewById(R.id.toast_text);
        if (t != null) {
            t.setText(msg);
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(layout);
        mToast.show();
    }

    /**
     * 显示一个长吐司
     * @param context
     * @param msg
     */
    public static void showToastLong(Context context, String msg) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = new Toast(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_layout, null);
        TextView t = (TextView) layout.findViewById(R.id.toast_text);
        if (t != null) {
            t.setText(msg);
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(layout);
        mToast.show();
    }
}
