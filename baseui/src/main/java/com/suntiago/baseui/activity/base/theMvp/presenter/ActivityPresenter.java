/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.suntiago.baseui.activity.base.theMvp.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.suntiago.baseui.activity.base.theMvp.model.IModel;
import com.suntiago.baseui.activity.base.theMvp.view.IDelegate;
import com.suntiago.baseui.utils.log.Slog;


/**
 * Presenter base class for Activity
 * Presenter层的实现基类
 *
 * @param <T> View delegate class type
 * @author kymjs (http://www.kymjs.com/) on 10/23/15.
 */
public abstract class ActivityPresenter<T extends IDelegate, D extends IModel> extends AppCompatActivity {
  private final String TAG = getClass().getSimpleName();
  final static String ACTIVITY_DATA_STATE = "ACTIVITY_DATA_STATE";
  protected T viewDelegate;
  protected D iModel;

  public ActivityPresenter() {
    try {
      Class<T> tClass = getDelegateClass();
      if (tClass != null) {
        viewDelegate = tClass.newInstance();
      } else {
        viewDelegate = null;
      }
    } catch (InstantiationException e) {
      e.printStackTrace();
      throw new RuntimeException("create IDelegate error");
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      throw new RuntimeException("create IDelegate error");
    }
    if (viewDelegate == null) {
      throw new NullPointerException("ViewDelegate is null, you must implement method getDelegateClass() correctly!");
    }

    try {
      Class<D> dClass = getModelClass();
      if (dClass != null) {
        iModel = dClass.newInstance();
      }
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    if (iModel == null) {
      Slog.e(TAG, "ActivityPresenter:iModel is null");
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Slog.state(TAG, "onCreate");
    //注册一些时间，比如rxbus, eventbus, 一些回调等
    bindEvenListener();
    preLoadData(savedInstanceState);
    //初始化界面
    initView(savedInstanceState);
    //初始化数据
    initData(savedInstanceState);
  }

  //数据预加载，加载本子缓存数据，加载完数据之后
  protected void preLoadData(Bundle savedInstanceState) {
    Slog.state(TAG, "preLoadData");
  }

  //界面刷新完成，进一步获取数据
  @CallSuper
  protected synchronized void initData(Bundle savedInstanceState) {
    Slog.state(TAG, "initData");

  }


  //数据预加载完成，开始绘制界面，此时界面可以用到一些数据
  @CallSuper
  protected synchronized void initView(Bundle savedInstanceState) {
    Slog.state(TAG, "initView");
    viewDelegate.create(getLayoutInflater(), null, savedInstanceState);
    setContentView(viewDelegate.getRootView());
  }

  @CallSuper
  protected void bindEvenListener() {
    Slog.state(TAG, "bindEvenListener");
  }

  @CallSuper
  protected void initToolbar() {
    Slog.state(TAG, "initToolbar");
    viewDelegate.getToolbar();
  }

  @Override
  @CallSuper
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    Slog.state(TAG, "onRestoreInstanceState");
    if (savedInstanceState != null) {
      iModel = savedInstanceState.getParcelable(ACTIVITY_DATA_STATE);
    }
    if (viewDelegate == null) {
      try {
        viewDelegate = getDelegateClass().newInstance();
      } catch (InstantiationException e) {
        throw new RuntimeException("create IDelegate error");
      } catch (IllegalAccessException e) {
        throw new RuntimeException("create IDelegate error");
      }
    }
  }

  @CallSuper
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Slog.state(TAG, "onSaveInstanceState");
    outState.putParcelable(ACTIVITY_DATA_STATE, iModel);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Slog.state(TAG, "onCreateOptionsMenu");
    if (viewDelegate.getOptionsMenuId() != 0) {
      getMenuInflater().inflate(viewDelegate.getOptionsMenuId(), menu);
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Slog.state(TAG, "onDestroy");
    iModel = null;
    viewDelegate = null;
  }

  @Override
  protected void onResume() {
    super.onResume();
    Slog.state(TAG, "onResume");
  }

  @Override
  protected void onPause() {
    super.onPause();
    Slog.state(TAG, "onPause");
  }

  @Override
  protected void onStart() {
    super.onStart();
    Slog.state(TAG, "onStart");
  }

  @Override
  protected void onStop() {
    super.onStop();
    Slog.state(TAG, "onStart");
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Slog.state(TAG, "onNewIntent");
  }

  protected abstract Class<T> getDelegateClass();

  protected abstract Class<D> getModelClass();

}
