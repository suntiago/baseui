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
package com.suntiago.baseui.activity.base.theMvp.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.suntiago.baseui.activity.base.theMvp.model.IModel;
import com.suntiago.baseui.utils.log.Slog;

/**
 * View delegate base class
 * 视图层代理的基类
 *
 * @author kymjs (http://www.kymjs.com/) on 10/23/15.
 */
public abstract class AppDelegate<D extends IModel> implements IDelegate {
  private final String TAG = getClass().getSimpleName();
  protected final SparseArray<View> mViews = new SparseArray<View>();

  protected View rootView;

  public abstract int getRootLayoutId();

  @Override
  public void create(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    int rootLayoutId = getRootLayoutId();
    rootView = inflater.inflate(rootLayoutId, container, false);
  }

  @Override
  public int getOptionsMenuId() {
    return 0;
  }

  public Toolbar getToolbar() {
    return null;
  }

  @Override
  public View getRootView() {
    return rootView;
  }

  public void setRootView(View rootView) {
    this.rootView = rootView;
  }

  @Override
  public void initWidget() {
  }

  public <T extends View> T bindView(int id) {
    T view = (T) mViews.get(id);
    if (view == null) {
      view = (T) rootView.findViewById(id);
      mViews.put(id, view);
    }
    return view;
  }

  public <T extends View> T get(int id) {
    return (T) bindView(id);
  }

  public void setOnClickListener(View.OnClickListener listener, int... ids) {
    if (ids == null) {
      return;
    }
    for (int id : ids) {
      get(id).setOnClickListener(listener);
    }
  }

  public void toast(CharSequence msg) {
    Toast.makeText(rootView.getContext(), msg, Toast.LENGTH_SHORT).show();
  }

  public <T extends Activity> T getActivity() {
    return (T) rootView.getContext();
  }

  public abstract void viewBindModel(D data);

  /*关联 Activity 生命周期， onCreate*/
  @CallSuper
  public void onACreate() {
    Slog.state(TAG, "onACreate  []:");
  }

  /*关联 Activity 生命周期， onStart*/
  @CallSuper
  public void onAStart() {
    Slog.state(TAG, "onAStart  []:");
  }

  /*关联 Activity 生命周期， onResume*/
  @CallSuper
  public void onAResume() {
    Slog.state(TAG, "onAResume  []:");
  }

  /*关联 Activity 生命周期， onPause*/
  @CallSuper
  public void onAPause() {
    Slog.state(TAG, "onAPause  []:");
  }

  /*关联 Activity 生命周期， onStop*/
  @CallSuper
  public void onASTop() {
    Slog.state(TAG, "onASTop  []:");
  }

  @CallSuper
  public void onADestory() {
    Slog.state(TAG, "onADestory  []:");
  }

}
