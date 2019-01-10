package com.suntiago.dblibDemo.themvpTest;

import android.annotation.SuppressLint;
import android.os.Parcel;

import com.suntiago.baseui.activity.base.theMvp.model.IModel;

/**
 * Created by zy on 2018/12/27.
 */

@SuppressLint("ParcelCreator")
public class JavaBeanT implements IModel {
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {

  }
}
