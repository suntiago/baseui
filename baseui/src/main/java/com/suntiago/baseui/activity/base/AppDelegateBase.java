package com.suntiago.baseui.activity.base;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.suntiago.baseui.R;
import com.suntiago.baseui.activity.base.theMvp.model.IModel;
import com.suntiago.baseui.activity.base.theMvp.view.AppDelegate;
import com.suntiago.baseui.activity.view.ProgressDialog;
import com.suntiago.baseui.utils.ToastUtils;

/**
 * Created by zy on 2018/12/4.
 */

public abstract class AppDelegateBase<D extends IModel> extends AppDelegate<D> {

  private boolean mResume = false;
  /*加载框*/
  protected ProgressDialog mProgressDlg;
  boolean mNeedToDismissProgressDlg = false;
  boolean mNeedToShowProgressDlg = false;
  Toolbar mToolbar = null;

  @Override
  public abstract void initWidget();

  protected void activityResume(boolean resume) {
    this.mResume = resume;
    checkProgress();
  }

  @Override
  public Toolbar getToolbar() {
    if (mToolbar == null && rootView instanceof ViewGroup) {
      mToolbar = (Toolbar) LayoutInflater.from(rootView.getContext()).inflate(
          R.layout.element_toolbar, (ViewGroup) rootView, false);
      ((ViewGroup) rootView).addView(mToolbar, 0);
    }
    return mToolbar;
  }

  @Override
  public void toast(CharSequence msg) {
    ToastUtils.showToast(this.getActivity(), msg.toString());
  }

  public void showProgress() {
    if (mProgressDlg == null) {
      mProgressDlg = ProgressDialog.newInstance();
    }
    if (mResume) {
      mProgressDlg.show(getActivity().getFragmentManager(), "");
      mNeedToShowProgressDlg = false;
    } else {
      mNeedToShowProgressDlg = true;
    }
  }

  private void checkProgress() {
    if (mProgressDlg != null && mResume) {
      if (mNeedToShowProgressDlg) {
        mProgressDlg.show(getActivity().getFragmentManager(), "");
        mNeedToShowProgressDlg = false;
      }
      if (mNeedToDismissProgressDlg) {
        mProgressDlg.dismiss();
        mNeedToDismissProgressDlg = false;
      }
    }
  }

  public void dismissProgress() {
    if (mProgressDlg != null) {
      if (mResume) {
        mProgressDlg.dismiss();
        mNeedToDismissProgressDlg = false;
      } else {
        mNeedToDismissProgressDlg = true;
      }
    }
  }

  public void showToast(String msg) {
    ToastUtils.showToast(getActivity(), msg);
  }

  public void showToastLong(String msg) {
    ToastUtils.showToastLong(getActivity(), msg);
  }

  public void handleToast(int code, String msg) {
  }

  @Override
  public void viewBindModel(D data) {

  }

  @Override
  public void viewBindModelin(D data) {

  }

  @Override
  public void viewBindModelin(Object data) {

  }

  @Override
  public void viewBindModelin(Object data, String tag) {

  }
}
