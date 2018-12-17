package com.suntiago.baseui.activity.base.pickmedia;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by zy on 2018/12/14.
 * 选择图片，视频
 */

public interface IMediaPicker {

    /**
     * 选择多张图片时候回调
     */
    interface PPsCallback {
        void pickPic(ArrayList<ImagePic> imagePics);
    }

    /**
     * 选择1张图片时候回调
     */
    interface PP1Callback {
        void pickPic(ImagePic imagePics);
    }

    /**
     * 选择1张图片时候回调
     */
    interface PMVCallback {
        void pickMV(String videoPath);
    }

    /**
     * 配置图片选择的参数
     * <p>
     *
     * @param maxPixel              长或宽不超过的最大像素,单位px
     * @param maxSize               压缩到的最大大小，单位B
     * @param maxSizeW              压缩到的最大宽度像素，单位px
     * @param macSizeH              压缩到的最大高度像素，单位px
     * @param enablePixelCompress   是否启用像素压缩   false
     * @param enableQualityCompress 是否启用质量压缩   false
     * @param enableReserveRaw      否保留原文件       false
     * @return
     * @throws
     */
    IMediaPicker configPickPicture(int maxPixel,
                                   int maxSize,
                                   int maxSizeW,
                                   int macSizeH,
                                   boolean enablePixelCompress,
                                   boolean enableQualityCompress,
                                   boolean enableReserveRaw);

    IMediaPicker configPickPictureCrop(boolean withOwnCrop, int aspectX, int aspectY, int outputX, int outputY);

    /**
     * 配置视频选择的参数
     * <p>
     *
     * @param quality       视频品质 0~1
     * @param durationLimit 视频长度 >0
     * @return
     * @throws
     */
    IMediaPicker configPickVideo(int quality, int durationLimit);

    /**
     * 选择图片，未知的图库，还是拍摄，需要提供选择界面
     * <p>
     *
     * @param amountLimit 数量大小的限制
     * @return
     * @throws
     */
    void pickPictures(Context context, int amountLimit, PPsCallback ppsCallback);

    /**
     * 选择图片，未知的图库，还是拍摄，需要提供选择界面
     * <p>
     *
     * @return
     * @throws
     */
    void pickPicture(Context context, PP1Callback pp1Callback);

    /**
     * 选择图片，未知的图库，还是拍摄，需要提供选择界面，
     * 并需要裁剪
     * <p>
     *
     * @param cutSizeW 裁剪尺寸宽度
     * @param cutSizeH 裁剪尺寸高度
     * @return
     * @throws
     */
    void pickPictureAndCut(Context context,int aspectX, int aspectY, int cutSizeW, int cutSizeH, PP1Callback pp1Callback);

    /**
     * 选择图片，未知的图库，还是拍摄，需要提供选择界面
     * <p>
     *
     * @param fromGallery 是否从相册选择，否则拍照
     * @param amountLimit 数量大小的限制
     * @return
     * @throws
     */
    void pickPictures(Context context,boolean fromGallery, int amountLimit, PPsCallback ppsCallback);

    /**
     * 选择图片，未知的图库，还是拍摄，需要提供选择界面
     * <p>
     *
     * @param fromGallery 是否从相册选择，否则拍照
     * @return
     * @throws
     */
    void pickPicture(Context context, boolean fromGallery, PP1Callback pp1Callback);

    /**
     * 选择图片，未知的图库，还是拍摄，需要提供选择界面，
     * 并需要裁剪
     * <p>
     *
     * @param fromGallery 是否从相册选择，否则拍照
     * @param cutSizeW    裁剪尺寸宽度
     * @param cutSizeH    裁剪尺寸高度
     * @return
     * @throws
     */
    void pickPictureAndCut(Context context,int aspectX, int aspectY, boolean fromGallery, int cutSizeW, int cutSizeH, PP1Callback pp1Callback);

    /**
     * 指定图片裁剪
     * <p>
     *
     * @param cutSizeW 裁剪尺寸宽度
     * @param cutSizeH 裁剪尺寸高度
     * @return
     * @throws
     */
    void cutPicture(Context context, String originPicturePath, int cutSizeW, int cutSizeH, PP1Callback pp1Callback);

    /**
     * 拍摄视频
     * <p>
     *
     * @param pmvCallback
     * @return
     * @throws
     */
    void takeVideo(Context context, PMVCallback pmvCallback);

    /**
     * 从相册获取视频
     * <p>
     *
     * @param pmvCallback
     * @return
     * @throws
     */
    void pickVideo(Context context, PMVCallback pmvCallback);

    /**
     * 获取视频缩略图
     * <p>
     *
     * @param filePath
     * @param kind
     * @return
     * @throws
     */
    Bitmap createVideoThumbnail(String filePath, int kind);
}
