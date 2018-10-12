package com.scorg.regform.singleton;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;
import net.gotev.uploadservice.BuildConfig;
import net.gotev.uploadservice.UploadService;

/**
 * Created by ganeshshirole on 26/10/17.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
    }
}
