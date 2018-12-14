package com.suntiago.baseui.activity.base.pickmedia;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;

import com.suntiago.baseui.R;
import com.suntiago.baseui.utils.FileUtils;
import com.suntiago.baseui.utils.ToastUtils;
import com.suntiago.getpermission.rxpermissions.RxPermissions;

import rx.functions.Action1;

/**
 * Created by zy on 2018/12/14.
 */

public class ShadowPickMediaActivity extends Activity {
    private final String TAG = getClass().getSimpleName();
    MediaPickerDelegate mMediaPickerDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        mMediaPickerDelegate = MediaPickerDelegate.getMediaPicker();
        String action = intent.getStringExtra("action");
        String type = intent.getStringExtra("type");
        if ("video".equals(action)) {
            if ("camera".equals(type)) {
                takeVideo();
            } else if ("local".equals(type)) {
                chooseVideoFromLocal();
            }
        } else if ("image".equals(action)) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VIDEO_CAMERA) {
            String path = null;
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                path = FileUtils.getPath(ShadowPickMediaActivity.this, uri);
            }
            mMediaPickerDelegate.videoCallback(path);
            finish();
        } else if (requestCode == REQUEST_CODE_VIDEO_LOCAL) {
            String path = null;
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                path = FileUtils.getPath(ShadowPickMediaActivity.this, uri);
            }
            mMediaPickerDelegate.videoCallback(path);
            finish();
        }
    }

    final static int REQUEST_CODE_VIDEO_LOCAL = 100002;
    final static int REQUEST_CODE_VIDEO_CAMERA = 100003;

    /**
     * 本地选择视频
     */
    private void chooseVideoFromLocal() {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (!granted) {
                            ToastUtils.showToast(ShadowPickMediaActivity.this,
                                    getResources().getString(R.string.please_turn_on_the_permission_of_external_storage));
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + ShadowPickMediaActivity.this.getPackageName()));
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent();
                            intent.setType("video/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(intent, REQUEST_CODE_VIDEO_LOCAL);
                        }
                    }
                });
    }

    /**
     * 拍摄视频
     */
    private void takeVideo() {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (!granted) {
                            ToastUtils.showToast(ShadowPickMediaActivity.this,
                                    getResources().getString(R.string.please_turn_on_the_permission_of_camera));
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + ShadowPickMediaActivity.this.getPackageName()));
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent();
                            intent.setAction("android.media.action.VIDEO_CAPTURE");
                            intent.addCategory("android.intent.category.DEFAULT");
                            ConfigVideoPick m = mMediaPickerDelegate.getConfigVideoPick();
                            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, m.durationLimit);//限制录制时间(10秒=10)
                            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, m.quality);
                            startActivityForResult(intent, REQUEST_CODE_VIDEO_CAMERA);
                        }
                    }
                });
    }
}
