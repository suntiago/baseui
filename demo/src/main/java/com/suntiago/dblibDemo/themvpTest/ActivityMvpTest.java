package com.suntiago.dblibDemo.themvpTest;

import android.os.Bundle;
import android.view.View;

import com.suntiago.baseui.activity.base.ActivityBase;
import com.suntiago.baseui.activity.base.theMvp.databind.DataBinder;
import com.suntiago.dblibDemo.R;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by zy on 2018/12/4.
 */

public class ActivityMvpTest extends ActivityBase<MvpTestDelegate, JavaBeanT> {
  @Override
  protected Class<MvpTestDelegate> getDelegateClass() {
    return MvpTestDelegate.class;
  }

  @Override
  protected Class<JavaBeanT> getModelClass() {
    return JavaBeanT.class;
  }

  @Override
  public DataBinder getDataBinder() {
    return new DatabindT();
  }

  @Override
  protected void dataPreloaded(Bundle savedInstanceState) {
    super.dataPreloaded(savedInstanceState);
    viewDelegate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        viewDelegate.showProgress();
        //
        getName();
      }
    }, R.id.btn_show_progress);
  }

  private void getName() {
    Observable.just("Hello, world! this is new mvp")
        .subscribe(new Action1<String>() {
          @Override
          public void call(String s) {
            notifyModelChanged(s, "name");
          }
        });
  }
}
