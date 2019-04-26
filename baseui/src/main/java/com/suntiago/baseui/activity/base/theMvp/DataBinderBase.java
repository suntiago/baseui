package com.suntiago.baseui.activity.base.theMvp;

import com.suntiago.baseui.activity.base.theMvp.databind.DataBinder;
import com.suntiago.baseui.activity.base.theMvp.model.IModel;
import com.suntiago.baseui.activity.base.theMvp.view.AppDelegate;

/**
 * Created by zy on 2018/12/27.
 */

public class DataBinderBase implements DataBinder {
  @Override
  public void viewBindModel(AppDelegate appDelegate, IModel data) {
    if (appDelegate != null) {
      appDelegate.viewBindModelin(data);
    }
  }

  @Override
  public void viewBindModel(AppDelegate appDelegate, Object data) {
    if (appDelegate != null) {
      appDelegate.viewBindModelin(data);
    }
  }

  @Override
  public void viewBindModel(AppDelegate appDelegate, Object data, String tag) {
    if (appDelegate != null) {
      appDelegate.viewBindModelin(data, tag);
    }
  }
}
