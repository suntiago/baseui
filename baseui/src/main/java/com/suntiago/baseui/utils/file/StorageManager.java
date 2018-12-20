package com.suntiago.baseui.utils.file;

import android.content.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

/**
 * Created by zy on 2018/12/20.
 */

public class StorageManager {

    /**
     * 目前 {@link com.suntiago.baseui.utils.log.Slog},
     *     {@link com.suntiago.baseui.utils.log.CrashHandler},
     *     {@link com.suntiago.baseui.activity.base.pickmedia.ShadowPickMediaActivity}
     *     用到了存储，需要修改。
     *
     * */
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
            return false;
        }

        @Override
        public String getStoragePath() {
            return null;
        }

        @Override
        public String getSDCardPath() {
            return null;
        }

        @Override
        public String getPackagePath(Context context) {
            return null;
        }

        @Override
        public String getFilePath(String subdirectory, String fileName) {
            return null;
        }

        @Override
        public void clearAllCache(Context context) {

        }

        @Override
        public void clearDir(Context context, String subDirectory) {

        }

        @Override
        public boolean deleteDir(Context context, String subDirectory) {
            return false;
        }

        @Override
        public int checkAndDelLowPriority(String subdirectory, int maxSize, long maxBytes) {
            return 0;
        }

        @Override
        public int checkAndDelOutOfDate(String subdirectory, long validity) {
            return 0;
        }

        @Override
        public long getSDCardAllSize() {
            return 0;
        }

        @Override
        public long getFreeBytes(String filePath) {
            return 0;
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
