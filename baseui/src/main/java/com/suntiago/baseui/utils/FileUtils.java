/*
    ShengDao Android Client, FileUtils
    Copyright (c) 2014 ShengDao Tech Company Limited
 */

package com.suntiago.baseui.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * [文件处理工具类]
 * 需要文件读取权限
 *
 * @author devin.hu
 * @version 1.0
 * @date 2014-2-25
 **/
@SuppressWarnings("ALL")
public class FileUtils {

    private static final String tag = FileUtils.class.getSimpleName();

    private static FileUtils instance;
    /**
     * 缓存路径
     **/
    private String rootPath;

    private FileUtils(String rootPath) {
        this.rootPath = rootPath;
        if (TextUtils.isEmpty(rootPath)) {
            throw new IllegalArgumentException("FileUtils rootPath is not null.");
        }
    }

    /**
     * 获取FileUtils实例，单例模式实现
     * 该方法缓存路径为SD卡
     *
     * @return
     */
    public static FileUtils getInstance(Context context) {
        if (isSDCardEnable()) {
            String sdCardPath = getSDCardPath();
            return getInstance(sdCardPath);
        }
        String filePath = getPackagePath(context);
        return getInstance(filePath);
    }

    /**
     * 判断SD卡是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡的路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 获取当前包的缓存路径为/data/data/cn.xxx.xxx(当前包)/files
     *
     * @param context
     * @return
     */
    public static String getPackagePath(Context context) {
        return context.getFilesDir().getPath();
    }

    /**
     * 获取FileUtils实例，单例模式实现
     * 该方法缓存路径为设置的rootPath
     *
     * @param rootPath
     * @return
     */
    public static FileUtils getInstance(String rootPath) {
        if (instance == null) {
            synchronized (FileUtils.class) {
                if (instance == null) {
                    instance = new FileUtils(rootPath);
                }
            }
        }
        return instance;
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return
     */
    public static long getSDCardAllSize() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取长时间保存的数据的目录
     *
     * @param context
     * @return
     */
    public static File getExternalFileDir(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //SD卡本包下的files文件路径
            return context.getExternalFilesDir(null);
        } else {
            // 内存本包下的files文件路径
            return context.getFilesDir();
        }
    }

