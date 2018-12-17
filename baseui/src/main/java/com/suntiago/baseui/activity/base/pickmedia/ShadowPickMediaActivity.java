package com.suntiago.baseui.activity.base.pickmedia;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.jph.takephoto.uitl.TFileUtils;
import com.suntiago.baseui.R;
import com.suntiago.baseui.utils.FileUtils;
import com.suntiago.baseui.utils.ToastUtils;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.getpermission.rxpermissions.RxPermissions;

import java.io.File;
import java.util.ArrayList;

import rx.functions.Action1;

/**
 * Created by zy on 2018/12/14.
 */

public class ShadowPickMediaActivity extends Activity implements TakePhoto.TakeResultListener,
        InvokeListener {
    private final String TAG = getClass().getSimpleName();
    MediaPickerDelegate mMediaPickerDelegate;

    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TFileUtils.setCachePath(FileUtils.getSDCardPath() + "/image/");
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
        getTakePhoto().onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
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
        } else if ("photo".equals(action)) {
            int limit = intent.getIntExtra("amountLimit", 1);
            if ("camera".equals(type)) {
                takePhoto(limit);
            } else if ("local".equals(type)) {
                choosePhotoFromLocal(limit);
            }
        }
    }

    private void choosePhotoFromLocal(final int limit) {
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
                            ConfigPicturePick configPicturePick = mMediaPickerDelegate.getConfigPicturePick();
                            CropOptions cropOptions = null;
                            if (configPicturePick.crop) {
                                cropOptions = new CropOptions.Builder().setAspectX(configPicturePick.aspectX)
                                        .setAspectY(configPicturePick.aspectY).setWithOwnCrop(true)
                                        .create();
                            }
                            setTakePhotoConfig(true, cropOptions, configPicturePick, limit);
                        }
                    }
                });

    }

    private void takePhoto(final int limit) {
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
                            ConfigPicturePick configPicturePick = mMediaPickerDelegate.getConfigPicturePick();
                            CropOptions cropOptions = null;
                            if (configPicturePick.crop) {
                                cropOptions = new CropOptions.Builder().setAspectX(configPicturePick.aspectX)
                                        .setAspectY(configPicturePick.aspectY).setWithOwnCrop(true)
                                        .create();
                            }
                            setTakePhotoConfig(false, cropOptions, configPicturePick, limit);
                        }
                    }
                });
    }

    /**
     * 设置各种参数
     */
    protected void setTakePhotoConfig(boolean isGallery, CropOptions cropOptions, ConfigPicturePick configPicturePick, int imageInsert) {
        File file = new File(FileUtils.getSDCardPath(), "/image/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        takePhoto = getTakePhoto();
        CompressConfig config = new CompressConfig.Builder()
                .setMaxPixel(configPicturePick.maxPixel)
                .enableReserveRaw(configPicturePick.enableReserveRaw)
                .enableQualityCompress(configPicturePick.enableQualityCompress)
                .enablePixelCompress(configPicturePick.enablePixelCompress)
                .create();
        takePhoto.onEnableCompress(config, false);
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(true);
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
        if (isGallery) {
            if (imageInsert > 1) {
                if (cropOptions != null) takePhoto.onPickMultipleWithCrop(imageInsert, cropOptions);
                else takePhoto.onPickMultiple(imageInsert);
            } else if (imageInsert == 1) {
                if (cropOptions != null) takePhoto.onPickFromGalleryWithCrop(imageUri, cropOptions);
                else takePhoto.onPickFromGallery();
            }
        } else {
            if (cropOptions != null) takePhoto.onPickFromCaptureWithCrop(imageUri, cropOptions);
            else takePhoto.onPickFromCapture(imageUri);
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
        } else {
            getTakePhoto().onActivityResult(requestCode, resultCode, data);
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
                            File file = new File(FileUtils.getSDCardPath(), "/video/" + System.currentTimeMillis());
                            if (file.exists()) {
                                file.delete();
                            }
                            Uri uri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", file);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);//限制录制时间(10秒=10)
                            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                            startActivityForResult(intent, REQUEST_CODE_VIDEO_CAMERA);
                        }
                    }
                });
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam
                .getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode,
                permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public void takeSuccess(TResult result) {
        ArrayList<TImage> images = result.getImages();
        ArrayList<ImagePic> imagePics = new ArrayList<>();
        if (images != null && images.size() > 0) {
            for (TImage image : images) {
                ImagePic p = new ImagePic();
                p.setCompressed(image.isCompressed());
                p.setCompressPath(image.getCompressPath());
                p.setCropped(image.isCropped());
                p.setFromType(image.getFromType() == TImage.FromType.CAMERA ?
                        ImagePic.FromType.CAMERA : ImagePic.FromType.OTHER);
                p.setOriginalPath(image.getOriginalPath());
                imagePics.add(p);
            }
            mMediaPickerDelegate.photoCallback(imagePics);
        } else {
            mMediaPickerDelegate.photoCallback(null);
        }
        finish();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Slog.d(TAG, "takeFail:" + msg);
        mMediaPickerDelegate.photoCallback(null);
        finish();
    }

    @Override
    public void takeCancel() {
        Slog.d(TAG, "takeCancel:");
        mMediaPickerDelegate.photoCallback(null);
        finish();
    }

}
