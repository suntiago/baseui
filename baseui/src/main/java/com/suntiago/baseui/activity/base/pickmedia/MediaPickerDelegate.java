package com.suntiago.baseui.activity.base.pickmedia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.suntiago.baseui.utils.log.Slog;

import java.util.ArrayList;

import static android.media.ThumbnailUtils.OPTIONS_RECYCLE_INPUT;

/**
 * Created by zy on 2018/12/14.
 * 选择图片的部分使用 takephoto 库
 */

public class MediaPickerDelegate implements IMediaPicker {
    private final String TAG = getClass().getSimpleName();
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
    public IMediaPicker configPickPicture(int maxPixel, int maxSize, int maxSizeW, int maxSizeH, boolean enablePixelCompress, boolean enableQualityCompress, boolean enableReserveRaw) {
        mConfigPicturePick.maxPixel = maxPixel;
        mConfigPicturePick.maxSize = maxSize;
        mConfigPicturePick.maxSizeW = maxSizeW;
        mConfigPicturePick.maxSizeH = maxSizeH;
        mConfigPicturePick.enablePixelCompress = enablePixelCompress;
        mConfigPicturePick.enableQualityCompress = enableQualityCompress;
        mConfigPicturePick.enableReserveRaw = enableReserveRaw;
        return this;
    }

    @Override
    public IMediaPicker configPickPictureCrop(boolean withOwnCrop, int aspectX, int aspectY, int outputX, int outputY) {
        mConfigPicturePick.crop = withOwnCrop;
        if (withOwnCrop) {
            if (aspectX <= 0) {
                aspectX = 1;
            } else {
                mConfigPicturePick.aspectX = aspectX;
            }
            if (aspectY <= 0) {
                aspectY = 1;
            } else {
                mConfigPicturePick.aspectY = aspectY;
            }
            if (outputX <= 0) {
                mConfigPicturePick.outputX = 1080;
            } else {
                mConfigPicturePick.outputX = outputX;
            }
            if (outputY <= 0) {
                mConfigPicturePick.outputY = 1920;
            } else {
                mConfigPicturePick.outputY = outputY;
            }
        }
        return this;
    }

    private PPsCallback mPpsCallback = null;

    @Override
    public void pickPictures(Context context, int amountLimit, PPsCallback ppsCallback) {
        pickPicture(context, true, ppsCallback, amountLimit, (PP1Callback) null);
    }

    @Override
    public void pickPicture(Context context, PP1Callback pp1Callback) {
        pickPicture(context, true, null, 1, pp1Callback);
    }

    @Override
    public void pickPictureAndCut(Context context
            , int aspectX, int aspectY, int cutSizeW, int cutSizeH, PP1Callback pp1Callback) {
        configPickPictureCrop(true, aspectX, aspectY, cutSizeW, cutSizeH);
        pickPicture(context, true, null, 1, pp1Callback);
    }

    @Override
    public void pickPictures(Context context, boolean fromGallery, int amountLimit, PPsCallback ppsCallback) {
        pickPicture(context, fromGallery, ppsCallback, amountLimit, null);
    }

    @Override
    public void pickPicture(Context context, boolean fromGallery, PP1Callback pp1Callback) {
        pickPicture(context, fromGallery, null, 1, pp1Callback);
    }

    @Override
    public void pickPictureAndCut(Context context, int aspectX, int aspectY,
                                  boolean fromGallery, int cutSizeW, int cutSizeH, PP1Callback pp1Callback) {
        configPickPictureCrop(true, aspectX, aspectY, cutSizeW, cutSizeH);
        pickPicture(context, true, null, 1, pp1Callback);
    }

    @Override
    public void cutPicture(Context context, String originPicturePath, int cutSizeW, int cutSizeH, PP1Callback pp1Callback) {

    }

    PP1Callback mPp1Callback;

    private void pickPicture(Context context, boolean fromGallery,
                             PPsCallback ppsCallback, int amountLimit, PP1Callback pp1Callback) {
        mPpsCallback = ppsCallback;
        mPp1Callback = pp1Callback;
        Intent intent = new Intent(context, ShadowPickMediaActivity.class);
        intent.putExtra("action", "photo");
        intent.putExtra("type", fromGallery ? "local" : "camera");
        intent.putExtra("amountLimit", amountLimit);
        context.startActivity(intent);
    }

    PMVCallback mPMVCallback;

    public void videoCallback(String videoPath) {
        if (mPMVCallback != null) {
            mPMVCallback.pickMV(videoPath);
            mPMVCallback = null;
            mConfigVideoPick.reset();
        }
    }

    public void photoCallback(ArrayList<ImagePic> imagePics) {
        if (imagePics != null && imagePics.size() > 0) {
            if (mPp1Callback != null) {
                Slog.d(TAG, "photoCallback  [imagePics]:"+ "picked  1");
                mPp1Callback.pickPic(imagePics.get(0));
                mPp1Callback = null;
                mConfigPicturePick.reset();
            } else if (mPpsCallback != null) {
                Slog.d(TAG, "photoCallback  [imagePics]:"+ "picked "+ imagePics.size());
                mPpsCallback.pickPic(imagePics);
                mPpsCallback = null;
                mConfigPicturePick.reset();
            }
        }else {
            Slog.d(TAG, "photoCallback  [imagePics]:"+ "pick null");
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
