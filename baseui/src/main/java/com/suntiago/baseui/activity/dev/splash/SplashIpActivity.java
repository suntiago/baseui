package com.suntiago.baseui.activity.dev.splash;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.suntiago.baseui.activity.dev.MonitorActivity;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.getpermission.rxpermissions.RxPermissions;
import com.suntiago.network.network.rsp.BaseResponse;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by Jeremy on 2018/8/2.
 */

public class SplashIpActivity extends MonitorActivity {

    //正在加载弹出框
    private AlertDialog alertDialog;
    int mAPITime = 1000 * 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alertDialog = new AlertDialog.Builder(this)
                .setTitle("配置加载")
                .setCancelable(false)
                .setMessage("正在加载配置信息...")
                .create();
        alertDialog.show();

        RxPermissions.getInstance(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            getApi(SplashIpActivity.this);
                        } else {
                            Slog.d(TAG, "uploadFile  [id, filepath, pkgName, action]:");
                        }
                    }
                });
    }

    public void getApi(Context context) {
        Slog.d(TAG, "getApi  [context]:");
        ConfigDevice.configIp(context, "", new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse r) {
                if (r.error_code == 1000) {
                    alertDialog.dismiss();
                    splashFinish();
                } else {
                    Observable.timer(mAPITime, TimeUnit.MILLISECONDS)
                            .subscribe(new Action1<Long>() {
                                           @Override
                                           public void call(Long aLong) {
                                               Slog.d(TAG, "Observable.timer:regetApi");
                                               getApi(SplashIpActivity.this);
                                           }
                                       }
                            );
                }
            }
        });
    }

    private void splashFinish() {
//        Intent intent = new Intent(context, MainStartService.class);
//        if (apichanged) {
//            intent.putExtra("apichanged", 1);
//        }
//        startService(intent);
    }

}
