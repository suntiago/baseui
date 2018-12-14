package com.suntiago.baseui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hwangjr.rxbus.RxBus;
import com.suntiago.baseui.activity.view.ProgressDialog;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.baseui.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


/**
 * 请使用{@link com.suntiago.baseui.activity.base.ActivityBase}
 */

@Deprecated
@SuppressLint("Registered")
public abstract class SlothActivity extends AppCompatActivity {

    protected final String TAG = getClass().getSimpleName();
    //标记当前状态，
    boolean mResume = false;
    //带启动intent
    Intent mIntentToStart = null;
    //Rxbus注册管理
    private List<Object> mRxBusList;
    /*加载框*/
    protected ProgressDialog mProgressDlg;

    boolean mNeedToDismissProgressDlg = false;
    boolean mNeedToShowProgressDlg = false;

    /**
     * 用来管理Subscribe的生命周期
     * 在Activity中用到的subscriptin一定要加到这个里面，在onDestroy的时候会
     * 释放掉。
     */
    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Slog.state(TAG, "---------onCreate");
        super.onCreate(savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();
        registerRxBus(this);
        //Activity管理
        ActivityStackManager.getInstance().addActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Slog.state(TAG, "---------onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mResume = true;
        startActivity();
        checkProgress();
        Slog.state(TAG, "---------onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mResume = false;
        Slog.state(TAG, "---------onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Slog.state(TAG, "---------onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Slog.state(TAG, "---------onRestart");
    }

    @Override
    protected void onDestroy() {
        Slog.state(TAG, "---------onDestroy ");
        super.onDestroy();
        unregisterRxBus();
        ActivityStackManager.getInstance().removeActivity(this);
        mCompositeSubscription.unsubscribe();
    }

    public void startActivitySloth(Intent intent) {
        if (intent != null) {
            if (mResume) {
                startActivity(intent);
            } else {
                mIntentToStart = intent;
            }
        }
    }

    private void startActivity() {
        if (mIntentToStart != null && mResume) {
            startActivity(mIntentToStart);
            mIntentToStart = null;
        }
    }

    public void addRxSubscription(Subscription sub) {
        if (sub != null) {
            mCompositeSubscription.add(sub);
        }
    }

    public void removeRxSubscription(Subscription sub) {
        mCompositeSubscription.remove(sub);
    }

    public void registerRxBus(Object o) {
        if (mRxBusList == null) {
            mRxBusList = new ArrayList<>();
        }
        if (mRxBusList.contains(o)) {
            return;
        }
        RxBus.get().register(o);
        mRxBusList.add(o);
    }

    public void unregisterRxBus(Object o) {
        if (mRxBusList != null && mRxBusList.contains(o)) {
            RxBus.get().unregister(o);
            mRxBusList.remove(o);
        }
    }

    public void unregisterRxBus() {
        if (mRxBusList != null && mRxBusList.size() > 0) {
            for (Object o : mRxBusList) {
                RxBus.get().unregister(o);
            }
            mRxBusList.clear();
        }
    }

    public void showProgress() {
        if (mProgressDlg == null) {
            mProgressDlg = ProgressDialog.newInstance();
        }
        if (mResume) {
            mProgressDlg.show(getFragmentManager(), "");
            mNeedToShowProgressDlg = false;
        } else {
            mNeedToShowProgressDlg = true;
        }
    }

    private void checkProgress() {
        if (mProgressDlg != null && mResume) {
            if (mNeedToShowProgressDlg) {
                mProgressDlg.show(getFragmentManager(), "");
                mNeedToShowProgressDlg = false;
            }
            if (mNeedToDismissProgressDlg) {
                mProgressDlg.dismiss();
                mNeedToDismissProgressDlg = false;
            }
        }
    }

    public void dismissProgress() {
        if (mProgressDlg != null) {
            if (mResume) {
                mProgressDlg.dismiss();
                mNeedToDismissProgressDlg = false;
            } else {
                mNeedToDismissProgressDlg = true;
            }
        }
    }

    public void showToast(String msg) {
        ToastUtils.showToast(this, msg);
    }

    public void showToastLong(String msg) {
        ToastUtils.showToastLong(this, msg);
    }

    public abstract void handleToast(int code, String msg);

    /**
     * 如果堆栈中没有Activity，就启动MainActivity
     */
    public void startMainActivityIfNeed(Context context) {
        ActivityStackManager asm = ActivityStackManager.getInstance();
        if (asm.activityCount() == 1) {
            Intent intent = new Intent("viroyal.action.main");
            context.startActivity(intent);
        }
    }
}
