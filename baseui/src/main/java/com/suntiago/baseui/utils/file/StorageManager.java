package com.suntiago.baseui.utils.file;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zy on 2018/12/20.
 */

public class StorageManager {

  /**
   * 目前 {@link com.suntiago.baseui.utils.log.Slog},
   * {@link com.suntiago.baseui.utils.log.CrashHandler},
   * {@link com.suntiago.baseui.activity.base.pickmedia.ShadowPickMediaActivity}
   * 用到了存储，需要修改。
   */
  static StorageHelper sStorageHelper;

  public static StorageHelper getStorageHelper() {
    if (sStorageHelper == null) {
      synchronized (StorageHelper.class) {
        if (sStorageHelper == null) {
          sStorageHelper = new StorageHelperIml();
        }
      }
    }
    return sStorageHelper;
  }

  static class StorageHelperIml implements StorageHelper {
    String mCompany = "";
    String mAppName = "";

    public StorageHelperIml() {
    }

    @Override
    public void initPath(String company, String appName) {
      mCompany = company;
      mAppName = appName;
    }

    @Override
    public boolean isSDCardEnable() {
      return Environment.getExternalStorageState().equals(
          Environment.MEDIA_MOUNTED);
    }


    @Override
    public String getStoragePath(Context context) {
      if (isSDCardEnable()) {
        return getSDCardPath();
      } else {
        return getPackagePath(context);
      }
    }

    @Override
    public String getSDCardPath() {
      return Environment.getExternalStorageDirectory().getAbsolutePath()
          + File.separator + mCompany + File.separator + mAppName + File.separator;
    }

    @Override
    public String getPackagePath(Context context) {
      return context.getFilesDir().getPath();
    }

    @Override
    public String getFilePath(String subdirectory, String fileName) {
      StringBuilder path = new StringBuilder(getSDCardPath());
      if (!TextUtils.isEmpty(fileName)) {
        if (!TextUtils.isEmpty(subdirectory)) {
          path.append(File.separator);
          path.append(subdirectory);
        }
        File file = new File(path.toString());
        if (!file.exists()) {
          file.mkdirs();
        }
        path.append(File.separator);
        path.append(fileName);
      }
      return path.toString();
    }

    @Override
    public void clearAllCache(Context context) {
      clearDir(context.getCacheDir());
      if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        clearDir(context.getExternalCacheDir());
        clearDir(new File(getSDCardPath()));
      }
    }

    /**
     * 删除指定目录文件
     *
     * @param dir
     * @return
     */
    private boolean clearDir(File dir) {
      if (dir == null) return false;
      if (dir.isDirectory()) {
        String[] children = dir.list();
        for (String aChildren : children) {
          boolean success = clearDir(new File(dir, aChildren));
          if (!success) {
            return false;
          }
        }
      }
      return dir.delete();
    }

    @Override
    public void clearDir(Context context, String subDirectory) {
      clearDir(new File(getSDCardPath(), subDirectory));
    }

    @Override
    public int checkAndDelLowPriority(String subdirectory, int maxSize) {
      List<File> files = getFileSort(subdirectory);
      int deleteCounts = 0;
      if (files != null) {
        for (int i = files.size() - 1; i >= maxSize; i--) {
          if (files.get(i).exists() && files.get(i).delete()) {
            deleteCounts++;
          }
        }
      }
      return deleteCounts;
    }

    /**
     * 获取目录下所有文件(按时间倒序排序)
     *
     * @return
     */
    private List<File> getFileSort(String subdirectory) {
      String savePath = getSavePath(subdirectory);
      File save = new File(savePath);
      if (!save.exists()) {
        return null;
      }
      List<File> list = getFiles(savePath, new ArrayList<File>());
      if (list != null && list.size() > 0) {
        Collections.sort(list, new Comparator<File>() {
          @Override
          public int compare(File file, File newFile) {
            if (file.lastModified() > newFile.lastModified()) {
              return -1;
            } else if (file.lastModified() == newFile.lastModified()) {
              return 0;
            } else {
              return 1;
            }
          }
        });
      }
      return list;
    }

    private String getSavePath(String subdirectory) {
      return new File(getSDCardPath(), subdirectory).getAbsolutePath();
    }

    /**
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public List<File> getFiles(String realpath, List<File> files) {
      File realFile = new File(realpath);
      if (realFile.isDirectory()) {
        File[] subfiles = realFile.listFiles();
        if (subfiles != null) {
          for (File file : subfiles) {
            if (file.isDirectory()) {
              getFiles(file.getAbsolutePath(), files);
            } else {
              files.add(file);
            }
          }
        }
      }
      return files;
    }

    @Override
    public int checkAndDelOutOfDate(String subdirectory, long validity) {
      List<File> files = getFileSort(subdirectory);
      int deleteCounts = 0;
      if (files != null) {
        for (int i = 0; i < files.size(); i++) {
          if (files.get(i).exists()) {
            long lastModefied = files.get(i).lastModified();
            if ((System.currentTimeMillis() - lastModefied) > validity
                && files.get(i).delete()) {
              deleteCounts++;
            }
          }
        }
      }
      return deleteCounts;
    }

    @Override
    public long getSDCardAllSize() {
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

    @Override
    public long getFreeBytes(String filePath) {
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
  }


  /**
   * 下面这几个方法应该归类为 datedFormat
   * 暂时未归类处理，先放在这里*/

  /**
   * 从resource的raw中读取文件数据
   *
   * @param context
   * @param resId
   * @return
   */
  InputStream getRawStream(Context context, int resId) throws Exception {
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
  InputStream Byte2InputStream(byte[] b) {
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
  public String inputSteamToString(InputStream instream) throws IOException {
    String result = null;
    try {
      byte bytes[] = InputStream2Bytes(instream);
      result = new String(bytes, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return result;
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
