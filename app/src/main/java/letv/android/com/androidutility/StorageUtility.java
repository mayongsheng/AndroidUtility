package letv.android.com.androidutility;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by mayongsheng on 16/1/10.
 */
public class StorageUtility {
    static final int ERROR = -1;

    /**
     * 外部存储是否可用
     *
     * @return
     */
    public static boolean externalStorageAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机内部可用空间大小
     *
     * @return
     */
    public static long getAvailableInternalStorageSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部空间大小
     *
     * @return
     */
    public static long getTotalInternalStorageSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取手机外部可用空间大小
     *
     * @return
     */
    public static long getAvailableExternalStorageSize() {
        if (externalStorageAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    /**
     * 获取手机外部空间大小
     *
     * @return
     */
    public static long getTotalExternalStorageSize() {
        if (externalStorageAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KiB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MiB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
}
