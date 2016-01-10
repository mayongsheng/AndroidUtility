package letv.android.com.androidutility;

/**
 * Created by mayongsheng on 16/1/9.
 */
public class CheckParamUtility {

    public static void exceptionOnParamNull(Object param,String msgIfNull){
        if(param ==null){
            throw new IllegalArgumentException(msgIfNull);
        }
    }

    public static boolean falseOnParamNull(Object param){
        if(param == null){
            return false;
        }

        return false;
    }
}
