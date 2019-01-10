package com.suntiago.baseui.activity.base;

import android.os.Bundle;

import com.hwangjr.rxbus.RxBus;
import com.suntiago.baseui.activity.ActivityStackManager;
import com.suntiago.baseui.activity.base.theMvp.DataBinderBase;
import com.suntiago.baseui.activity.base.theMvp.databind.DataBinder;
import com.suntiago.baseui.activity.base.theMvp.model.IModel;
import com.suntiago.baseui.activity.base.theMvp.presenter.ActivityPresenter;
import com.suntiago.baseui.utils.log.Slog;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by zy on 2018/12/4.
 */

public abstract class ActivityBase<T extends AppDelegateBase, D extends IModel> extends ActivityPresenter<T, D> {

  protected final String TAG = getClass().getSimpleName();
  //Rxbus注册管理
  private List<Object> mRxBusList;
  /**
   * 用来管理Subscribe的生命周期
   * 在Activity中用到的subscriptin一定要加到这个里面，在onDestroy的时候会
   * 释放掉。
   */
  private CompositeSubscription mCompositeSubscription;
  protected DataBinder binder;

  public DataBinder getDataBinder() {
    return null;
  }

  public final void notifyModelChanged() {
    if (binder != null)
      binder.viewBindModel(viewDelegate, iModel);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void initView(Bundle savedInstanceState) {
    super.initView(savedInstanceState);
    binder = getDataBinder();
    if (binder == null) {
      binder = dataBinderBase();
    }
    initToolbar();
  }

  protected void bindEvenListener() {
    super.bindEvenListener();
    //Activity管理
    ActivityStackManager.getInstance().addActivity(this);
    //请求管理
    mCompositeSubscription = new CompositeSubscription();
    //rxbus事件
    registerRxBus(this);
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

  private final DataBinderBase dataBinderBase() {
    return new DataBinderBase();
  }
}