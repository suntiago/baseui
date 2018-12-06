package com.suntiago.dblibDemo;

import com.suntiago.baseui.App;
import com.suntiago.baseui.account.AccountManager;
import com.suntiago.baseui.utils.log.CrashHandler;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.lockpattern.PatternManager;


/**
 * Created by Jeremy on 2018/11/20.
 */

public class DemoApp extends App {
    @Override
    public void onCreate() {
        super.onCreate();
        AccountManager.init(this);
        PatternManager.init(this);
        Slog.init(this, "suntiago", "com.suntiago.demo");
        Slog.enableSaveLog(true);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext(), "suntiago", "com.suntiago.demo");
    }
}
