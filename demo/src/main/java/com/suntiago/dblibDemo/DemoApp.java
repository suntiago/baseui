package com.suntiago.dblibDemo;

import com.suntiago.baseui.App;
import com.suntiago.baseui.account.AccountManager;
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
    }
}
