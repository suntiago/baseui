package com.suntiago.dblibDemo;

import android.content.Context;

import com.suntiago.baseui.App;
import com.suntiago.baseui.account.AccountManager;
import com.suntiago.baseui.utils.FileUtils;
import com.suntiago.baseui.utils.log.CrashHandler;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.lockpattern.PatternManager;


/**
 * Created by Jeremy on 2018/11/20.
 */

public class DemoApp extends App {
    static final String COM = "suntiago";
    static  final  String appNAme= "demo";
    @Override
    public void onCreate() {
        super.onCreate();
        Context ct = this;
        FileUtils.initPath(COM, appNAme);
        AccountManager.init(ct);
        PatternManager.init(ct);
        Slog.init(ct, COM, appNAme);
        Slog.enableSaveLog(true);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext(), COM, appNAme);
    }
}
