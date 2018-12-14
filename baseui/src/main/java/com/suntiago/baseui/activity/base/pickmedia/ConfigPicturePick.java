package com.suntiago.baseui.activity.base.pickmedia;

/**
 * Created by zy on 2018/12/14.
 */

public class ConfigPicturePick {
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
    public int macSizeH;
    public boolean enablePixelCompress = false;
    public boolean enableQualityCompress = false;
    public boolean enableReserveRaw = true;

    public ConfigPicturePick() {
    }
}
