package com.suntiago.baseui;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.suntiago.baseui.utils.log.Slog;

/**
 * Created by Jeremy on 2018/4/25.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initLogs();
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initLogs() {
        boolean logEnable = true;
        Slog.setDebug(logEnable, logEnable);
        com.suntiago.network.network.utils.Slog.setDebug(logEnable, logEnable);
        com.suntiago.network.network.utils.Slog.setLogCallback(
                new com.suntiago.network.network.utils.Slog.ILog() {
                    @Override
                    public void i(String tag, String msg) {
                        Slog.i(tag, msg);
                    }

                    @Override
                    public void v(String tag, String msg) {
                        Slog.v(tag, msg);
                    }

                    @Override
                    public void d(String tag, String msg) {
                        Slog.d(tag, msg);
                    }

                    @Override
                    public void e(String tag, String msg) {
                        Slog.e(tag, msg);
                    }

                    @Override
                    public void state(String packName, String state) {
                        Slog.state(packName, state);
                    }
                });
    }
}
