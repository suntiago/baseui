package com.suntiago.dblibDemo.themvpTest;

import android.annotation.SuppressLint;
import android.os.Parcel;

import com.suntiago.baseui.activity.base.theMvp.model.BaseModel;

/**
 * Created by zy on 2018/12/27.
 */

@SuppressLint("ParcelCreator")
public class JavaBeanT extends BaseModel {
  String name;


  public void setName(String name) {
    this.name = name;
    notifyModelChanged(name, "name");
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {

  }
}
