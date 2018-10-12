package com.scorg.regform.helpers;

import android.content.Context;

import com.android.volley.Request;
import com.scorg.regform.interfaces.ConnectionListener;
import com.scorg.regform.interfaces.CustomResponse;
import com.scorg.regform.interfaces.HelperResponse;
import com.scorg.regform.models.login.LoginModel;
import com.scorg.regform.models.login.request.LoginRequestModel;
import com.scorg.regform.network.ConnectRequest;
import com.scorg.regform.network.ConnectionFactory;
import com.scorg.regform.util.CommonMethods;
import com.scorg.regform.util.Config;
import com.scorg.regform.util.Constants;

public class LoginHelper implements ConnectionListener {
    private String TAG = this.getClass().getName();
    private Context mContext;
    private HelperResponse mHelperResponseManager;
    private String mServerPath;

    public LoginHelper(Context context) {
        this.mContext = context;
        this.mHelperResponseManager = (HelperResponse) context;
    }


    @Override
    public void onResponse(int responseResult, CustomResponse customResponse, String mOldDataTag) {

        //CommonMethods.log(TAG, customResponse.toString());
        switch (responseResult) {
            case ConnectionListener.RESPONSE_OK:
                switch (mOldDataTag) {
                    case Constants.TASK_LOGIN:
                        LoginModel loginModel = (LoginModel) customResponse;
                        mHelperResponseManager.onSuccess(mOldDataTag, loginModel);
                        break;
                    case Constants.TASK_CHECK_SERVER_CONNECTION:
                        mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                        break;
                }
                break;
            case ConnectionListener.PARSE_ERR0R:
                CommonMethods.log(TAG, "parse error");
                mHelperResponseManager.onParseError(mOldDataTag, "parse error");
                break;
            case ConnectionListener.SERVER_ERROR:
                CommonMethods.log(TAG, "server error");
                mHelperResponseManager.onServerError(mOldDataTag, "server error");
                break;
            case ConnectionListener.NO_CONNECTION_ERROR:
                CommonMethods.log(TAG, "no connection error");
                mHelperResponseManager.onNoConnectionError(mOldDataTag, "no connection error");
                break;
            default:
                CommonMethods.log(TAG, "default error");
                break;
        }
    }

    @Override
    public void onTimeout(ConnectRequest request) {

    }

    //Do login using mobileNo and password
    public void doLogin(String username, String password) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, Constants.TASK_LOGIN, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        LoginRequestModel loginRequestModel = new LoginRequestModel();
        loginRequestModel.setUserName(username);
        loginRequestModel.setPassword(password);
        mConnectionFactory.setPostParams(loginRequestModel);
        mConnectionFactory.setUrl(Config.LOGIN_URL);
        mConnectionFactory.createConnection(Constants.TASK_LOGIN);
    }

    public void checkConnectionToServer(String serverPath) {
        this.mServerPath = serverPath;
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, Constants.TASK_CHECK_SERVER_CONNECTION, Request.Method.GET, false);
        mConnectionFactory.setUrl(Config.URL_CHECK_SERVER_CONNECTION);
        mConnectionFactory.createConnection(Constants.TASK_CHECK_SERVER_CONNECTION);
    }
}