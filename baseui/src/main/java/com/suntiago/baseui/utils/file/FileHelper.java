package com.suntiago.baseui.utils.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zy on 2018/12/20.
 */

public interface FileHelper {
    /**
     * 保存数据流到本地
     *
     * @param instream 数据流
     * @param outFile  File类
     * @return
     */
    boolean saveInputStreamToLocalWithFile(InputStream instream, File outFile);

    /**
     * 保存数据流到本地
     *
     * @param instream 数据流
     * @param fileName 文件名
     * @return
     */
    boolean saveInputStreamToLocalWithFileName(InputStream instream, String fileName);

    /**
     * 从resource的asset中读取文件数据
     *
     * @param context
     * @param fileName
     * @return
     */
    InputStream getAssetsStream(Context context, String fileName);


    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     */
    String getPath(final Context context, final Uri uri);


    /**
     * 保存图片到本地
     *
     * @param bitmap   Bitmap对象
     * @param fileName 文件名
     * @return
     */
    boolean saveBitmapAsPicToLocal(Bitmap bitmap, String fileName);

    /**
     * 保存字符串到本地
     *
     * @param content  字符串内容
     * @param fileName 文件名
     * @return
     */
    boolean saveStringToLocal(String content, String fileName) throws IOException;

    /**
     * 保存字节到本地
     *
     * @param bytes    字符串内容
     * @param fileName 文件名
     * @return
     */
    boolean saveByteToLocal(byte[] bytes, String fileName) throws IOException;


    /**
     * 读取文件, 返回字符串
     *
     * @param filePath
     * @return
     */
    String readFile(String filePath) throws IOException;


    /**
     * 获取图片下载目录文件
     *
     * @param context
     * @return
     */
    String getImageLoaderDirPath(Context context);


}
