package letv.android.com.androidutility;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * Created by mayongsheng on 16/1/10.
 */
public class ApplicationUtility {

    public static int getAppVersionCode(Context context) {
        CheckParamUtility.exceptionOnParamNull(context, "context cant be null");

        int code;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            code = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            code = 0;
        }
        return code;
    }

    public static String getAppVersionName(Context context) {
        CheckParamUtility.exceptionOnParamNull(context, "context cant be null");

        String packageName;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            packageName = packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            packageName = "";
        }
        return packageName;
    }

    /**
     * compare version code like xxx.x.xx pattern
     *
     * @param oldVersion
     * @param newVersion
     * @return
     */
    public static int compareVersionCode(String oldVersion, String newVersion) {
        if (TextUtils.isEmpty(oldVersion) || TextUtils.isEmpty(newVersion)) {
            throw new IllegalArgumentException("oldVersion and newVersion cant be empty!");
        }

        if (isStringValidVersionPattern(oldVersion) || isStringValidVersionPattern(newVersion)) {
            throw new IllegalArgumentException("oldVersion and newVersion must be start with digit and only contains digit or dot");
        }

        int[] oldSplits = convertDigitStringToDigitInt(oldVersion.split("\\."));
        int[] newSplits = convertDigitStringToDigitInt(newVersion.split("\\."));
        final int oLength = oldSplits.length;
        final int nLength = newSplits.length;
        final int minLength = oLength >= nLength ? oLength : nLength;
        int cursor = 0;
        do {
            if (cursor < oLength && cursor < nLength) {
                if (newSplits[cursor] > oldSplits[cursor]) {
                    return 1;
                } else if (newSplits[cursor] < oldSplits[cursor]) {
                    return -1;
                }
            }
            cursor++;
        } while (cursor < minLength);

        return nLength == oLength ? 0 : (nLength > oLength ? 1 : -1);
    }

    private static boolean isStringValidVersionPattern(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }

        char[] chararray = value.toCharArray();
        if (chararray[0] == '.') {
            return false;
        }

        final int length = chararray.length;
        for (int index = 0; index < length; index++) {
            if (!(Character.isDigit(chararray[index]) || chararray[index] != '.')) {
                return false;
            }
        }
        return true;
    }

    private static int[] convertDigitStringToDigitInt(String[] target) {
        int[] result = new int[target.length];
        for (int index = 0; index < target.length; index++) {
            result[index] = TextUtils.isEmpty(target[index]) ? 0 : Integer.valueOf(target[index]);
        }
        return result;
    }
}
