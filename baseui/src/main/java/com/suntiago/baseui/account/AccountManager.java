package com.suntiago.baseui.account;

import android.content.Context;
import android.text.TextUtils;

import com.suntiago.network.network.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeremy on 2018/8/2.
 * 账户管理应用，适用于单用户系统
 */

public class AccountManager {
    private static String AccountManager_user_id = "AccountManager_user_id";
    private static String AccountManager_account_token = "AccountManager_account_token";
    private static AccountManager sAccountManager;
    private Context sContext;
    //账户ID
    private Account mAccount;
    List<AccountStatusCallback> mAccountStatusCallbacks = new ArrayList<>();

    public static AccountManager get() {
        if (sAccountManager == null) {
            throw new NullPointerException("please init AccountManager first before getting");
        }
        return sAccountManager;
    }

    private AccountManager(Context sContext, boolean autologin) {
        this.sContext = sContext;
        if (autologin) {
            String account = SPUtils.getInstance(sContext).get(AccountManager_user_id);
            String token = SPUtils.getInstance(sContext).get(AccountManager_account_token);
            setAccount(account, token);
        }
    }

    public static void init(Context context) {
        init(context, false);
    }

    public static void init(Context context, boolean autoLogin) {
        if (sAccountManager == null) {
            sAccountManager = new AccountManager(context, autoLogin);
        }
    }

    public boolean isLogin() {
        if (mAccount != null && !TextUtils.isEmpty(mAccount.userId)) {
            return true;
        }
        return false;
    }

    public String getUserData(String key) {
        if (isLogin()) {
            String s = SPUtils.getInstance(sContext).get(key);
            if (s != null && s.startsWith(mAccount.userId)) {
                return s.substring(mAccount.userId.length(), s.length());
            }
        }
        return "";
    }

    public void putUserData(String key, String data) {
        if (isLogin()) {
            SPUtils.getInstance(sContext).put(key, mAccount.userId + data);
            if (mAccountStatusCallbacks.size() > 0) {
                for (AccountStatusCallback accountStatusCallback : mAccountStatusCallbacks) {
                    accountStatusCallback.userinfoChange(key);
                }
            }
        }
    }

    public String getUserId() {
        if (isLogin()) {
            return mAccount.userId;
        }
        return "";
    }

    public String getToken() {
        if (isLogin()) {
            return mAccount.token;
        }
        return "";
    }

    public void autoLogin() {
        String account = SPUtils.getInstance(sContext).get(AccountManager_user_id);
        String token = SPUtils.getInstance(sContext).get(AccountManager_account_token);
        setAccount(account, token);
    }

    public void login(String userId, String token) {
        setAccount(userId, token);
    }

    public void logout() {
        setAccount("", "");
    }

    private void setAccount(String userId, String token) {
        SPUtils.getInstance(sContext).put(AccountManager_user_id, userId);
        SPUtils.getInstance(sContext).put(AccountManager_account_token, token);
        if (mAccount == null) {
            mAccount = new Account();
        }
        mAccount.userId = userId;
        mAccount.token = token;
        if (mAccountStatusCallbacks.size() > 0) {
            if (isLogin()) {
                for (AccountStatusCallback accountStatusCallback : mAccountStatusCallbacks) {
                    accountStatusCallback.loginCallback();
                }
            } else {
                for (AccountStatusCallback accountStatusCallback : mAccountStatusCallbacks) {
                    accountStatusCallback.logoutCallback();
                }
            }
        }
    }

    /*初次注册，立即回调一下*/
    public void registerAccountStatusCallBack(AccountStatusCallback statusCallback) {
        if (!mAccountStatusCallbacks.contains(mAccountStatusCallbacks)) {
            mAccountStatusCallbacks.add(statusCallback);
            if (isLogin()) {
                statusCallback.loginCallback();
            } else {
                statusCallback.logoutCallback();
            }
        }
    }

    public void unregisterAccountStatusCallBack(AccountStatusCallback statusCallback) {
        if (mAccountStatusCallbacks.contains(statusCallback)) {
            mAccountStatusCallbacks.remove(statusCallback);
        }
    }
}
