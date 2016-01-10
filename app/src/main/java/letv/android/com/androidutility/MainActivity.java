package letv.android.com.androidutility;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String newVersion = "10.2.1111";
        String oldVersion="3.333333.3";
        int result = ApplicationUtility.compareVersionCode(oldVersion,newVersion);
        Log.i("MainActivity", "1 expected result=" + result);
        newVersion = "2.2.1111";
        oldVersion="3.333333.3";
        int result1 = ApplicationUtility.compareVersionCode(oldVersion,newVersion);
        Log.i("MainActivity", "-1 expected result1=" + result1);

        newVersion = "3.333333.3";
        oldVersion="3.333333.3";
        int result2 = ApplicationUtility.compareVersionCode(oldVersion,newVersion);
        Log.i("MainActivity", "0 expected result2=" + result2);
    }
}
