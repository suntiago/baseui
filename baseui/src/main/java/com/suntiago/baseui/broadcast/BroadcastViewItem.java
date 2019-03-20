package com.suntiago.baseui.broadcast;

import android.view.View;

/**
 * Created by zy on 2019/1/29.
 */

public interface BroadcastViewItem<T> {
  View bindView(T data);

  View createView();

  View getView();

  void start();

  void resume();

  void pause();

  void stop();

  void destoryView();

  void setBroadcastProgress(BroadcastProgress progress);
}
