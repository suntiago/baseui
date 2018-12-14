package com.suntiago.dblibDemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.suntiago.baseui.account.AccountManager;
import com.suntiago.baseui.activity.SlothActivity;
import com.suntiago.baseui.activity.base.pickmedia.IMediaPicker;
import com.suntiago.baseui.activity.base.pickmedia.MediaPickerDelegate;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.dblibDemo.themvpTest.ActivityMvpTest;
import com.suntiago.lockpattern.PatternManager;


/**
 * Created by Jeremy on 2018/11/16.
 */

public class MainActivity extends SlothActivity {
    private final String TAG = getClass().getSimpleName();

    TextView tvPatternLoginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPattern();
        Slog.d(TAG, "onCreate  [savedInstanceState]:");
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
}
