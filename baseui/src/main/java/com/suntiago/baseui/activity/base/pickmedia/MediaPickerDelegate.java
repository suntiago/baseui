package com.suntiago.baseui.activity.base.pickmedia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import static android.media.ThumbnailUtils.OPTIONS_RECYCLE_INPUT;

/**
 * Created by zy on 2018/12/14.
 * 选择图片的部分使用 takephoto 库
 */

public class MediaPickerDelegate implements IMediaPicker {


    ConfigPicturePick mConfigPicturePick = new ConfigPicturePick();
    ConfigVideoPick mConfigVideoPick = new ConfigVideoPick();

    private static MediaPickerDelegate sMediaPickerDelegate;
    private Context mContext;

    public static IMediaPicker get() {
        if (sMediaPickerDelegate == null) {
            synchronized (MediaPickerDelegate.class) {
                if (sMediaPickerDelegate == null) {
                    sMediaPickerDelegate = new MediaPickerDelegate();
                }
            }
        }
        return sMediaPickerDelegate;
    }

    protected static MediaPickerDelegate getMediaPicker() {
        if (sMediaPickerDelegate == null) {
            synchronized (MediaPickerDelegate.class) {
                if (sMediaPickerDelegate == null) {
                    sMediaPickerDelegate = new MediaPickerDelegate();
                }
            }
        }
        return sMediaPickerDelegate;
    }

    public MediaPickerDelegate() {
    }

    public ConfigPicturePick getConfigPicturePick() {
        return mConfigPicturePick;
    }

    public ConfigVideoPick getConfigVideoPick() {
        return mConfigVideoPick;
    }

    @Override
    public IMediaPicker configPickVideo(int quality, int durationLimit) {
        if (quality > 1) {
            quality = 1;
        } else if (quality < 0) {
            quality = 0;
        }
        if (durationLimit <= 0) {
            durationLimit = 10;
        }

        mConfigVideoPick.durationLimit = durationLimit;
        mConfigVideoPick.quality = quality;
        return this;
    }

    @Override
    public IMediaPicker configPickPicture(int maxPixel, int maxSize, int maxSizeW, int macSizeH, boolean enablePixelCompress, boolean enableQualityCompress, boolean enableReserveRaw) {
        return null;
    }

    @Override
    public void pickPictures(int amountLimit, PPsCallback ppsCallback) {

    }

    @Override
    public void pickPicture(PP1Callback pp1Callback) {

    }

    @Override
    public void pickPictureAndCut(int cutSizeW, int cutSizeH, PP1Callback pp1Callback) {

    }

    @Override
    public void pickPictures(boolean fromGallery, int amountLimit, PPsCallback ppsCallback) {

    }

    @Override
    public void pickPicture(boolean fromGallery, PP1Callback pp1Callback) {

    }

    @Override
    public void pickPictureAndCut(boolean fromGallery, int cutSizeW, int cutSizeH, PP1Callback pp1Callback) {

    }

    @Override
    public void cutPicture(String originPicturePath, int cutSizeW, int cutSizeH, PP1Callback pp1Callback) {

    }

    PMVCallback mPMVCallback;

    public void videoCallback(String videoPath) {
        if (mPMVCallback != null) {
            mPMVCallback.pickMV(videoPath);
            mPMVCallback = null;
        }
    }

    @Override
    public void takeVideo(Context context, PMVCallback pmvCallback) {
        mPMVCallback = pmvCallback;
        Intent intent = new Intent(context, ShadowPickMediaActivity.class);
        intent.putExtra("action", "video");
        intent.putExtra("type", "camera");
        context.startActivity(intent);
    }

    @Override
    public void pickVideo(Context context, PMVCallback pmvCallback) {
        mPMVCallback = pmvCallback;
        Intent intent = new Intent(context, ShadowPickMediaActivity.class);
        intent.putExtra("action", "video");
        intent.putExtra("type", "local");
        context.startActivity(intent);
    }

    @Override
    public Bitmap createVideoThumbnail(String filePath, int kind) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            int i = -1;
            //有些情况下会出现获取不到第一帧的情况
            while (bitmap == null && i < 5) {
                bitmap = retriever.getFrameAtTime(i++);
            }
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (bitmap == null) return null;

        if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {
            // Scale down the bitmap if it's too large.
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int max = Math.max(width, height);
            if (max > 512) {
                float scale = 512f / max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }
        } else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 200, 200, OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }
}
