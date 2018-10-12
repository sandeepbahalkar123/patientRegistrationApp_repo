package com.scorg.regform.network;

/**
 * @author Sandeep Bahalkar
 */

import android.content.Context;
import android.view.View;

import com.scorg.regform.interfaces.ConnectionListener;
import com.scorg.regform.interfaces.Connector;
import com.scorg.regform.interfaces.CustomResponse;
import com.scorg.regform.preference.AppPreferencesManager;
import com.scorg.regform.singleton.Device;
import com.scorg.regform.util.CommonMethods;
import com.scorg.regform.util.Config;
import com.scorg.regform.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class ConnectionFactory extends ConnectRequest {

    private final String TAG = this.getClass().getName();
    Connector connector = null;
    private Device device;

    public ConnectionFactory(Context context, ConnectionListener connectionListener, View viewById, boolean isProgressBarShown, String mOldDataTag, int reqPostOrGet, boolean isOffline) {
        super();
        this.mConnectionListener = connectionListener;
        this.mContext = context;
        this.mViewById = viewById;
        this.isProgressBarShown = isProgressBarShown;
        this.mOldDataTag = mOldDataTag;
        this.reqPostOrGet = reqPostOrGet;
        this.isOffline = isOffline;

        device = Device.getInstance(mContext);
    }

    public void setHeaderParams(Map<String, String> headerParams) {
        this.mHeaderParams = headerParams;
    }

    public void setHeaderParams() {

        Map<String, String> headerParams = new HashMap<>();
        String authorizationString = AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.AUTHTOKEN, mContext);
        headerParams.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        headerParams.put(Constants.AUTHORIZATION_TOKEN, authorizationString);
        headerParams.put(Constants.DEVICE_ID, device.getDeviceId());
        headerParams.put(Constants.OS, device.getOS());
        headerParams.put(Constants.OSVERSION, device.getOSVersion());
        headerParams.put(Constants.DEVICE_TYPE, device.getDeviceType());

        CommonMethods.log(TAG, "setHeaderParams:" + headerParams.toString());
        this.mHeaderParams = headerParams;
    }

    public void setPostParams(CustomResponse customResponse) {
        this.customResponse = customResponse;
    }

    public void setPostParams(Map<String, String> postParams) {
        this.mPostParams = postParams;
    }

    public void setUrl(String url) {
        this.mURL = Config.HTTP + AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, mContext) + "/" + url;
        CommonMethods.log(TAG, "mURL: " + this.mURL);
    }

    public Connector createConnection(String type) {

        connector = new RequestManager(mContext, mConnectionListener, type, mViewById, isProgressBarShown, mOldDataTag, reqPostOrGet, isOffline);

        if (customResponse != null) connector.setPostParams(customResponse);

        if (mPostParams != null) connector.setPostParams(mPostParams);

        if (mHeaderParams != null) connector.setHeaderParams(mHeaderParams);

        if (mURL != null) connector.setUrl(mURL);

        connector.connect();

        return connector;
    }

    public void cancel() {
        connector.abort();
    }
}