package com.suntiago.baseui.utils.file;

import android.content.Context;

/**
 * Created by zy on 2018/12/20.
 */

public interface StorageHelper {

    /**
     * 初始话存储路径
     */
    void initPath(String company, String appName);

    /**
     * 判断SD卡是否可用
     *
     * @return
     */
    boolean isSDCardEnable();

    /**
     * 获取存储路径，优先返回sdcard路径
     */
    String getStoragePath();

    /**
     * 获取SD卡的路径
     *
     * @return
     */
    String getSDCardPath();

    /**
     * 获取当前包的缓存路径为/data/data/cn.xxx.xxx(当前包)/files
     *
     * @param context
     * @return
     */
    String getPackagePath(Context context);

    /**
     * 获取保存到本地SD Card 的存储路径
     *
     * @param subdirectory 子目录
     * @param fileName     文件名
     * @return
     */
    String getFilePath(String subdirectory, String fileName);

    /**
     * 清除所有的缓存文件
     * <p>
     * 第三方框架的需要单独处理， 例如：图片请求框架里面的缓存等。
     *
     * @param context
     */
    void clearAllCache(Context context);

    /**
     * 清楚指定目录下的所有文件， 不会删除当前文件夹
     *
     * @param context
     * @param subDirectory 指定目录
     */
    void clearDir(Context context, String subDirectory);

    /**
     * 删除指定目录文件,包括文件夹本身
     *
     * @param context
     * @param subDirectory not null
     * @return
     */
    boolean deleteDir(Context context, String subDirectory);

    /**
     * 检查目录下文件数量，超过阈值则删除低优先级的数据
     * 默认优先级按时间先后排序
     *
     * @param subdirectory 目录，相对getSDCardPath（）下的子目录
     * @param maxSize      最大文件个数
     * @param maxBytes     存储数据占用最大内存空间
     * @return 返回删除文件的个数
     */
    int checkAndDelLowPriority(String subdirectory, int maxSize, long maxBytes);

    /**
     * 检查目录下过期的数据
     *
     * @param subdirectory 目录，相对getSDCardPath（）下的子目录
     * @param validity     有效期，ms
     * @return 返回删除文件的个数
     */
    int checkAndDelOutOfDate(String subdirectory, long validity);

    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return
     */
    long getSDCardAllSize();

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    long getFreeBytes(String filePath);

}
