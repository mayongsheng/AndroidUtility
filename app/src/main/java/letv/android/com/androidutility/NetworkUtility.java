package letv.android.com.androidutility;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Pair;

/**
 * Created by mayongsheng on 16/1/9.
 */
public class NetworkUtility {
    /**
     * check if the current network is available
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        CheckParamUtility.exceptionOnParamNull(context, "context cant be null");

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }

        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }

        return false;
    }

    /**
     * get current networktype
     * the TYPE_NONE field in ConnectivityManager which value is -1,has been declared hide,
     * so when we get this method result,we use -1 for ConnectivityManager.TYPE_NONE.
     *
     * @param context
     * @return
     */
    private static Pair<Integer, String> getConnectedNetworkTypeName(Context context) {
        CheckParamUtility.exceptionOnParamNull(context, "context cant be null");

        int typeNone = -1;
        String typeNoneName = "unknow type";
        Pair<Integer, String> nonePair = new Pair<>(typeNone, typeNoneName);

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return nonePair;
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return nonePair;
        }

        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            Pair<Integer, String> pair = new Pair<>(networkInfo.getType(), networkInfo.getTypeName());
            return pair;
        }

        return nonePair;
    }

    public static boolean isNetworktWifiConnected(Context context) {
        CheckParamUtility.exceptionOnParamNull(context, "context cant be null");

        return getConnectedNetworkTypeName(context).first == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean is3GConnected(Context context) {
        CheckParamUtility.exceptionOnParamNull(context, "context cant be null");

        int networkType = getConnectedNetworkTypeName(context).first;
        boolean isMobileConnected = networkType == ConnectivityManager.TYPE_MOBILE;
        return isMobileConnected && (getNetworkClass(networkType) == NETWORK_CLASS_3_G);
    }

    public static boolean is3Gor4GConnected(Context context) {
        CheckParamUtility.exceptionOnParamNull(context, "context cant be null");

        int networkType = getConnectedNetworkTypeName(context).first;
        boolean isMobileConnected = networkType == ConnectivityManager.TYPE_MOBILE;
        return isMobileConnected && (getNetworkClass(networkType) == NETWORK_CLASS_3_G || getNetworkClass(networkType) == NETWORK_CLASS_4_G);
    }

    private static final String CTWAP = "ctwap";
    private static final String CTNET = "ctnet";
    private static final String CMWAP = "cmwap";
    private static final String CMNET = "cmnet";
    private static final String NET_3G = "3gnet";
    private static final String WAP_3G = "3gwap";
    private static final String UNIWAP = "uniwap";
    private static final String UNINET = "uninet";

    //电信
    public static final int TYPE_CT_WAP = 5;
    public static final int TYPE_CT_NET = 6;
    public static final int TYPE_CT_WAP_2G = 7;
    public static final int TYPE_CT_NET_2G = 8;
    //移动
    public static final int TYPE_CM_WAP = 9;
    public static final int TYPE_CM_NET = 10;
    public static final int TYPE_CM_WAP_2G = 11;
    public static final int TYPE_CM_NET_2G = 12;
    //联通
    public static final int TYPE_CU_WAP = 13;
    public static final int TYPE_CU_NET = 14;
    public static final int TYPE_CU_WAP_2G = 15;
    public static final int TYPE_CU_NET_2G = 16;

    public static final int TYPE_OTHER = 17;
    /**
     * 没有网络
     */
    public static final int TYPE_NET_WORK_DISABLED = -1;

    /**
     * wifi网络
     */
    public static final int TYPE_WIFI = 4;

    public static Uri PREFERRED_APN_URI = Uri
            .parse("content://telephony/carriers/preferapn");

    /***
     * 判断Network具体类型（联通移动wap，电信wap，其他net）
     */
    public static int checkNetworkType(Context context) {
        CheckParamUtility.exceptionOnParamNull(context, "context cant be null");

        try {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo mobNetInfoActivity = connectivityManager
                    .getActiveNetworkInfo();
            if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {
                // 注意一：
                // NetworkInfo 为空或者不可以用的时候正常情况应该是当前没有可用网络，
                // 但是有些电信机器，仍可以正常联网，
                // 所以当成net网络处理依然尝试连接网络。
                // （然后在socket中捕捉异常，进行二次判断与用户提示）。
                return TYPE_NET_WORK_DISABLED;
            } else {
                // NetworkInfo不为null开始判断是网络类型
                int netType = mobNetInfoActivity.getType();
                if (netType == ConnectivityManager.TYPE_WIFI) {
                    // wifi net处理
                    return TYPE_WIFI;
                } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                    // 注意二：
                    // 判断是否电信wap:
                    // 不要通过getExtraInfo获取接入点名称来判断类型，
                    // 因为通过目前电信多种机型测试发现接入点名称大都为#777或者null，
                    // 电信机器wap接入点中要比移动联通wap接入点多设置一个用户名和密码,
                    // 所以可以通过这个进行判断！
                    boolean is3G = getNetworkClass(netType) == NETWORK_CLASS_3_G;
                    final Cursor c = context.getContentResolver().query(
                            PREFERRED_APN_URI, null, null, null, null);
                    if (c != null) {
                        c.moveToFirst();
                        final String user = c.getString(c
                                .getColumnIndex("user"));
                        if (!TextUtils.isEmpty(user)) {
                            if (user.startsWith(CTWAP)) {
                                return is3G ? TYPE_CT_WAP : TYPE_CT_WAP_2G;
                            } else if (user.startsWith(CTNET)) {
                                return is3G ? TYPE_CT_NET : TYPE_CT_NET_2G;
                            }
                        }
                    }
                    c.close();
                    // 注意三：
                    // 判断是移动联通wap:
                    // 其实还有一种方法通过getString(c.getColumnIndex("proxy")获取代理ip
                    // 来判断接入点，10.0.0.172就是移动联通wap，10.0.0.200就是电信wap，但在
                    // 实际开发中并不是所有机器都能获取到接入点代理信息，例如魅族M9 （2.2）等...
                    // 所以采用getExtraInfo获取接入点名字进行判断
                    String netMode = mobNetInfoActivity.getExtraInfo();
                    if (netMode != null) {
                        // 通过apn名称判断是否是联通和移动wap
                        netMode = netMode.toLowerCase();
                        if (netMode.equals(CMWAP)) {
                            return is3G ? TYPE_CM_WAP : TYPE_CM_WAP_2G;
                        } else if (netMode.equals(CMNET)) {
                            return is3G ? TYPE_CM_NET : TYPE_CM_NET_2G;
                        } else if (netMode.equals(NET_3G)
                                || netMode.equals(UNINET)) {
                            return is3G ? TYPE_CU_NET : TYPE_CU_NET_2G;
                        } else if (netMode.equals(WAP_3G)
                                || netMode.equals(UNIWAP)) {
                            return is3G ? TYPE_CU_WAP : TYPE_CU_WAP_2G;
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return TYPE_OTHER;
        }

        return TYPE_OTHER;
    }

    //networktype that hide by the framework
    /**
     * Unknown network class.
     */
    private static final int NETWORK_CLASS_UNKNOWN = 0;
    /**
     * Class of broadly defined "2G" networks.
     */
    private static final int NETWORK_CLASS_2_G = 1;
    /**
     * Class of broadly defined "3G" networks.
     */
    private static final int NETWORK_CLASS_3_G = 2;
    /**
     * Class of broadly defined "4G" networks.
     */
    private static final int NETWORK_CLASS_4_G = 3;

    //detail networktype that hide by the framework
    private static final int NETWORK_TYPE_GSM = 16;
    private static final int NETWORK_TYPE_TD_SCDMA = 17;
    private static final int NETWORK_TYPE_IWLAN = 18;

    private static int getNetworkClass(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case NETWORK_TYPE_TD_SCDMA:
                return NETWORK_CLASS_3_G;
            case TelephonyManager.NETWORK_TYPE_LTE:
            case NETWORK_TYPE_IWLAN:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }
}
