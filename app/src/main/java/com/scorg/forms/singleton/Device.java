package com.scorg.forms.singleton;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;

import com.scorg.forms.R;
import com.scorg.forms.util.Constants;

/**
 * Created by Sandeep Bahalkar
 */

public class Device {

    private final String TAG = this.getClass().getName();
    private Context context;
    private WindowManager windowManager;

    public Device(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public Device(Context context) {
        this.context = context;
    }

    /**
     * Create a static method to get instance.
     */

    public static Device getInstance(Context context) {

        return new Device(context);
    }

    public static Device getInstance(WindowManager windowManager) {

        return new Device(windowManager);
    }

    public String getDeviceType() {
        String what = "";
        boolean tabletSize = context.getResources().getBoolean(R.bool.isTab);
        if (tabletSize) {
            what = Constants.TABLET;
        } else {
            what = Constants.PHONE;
        }
        return what;
    }


    public String getDeviceId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public String getOS() {
        return "Android (" + Build.BRAND + ")";
    }


}
