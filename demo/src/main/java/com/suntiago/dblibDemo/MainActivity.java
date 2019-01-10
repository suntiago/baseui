package com.suntiago.dblibDemo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.suntiago.baseui.account.AccountManager;
import com.suntiago.baseui.activity.SlothActivity;
import com.suntiago.baseui.activity.base.pickmedia.IMediaPicker;
import com.suntiago.baseui.activity.base.pickmedia.ImagePic;
import com.suntiago.baseui.activity.base.pickmedia.MediaPickerDelegate;
import com.suntiago.baseui.utils.file.StorageHelper;
import com.suntiago.baseui.utils.file.StorageManager;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.dblibDemo.rxjavaTest.RxJavaActivity;
import com.suntiago.dblibDemo.themvpTest.ActivityMvpTest;
import com.suntiago.getpermission.rxpermissions.RxPermissions;
import com.suntiago.lockpattern.PatternManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import rx.functions.Action1;


/**
 * Created by Jeremy on 2018/11/16.
 */

public class MainActivity extends SlothActivity {
  private final String TAG = getClass().getSimpleName();

  TextView tvPatternLoginStatus;
  StorageHelper mStorageHelper = StorageManager.getStorageHelper();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initPattern();
    Slog.d(TAG, "onCreate  [savedInstanceState]:");
    mStorageHelper.initPath("company", "test");
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  public void handleToast(int code, String msg) {
  }

  public void initPattern() {
    if (AccountManager.get().isLogin()) {
      PatternManager.get().accountLogin(AccountManager.get().getUserId());
    }
    tvPatternLoginStatus = (TextView) findViewById(R.id.tv_pattern_login_status);
    tvPatternLoginStatus.setText(PatternManager.get().isPatternSet() ?
        "已设置手势密码" : "未设置手势密码");
  }

  public void patternLogin(View v) {
    if (!PatternManager.get().isPatternSet()) {
      AccountManager.get().login("1234", "5678");
      PatternManager.get().accountLogin(AccountManager.get().getUserId());
      PatternManager.get().setPatternCallback(new PatternManager.PatternCallback() {
        @Override
        public void patternSet() {
          Slog.d(TAG, "patternSet  []:");
          tvPatternLoginStatus.setText(PatternManager.get().isPatternSet() ?
              "已设置手势密码" : "未设置手势密码");
        }

        @Override
        public void patternForget() {
          Slog.d(TAG, "patternForget  []:");
        }

        @Override
        public void patternChecked() {
          Slog.d(TAG, "patternChecked  []:");

        }
      });
      PatternManager.get().setPattern();
    }
  }

  public void patternLogout(View v) {
    AccountManager.get().logout();
    if (PatternManager.get().isPatternSet()) {
      PatternManager.get().accountLoginout();
      tvPatternLoginStatus.setText(PatternManager.get().isPatternSet() ?
          "已设置手势密码" : "未设置手势密码");
    }
  }

  public void patternCheck(View v) {
    if (PatternManager.get().isPatternSet()) {
      PatternManager.get().checkoutPattern();
    } else {
      Toast.makeText(this, "请先设置手势密码", Toast.LENGTH_SHORT).show();
    }
  }

  public void onclickTestMvp(View view) {
    startActivitySloth(new Intent(this, ActivityMvpTest.class));
  }

  public void onclickPickVideo(View view) {
    MediaPickerDelegate.get().pickVideo(this, new IMediaPicker.PMVCallback() {
      @Override
      public void pickMV(String videoPath) {
        Slog.d(TAG, "onclickPickVideo pickMV  [videoPath]:" + videoPath);

      }
    });
  }

  public void onclickTakeVideo(View view) {
    MediaPickerDelegate.get().configPickVideo(1, 5).takeVideo(this, new IMediaPicker.PMVCallback() {
      @Override
      public void pickMV(String videoPath) {
        Slog.d(TAG, "onclickTakeVideo pickMV  [videoPath]:" + videoPath);
      }
    });
  }

  public void onclickTakePhoto(View view) {
    MediaPickerDelegate.get().pickPicture(this, false, new IMediaPicker.PP1Callback() {
      @Override
      public void pickPic(ImagePic imagePics) {
        Slog.d(TAG, "onclickTakePhoto pickPic  [imagePics]: \n"
            + "CompressPath: " + imagePics.getCompressPath() + "\n"
            + "OriginalPath: " + imagePics.getOriginalPath());

      }
    });
  }

  public void onclickPickPhoto(View view) {
    MediaPickerDelegate.get().pickPictures(this, true, 4, new IMediaPicker.PPsCallback() {
      @Override
      public void pickPic(ArrayList<ImagePic> imagePics) {
        if (imagePics != null && imagePics.size() > 0) {
          Slog.d(TAG, "onclickPickPhoto pickPic  [imagePics]:" + imagePics.size());
          for (ImagePic pic : imagePics) {
            Slog.d(TAG, "onclickTakePhoto pickPic  [imagePics]: \n"
                + "CompressPath: " + pic.getCompressPath() + "\n"
                + "OriginalPath: " + pic.getOriginalPath());
          }
        } else {
          Slog.d(TAG, "onclickPickPhoto pickPic  [imagePics]: null");
        }

      }
    });
  }

  public void onclickRxjava(View view) {
    startActivitySloth(new Intent(this, RxJavaActivity.class));
  }

  public void onclickSaveMax(View view) {
    RxPermissions.getInstance(this)
        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .subscribe(new Action1<Boolean>() {
          @Override
          public void call(Boolean granted) {
            try {
              saveByteToLocal("123", "test", System.currentTimeMillis() + ".txt");
            } catch (IOException e) {
              e.printStackTrace();
            }
            mStorageHelper.checkAndDelLowPriority("test", 5);
          }
        });

  }

  public void onclickSaveValidity(View view) {
    RxPermissions.getInstance(this)
        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .subscribe(new Action1<Boolean>() {
          @Override
          public void call(Boolean granted) {
            mStorageHelper.checkAndDelOutOfDate("test2", 60000);
            try {
              saveByteToLocal("123", "test2", System.currentTimeMillis() + ".txt");
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
  }

  /**
   * 保存字节到本地
   *
   * @param ss 字符串内容
   * @return
   */
  public boolean saveByteToLocal(String ss, String filepath, String name) throws IOException {
    byte[] bytes = ss.getBytes();
    FileOutputStream output = null;
    try {
      if (bytes != null) {
        String p = mStorageHelper.getFilePath(filepath, name);
        File file = new File(p);
        output = new FileOutputStream(file);
        output.write(bytes);
        output.flush();
        return true;
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw e;
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      try {
        if (output != null) output.close();
      } catch (IOException e) {
        e.printStackTrace();
        throw e;
      }
    }
    return false;
  }

  public void onclickClearSd(View view) {
    mStorageHelper.clearAllCache(this);
  }
}
