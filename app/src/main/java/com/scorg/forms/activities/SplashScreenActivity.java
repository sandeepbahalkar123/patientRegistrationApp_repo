package com.scorg.forms.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.scorg.forms.R;
import com.scorg.forms.helpers.LoginHelper;
import com.scorg.forms.interfaces.CheckIpConnection;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.interfaces.HelperResponse;
import com.scorg.forms.models.login.IpTestResponseModel;
import com.scorg.forms.preference.AppPreferencesManager;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Constants;

public class SplashScreenActivity extends AppCompatActivity implements HelperResponse {

    private Context mContext;
    private LoginHelper mLoginHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mContext = SplashScreenActivity.this;
        mLoginHelper = new LoginHelper(mContext);

        // hardcoded
//        AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, "192.168.0.182:4004", mContext);
//        AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.IS_VALID_IP_CONFIG, Constants.TRUE, mContext);

        doNext();

    }

    private void doNext() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.LOGIN_STATUS, mContext).equals(Constants.YES)) {
                    Intent intentObj = new Intent(SplashScreenActivity.this, PersonalInfoActivity.class);
                    startActivity(intentObj);
                    finish();
                } else {

                    if (!AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.IS_VALID_IP_CONFIG, mContext).equals(Constants.TRUE)) {
                        //alert dialog for serverpath
                        CommonMethods.showAlertDialog(SplashScreenActivity.this, getString(R.string.server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
                            @Override
                            public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                                mLoginHelper.checkConnectionToServer(serverPath);
                                dialog.dismiss();
                            }
                        }, true);
                    } else {
                        Intent intentObj = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(intentObj);
                        finish();
                    }
                }
            }
        }, Constants.TIME_STAMPS.THREE_SECONDS);
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        IpTestResponseModel ipTestResponseModel = (IpTestResponseModel) customResponse;
        if (ipTestResponseModel.getCommon().isSuccess()) {
            AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.IS_VALID_IP_CONFIG, Constants.TRUE, mContext);
            Intent intentObj = new Intent(mContext, LoginActivity.class);
            startActivity(intentObj);
            finish();
        } else {
            AppPreferencesManager.putString(Constants.LOGIN_SUCCESS, Constants.FALSE, mContext);
            CommonMethods.showAlertDialog(SplashScreenActivity.this, getString(R.string.wrong_server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
                @Override
                public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                    AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                    mLoginHelper.checkConnectionToServer(serverPath);
                }
            }, true);
        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        AppPreferencesManager.putString(Constants.LOGIN_SUCCESS, Constants.FALSE, mContext);
        CommonMethods.showAlertDialog(SplashScreenActivity.this, getString(R.string.wrong_server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
            @Override
            public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                mLoginHelper.checkConnectionToServer(serverPath);
            }
        }, true);
    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        AppPreferencesManager.putString(Constants.LOGIN_SUCCESS, Constants.FALSE, mContext);
        CommonMethods.showAlertDialog(SplashScreenActivity.this, getString(R.string.wrong_server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
            @Override
            public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                mLoginHelper.checkConnectionToServer(serverPath);
            }
        }, true);
    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        AppPreferencesManager.putString(Constants.LOGIN_SUCCESS, Constants.FALSE, mContext);
        CommonMethods.showAlertDialog(SplashScreenActivity.this, getString(R.string.wrong_server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
            @Override
            public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                mLoginHelper.checkConnectionToServer(serverPath);
            }
        }, true);
    }
}