    /**
     * 保存数据流到本地
     *
     * @param instream 数据流
     * @param outFile  File类
     * @return
     */
    public static boolean saveInputStreamToLocalWithFile(InputStream instream, File outFile) {
        FileOutputStream buffer = null;
        try {
            if (instream != null) {
                buffer = new FileOutputStream(outFile);
                byte[] tmp = new byte[1024];
                int length = 0;
                while ((length = instream.read(tmp)) != -1) {
                    buffer.write(tmp, 0, length);
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (instream != null) instream.close();
                if (buffer != null) {
                    buffer.flush();
                    buffer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 从resource的asset中读取文件数据
     *
     * @param context
     * @param fileName
     * @return
     */
    public static InputStream getAssetsStream(Context context, String fileName) {
        try {
            return context.getResources().getAssets().open(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取保存到本地的资源路径
     *
     * @param fileName 文件名
     * @return
     */
    public String getFilePath(String fileName) {
        StringBuilder path = new StringBuilder(rootPath);
        if (!TextUtils.isEmpty(fileName)) {
            File file = new File(path.toString());
            if (!file.exists()) {
                file.mkdirs();
            }
            path.append(File.separator);
            path.append(fileName);
        }
        return path.toString();
    }

    /**
     * 判断文件是否存在， true表示存在，false表示
     *
     * @param fileName 文件名
     * @return
     */
    public boolean isFileExits(String fileName) {
        File file = new File(getFilePath(fileName));
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 保存图片到本地
     *
     * @param bitmap   Bitmap对象
     * @param fileName 文件名
     * @return
     */
    public boolean saveBitmapAsPicToLocal(Bitmap bitmap, String fileName) {
        if (bitmap == null) return false;
        OutputStream output = null;
        try {
            File file = new File(getFilePath(fileName));
            if (file.exists()) {
                return true;
            } else {
                if (file.createNewFile()) {
                    output = new FileOutputStream(file);
                    CompressFormat format = CompressFormat.PNG;
                    String tempFileName = fileName.toLowerCase(Locale.getDefault());
                    if (".jpg".endsWith(tempFileName)) {
                        format = CompressFormat.JPEG;
                    } else if (".png".endsWith(tempFileName)) {
                        format = CompressFormat.PNG;
                    }
                    bitmap.compress(format, 100, output);
                    output.flush();
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 保存字符串到本地
     *
     * @param content  字符串内容
     * @param fileName 文件名
     * @return
     */
    public boolean saveStringToLocal(String content, String fileName) throws IOException {
        if (!TextUtils.isEmpty(content)) {
            return saveByteToLocal(content.getBytes(), fileName);
        }
        return false;
    }

    /**
     * 保存字节到本地
     *
     * @param bytes    字符串内容
     * @param fileName 文件名
     * @return
     */
    public boolean saveByteToLocal(byte[] bytes, String fileName) throws IOException {
        FileOutputStream output = null;
        try {
            if (bytes != null) {
                File file = new File(rootPath, fileName);
                output = new FileOutputStream(file);
                output.write(bytes);
                output.flush();
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (output != null) output.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    /**
     * 保存数据流到本地
     *
     * @param instream 数据流
     * @param fileName 文件名
     * @return
     */
    public boolean saveInputStreamToLocalWithFileName(InputStream instream, String fileName) {
        File file = new File(rootPath, fileName);
        return saveInputStreamToLocalWithFile(instream, file);
    }

    /**
     * 读取文件
     *
     * @param filePath
     * @return
     */
    public String readFile(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return sb.toString();
    }

    /**
     * 从resource的raw中读取文件数据
     *
     * @param context
     * @param resId
     * @return
     */
    public InputStream getRawStream(Context context, int resId) throws Exception {
        try {
            return context.getResources().openRawResource(resId);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 将byte[]转换成InputStream
     *
     * @param b
     * @return
     */
    public InputStream Byte2InputStream(byte[] b) {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        return bais;
    }

    /**
     * InputStream转换成byte[]
     *
     * @param instream
     * @return
     */
    public byte[] InputStream2Bytes(InputStream instream) throws IOException {
        byte bytes[] = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            byte[] tmp = new byte[1024];
            int length = 0;
            while ((length = instream.read(tmp)) != -1) {
                output.write(tmp, 0, length);
            }
            bytes = output.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (instream != null) instream.close();
                if (output != null) output.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return bytes;
    }

    /**
     * 数据流转字符串
     *
     * @param instream
     * @return
     * @throws IOException
     */
    public String inputSteamToString(InputStream instream)  throws IOException{
        String result = null;
        try {
            byte bytes[] = InputStream2Bytes(instream);
            result = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    //利用反射来获取存储器列表
    public static class StorageList {
        private Context mContext;
        private StorageManager mStorageManager;
        private Method mMethodGetPaths;

        public StorageList(Context context) {
            mContext = context;
            if (mContext != null) {
                //getMethod("getVolumePaths")返回StorageManager类对应的Class对象的getVolumePaths方法.
                mStorageManager = (StorageManager) mContext
                        .getSystemService(Activity.STORAGE_SERVICE);
                try {
                    mMethodGetPaths = mStorageManager.getClass()
                            .getMethod("getVolumePaths");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 获取存储器列表
         *
         * @return
         */
        public String[] getVolumePaths() {
            String[] paths = null;
            try {
                //调用该方法
                paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return paths;
        }
    }

    /**
     * 清除所有的缓存文件
     * <p>
     * 第三方框架的需要单独处理， 例如：图片请求框架里面的缓存等。
     *
     * @param context
     */
    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
            deleteDir(new File(getImageLoaderDirPath(context)));
        }
    }

    /**
     * 删除指定目录文件
     *
     * @param dir
     * @return
     */
    private static boolean deleteDir(File dir) {
        if (dir == null) return false;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * 获取图片下载目录文件
     *
     * @param context
     * @return
     */
    public static String getImageLoaderDirPath(Context context) {
        File file = getExternalFileDir(context);
        if (file == null) {
            return "/mnt/sdcard/.imageloader/";
        } else {
            return file.getAbsolutePath() + '/'
                    + ".imageloader" + '/';
        }
    }


    public static String formatLength(long size) {
        long cacheSize = size;
        if (cacheSize <= 0) {
            return "0K";
        } else {
            float cacheSizeTemp1 = changFloatToTwoDecimal(Math.round(cacheSize / 1024));
            float cacheSizeTemp2 = changFloatToTwoDecimal(Math.round((cacheSize / 1024) / 1024));
            if (cacheSizeTemp1 < 1) {
                return cacheSize + "B";
            } else if (((cacheSizeTemp1 >= 1) && (cacheSizeTemp2 < 1))) {
                return cacheSizeTemp1 + "K";
            } else if (cacheSizeTemp2 >= 1) {
                return cacheSizeTemp2 + "M";
            } else {
                return "0K";
            }
        }
    }

    /**
     * 使Float保留两位小数
     *
     * @param in
     * @return
     */
    private static float changFloatToTwoDecimal(float in) {
        DecimalFormat df = new DecimalFormat("0.00");
        String out = df.format(in);
        float result = Float.parseFloat(out);
        return result;
    }
}
