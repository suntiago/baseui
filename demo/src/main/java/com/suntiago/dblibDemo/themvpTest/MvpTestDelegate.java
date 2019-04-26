package com.suntiago.dblibDemo.themvpTest;

import android.widget.TextView;

import com.suntiago.baseui.activity.base.AppDelegateBase;
import com.suntiago.baseui.activity.base.theMvp.annotation.BindingD;
import com.suntiago.dblibDemo.R;

/**
 * Created by zy on 2018/12/4.
 */

public class MvpTestDelegate extends AppDelegateBase<JavaBeanT> {
  @Override
  public int getRootLayoutId() {
    return R.layout.activity_mvp_test;
  }

  @Override
  public void initWidget() {

  }

  @Override
  public void viewBindModel(JavaBeanT data) {
  }

  private void nameChanged(String name) {
    TextView textView = get(R.id.tv_hello_world);
    textView.setText(name);
  }

  @BindingD(tag = "name")
  public void name(String n) {
    nameChanged(n);
  }
}
