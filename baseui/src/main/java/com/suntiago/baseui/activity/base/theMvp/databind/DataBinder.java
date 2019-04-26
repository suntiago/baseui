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
package com.suntiago.baseui.activity.base.theMvp.databind;


import com.suntiago.baseui.activity.base.theMvp.model.IModel;
import com.suntiago.baseui.activity.base.theMvp.view.AppDelegate;

/**
 * ViewModel实现
 *
 * @author kymjs (http://www.kymjs.com/) on 10/30/15.
 * <p>
 *
 * 改为使用BindingD 方式来实现
 */
@Deprecated
public interface DataBinder<T extends AppDelegate, D extends IModel> {

  /**
   * 将数据与View绑定，这样当数据改变的时候，框架就知道这个数据是和哪个View绑定在一起的，就可以自动改变ui
   * 当数据改变的时候，会回调本方法。
   *
   * @param appDelegate 视图层代理
   * @param data        数据模型对象
   */
  void viewBindModel(T appDelegate, D data);

  /**
   * 将数据与View绑定，这样当数据改变的时候，框架就知道这个数据是和哪个View绑定在一起的，就可以自动改变ui
   * 当数据改变的时候，会回调本方法。
   *
   * @param appDelegate 视图层代理
   * @param data        数据模型对象
   */
  void viewBindModel(T appDelegate, Object data);

  /**
   * 将数据与View绑定，这样当数据改变的时候，框架就知道这个数据是和哪个View绑定在一起的，就可以自动改变ui
   * 当数据改变的时候，会回调本方法。
   *
   * @param appDelegate 视图层代理
   * @param data        数据模型对象
   * @param tag         数据-UI 配对标识
   */
  void viewBindModel(T appDelegate, Object data, String tag);
}
