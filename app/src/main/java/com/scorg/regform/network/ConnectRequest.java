package com.scorg.regform.network;

import android.content.Context;
import android.view.View;

import com.scorg.regform.customui.CustomProgressDialog;
import com.scorg.regform.interfaces.ConnectionListener;
import com.scorg.regform.interfaces.CustomResponse;

import java.util.Map;

public class ConnectRequest {
    protected Context mContext;
    protected CustomResponse customResponse;
    protected Map<String, String> mHeaderParams;
    protected Map<String, String> mPostParams;
    protected ConnectionListener mConnectionListener;
    protected CustomProgressDialog mProgressDialog;
    protected View mViewById;
    protected boolean isProgressBarShown;
    protected String mOldDataTag;
    protected String mURL;
    protected int reqPostOrGet;
    protected boolean isOffline;
}
