package com.suntiago.baseui.activity.base.pickmedia;

/**
 * Created by zy on 2018/12/14.
 */

public class ConfigPicturePick {
    private int FILE_PIXEL_MAX_IMAGE_DEFAULT = 1080;
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
     * @param enableReserveRaw      否保留原文件       true
     * @return
     * @throws
     */

    public int maxPixel;
    public int maxSize;
    public int maxSizeW;
    public int maxSizeH;
    public boolean enablePixelCompress = false;
    public boolean enableQualityCompress = false;
    public boolean enableReserveRaw = true;

    //是否使用自带工具裁剪
    public boolean crop = false;
    //宽高比例， X
    public int aspectX = 0;
    //宽高比例, Y
    public int aspectY = 0;
    public int outputX = 0;
    public int outputY = 0;

    public ConfigPicturePick() {
    }

    public void reset() {
        maxPixel = FILE_PIXEL_MAX_IMAGE_DEFAULT;
        maxSize = 5 * 1024 * 1024;
        maxSizeW = 1080;
        maxSizeH = 1920;
        enablePixelCompress = false;
        enableQualityCompress = false;
        enableReserveRaw = true;

        //是否使用自带工具裁剪
       crop = false;
        //宽高比例， X
        aspectX = 1;
        //宽高比例, Y
        aspectY = 1;
        outputX = 0;
        outputY = 0;
    }
}
