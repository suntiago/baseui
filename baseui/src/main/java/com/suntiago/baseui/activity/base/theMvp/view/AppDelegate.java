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

import com.suntiago.baseui.activity.base.theMvp.annotation.BindingD;
import com.suntiago.baseui.activity.base.theMvp.annotation.BindingKey;
import com.suntiago.baseui.activity.base.theMvp.annotation.BindingValue;
import com.suntiago.baseui.activity.base.theMvp.model.IModel;
import com.suntiago.baseui.utils.log.Slog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

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
    injectBinding(this);
  }

  @Override
  public int getOptionsMenuId() {
    return 0;
  }

  @Override
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

  /**
   * 关联 Activity 生命周期， onCreate
   */
  @CallSuper
  public void onACreate() {
    Slog.state(TAG, "onACreate  []:");
  }

  /**
   * 关联 Activity 生命周期， onStart
   */
  @CallSuper
  public void onAStart() {
    Slog.state(TAG, "onAStart  []:");
  }

  /**
   * 关联 Activity 生命周期， onResume
   */
  @CallSuper
  public void onAResume() {
    Slog.state(TAG, "onAResume  []:");
  }

  /**
   * 关联 Activity 生命周期， onPause
   */
  @CallSuper
  public void onAPause() {
    Slog.state(TAG, "onAPause  []:");
  }

  /**
   * 关联 Activity 生命周期， onStop
   */
  @CallSuper
  public void onASTop() {
    Slog.state(TAG, "onASTop  []:");
  }

  @CallSuper
  public void onADestory() {
    Slog.state(TAG, "onADestory  []:");
  }

  /**
   * 将数据与View绑定，这样当数据改变的时候，框架就知道这个数据是和哪个View绑定在一起的，就可以自动改变ui
   * 当数据改变的时候，会回调本方法。
   *
   * @param data 数据模型对象
   */
  public abstract void viewBindModel(D data);

  /**
   * 将数据与View绑定，这样当数据改变的时候，框架就知道这个数据是和哪个View绑定在一起的，就可以自动改变ui
   * 当数据改变的时候，会回调本方法。
   *
   * @param data 数据模型对象
   */
  public abstract void viewBindModelin(D data);

  /**
   * 将数据与View绑定，这样当数据改变的时候，框架就知道这个数据是和哪个View绑定在一起的，就可以自动改变ui
   * 当数据改变的时候，会回调本方法。
   *
   * @param data 数据模型对象
   */
  public abstract void viewBindModelin(Object data);

  /**
   * 将数据与View绑定，这样当数据改变的时候，框架就知道这个数据是和哪个View绑定在一起的，就可以自动改变ui
   * 当数据改变的时候，会回调本方法。
   *
   * @param data 数据模型对象
   * @param tag  数据-UI 配对标识
   */
  public abstract void viewBindModelin(Object data, String tag);

  final public boolean dataBinding(Object data, String tag) {
    if (tag == null && data == null) {
      return false;
    }
    if (tag == null) {
      tag = "";
    }
    if (mBindingHashMap != null) {
      BindingValue v = mBindingHashMap.get(new BindingKey(tag, data.getClass()));
      if (v != null) {
        try {
          v.produce(data);
          return true;
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }

  HashMap<BindingKey, BindingValue> mBindingHashMap;

  final protected void injectBinding(Object listener) {
    Class<?> listenerClass = listener.getClass();
    Method[] methods = listenerClass.getDeclaredMethods();
    if (mBindingHashMap == null) {
      mBindingHashMap = new HashMap<>();
    }
    for (int i = 0; i < methods.length; i++) {
      Method method = methods[i];
      if (method.isBridge()) {
        return;
      }

      if (method.isAnnotationPresent(BindingD.class)) {
        Class[] p = method.getParameterTypes();
        if (p.length != 1) {
          throw new IllegalArgumentException("Method " + method +
              " has @Subscribe annotation but requires " + p.length +
              " arguments.  Methods must require a single argument.");
        }
        Class parameterClazz = p[0];
        if (parameterClazz.isInterface()) {
          throw new IllegalArgumentException("Method " + method +
              " has @Subscribe annotation on " + parameterClazz +
              " which is an interface.  Subscription must be on a concrete class type.");
        }

        if ((method.getModifiers() & 1) == 0) {
          throw new IllegalArgumentException("Method " + method +
              " has @Subscribe annotation on " + parameterClazz + " but is not 'public'.");
        }

        BindingD annotation = (BindingD) method.getAnnotation(BindingD.class);
        String tag = annotation.tag();
        BindingKey k = new BindingKey(tag, parameterClazz);
        BindingValue v = new BindingValue(listener, method);
        if (!mBindingHashMap.containsKey(k)) {
          mBindingHashMap.put(k, v);
        } else {
          throw new IllegalArgumentException("Method " + method +
              " has @Subscribe annotation on " + parameterClazz + " but is not unique");
        }
      }
    }
  }

}
