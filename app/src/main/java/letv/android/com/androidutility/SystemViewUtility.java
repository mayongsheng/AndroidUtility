package letv.android.com.androidutility;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by mayongsheng on 16/1/10.
 * this class is used to open system`view,for example:
 * network setting,gps settings and so on
 */
public class SystemViewUtility {

    /**
     * open network setting
     */
    public static void openWirelessSettings(Context context) {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction(Intent.ACTION_VIEW);
        }
        context.startActivity(intent);
    }
}
