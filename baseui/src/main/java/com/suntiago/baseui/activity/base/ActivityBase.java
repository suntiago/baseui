package com.suntiago.baseui.activity.base;

import android.os.Bundle;

import com.hwangjr.rxbus.RxBus;
import com.kymjs.themvp.presenter.ActivityPresenter;
import com.suntiago.baseui.activity.ActivityStackManager;
import com.suntiago.baseui.utils.log.Slog;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by zy on 2018/12/4.
 */

public abstract class ActivityBase<T extends AppDelegateBase> extends ActivityPresenter<AppDelegateBase> {

    protected final String TAG = getClass().getSimpleName();
    //Rxbus注册管理
    private List<Object> mRxBusList;
    /**
     * 用来管理Subscribe的生命周期
     * 在Activity中用到的subscriptin一定要加到这个里面，在onDestroy的时候会
     * 释放掉。
     */
    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();
        registerRxBus(this);
        //Activity管理
        ActivityStackManager.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        Slog.state(TAG, "---------onDestroy ");
        super.onDestroy();
        unregisterRxBus();
        ActivityStackManager.getInstance().removeActivity(this);
        mCompositeSubscription.unsubscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewDelegate.activityResume(true);
    }

    @Override
    protected void onPause() {
        viewDelegate.activityResume(false);
        super.onPause();
    }

    protected abstract void bindEvenListener();

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
}