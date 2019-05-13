package com.suntiago.baseui.activity.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.suntiago.baseui.R;
import com.suntiago.baseui.utils.log.Slog;


public class ProgressDialog extends DialogFragment {

  private static final String TAG = ProgressDialog.class.getSimpleName();

  public static ProgressDialog newInstance() {
    ProgressDialog f = new ProgressDialog();
    return f;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    if (getDialog() == null) {  // Returns mDialog
      Slog.e(TAG, "ProgressDialog dialog is null");
    }
    super.onActivityCreated(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Dialog dialog = getDialog();
    if (dialog == null) {
      return null;
    }
    Window window = dialog.getWindow();
    if (window == null) {
      return null;
    }
    window.requestFeature(Window.FEATURE_NO_TITLE);
    window.setBackgroundDrawableResource(android.R.color.transparent);

    View view = inflater.inflate(R.layout.dialog_progress_layout,
            container, false);
    ImageView loading = (ImageView) view.findViewById(R.id.dialog_progress_img);
    AnimationDrawable animationDrawable = (AnimationDrawable) loading.getDrawable();
    animationDrawable.start();
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    Dialog dialog = getDialog();
    if (dialog == null) {
      return;
    }
    Window window = dialog.getWindow();
    if (window == null) {
      return;
    }
    int width = getResources().getDimensionPixelSize(R.dimen.dialog_progress_width);
    int height = getResources().getDimensionPixelSize(R.dimen.dialog_progress_height);
    window.setLayout(width, height);
    window.setGravity(Gravity.CENTER);
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
  }
}
