package com.suntiago.baseui.account;

/**
 * Created by Jeremy on 2018/8/2.
 */

public interface AccountStatusCallback {
    void loginCallback();

    void logoutCallback();

    void userinfoChange(String key);
}
