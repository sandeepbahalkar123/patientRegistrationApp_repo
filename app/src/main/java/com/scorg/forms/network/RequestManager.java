package com.scorg.forms.network;

/**
 * @author Sandeep Bahalkar
 */

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.scorg.forms.R;
import com.scorg.forms.customui.CustomProgressDialog;
import com.scorg.forms.database.AppDBHelper;
import com.scorg.forms.interfaces.ConnectionListener;
import com.scorg.forms.interfaces.Connector;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.models.Common;
import com.scorg.forms.models.CommonResponse;
import com.scorg.forms.models.PatientData;
import com.scorg.forms.models.login.IpTestResponseModel;
import com.scorg.forms.models.login.LoginModel;
import com.scorg.forms.models.login.request.LoginRequestModel;
import com.scorg.forms.models.master.MasterDataModel;
import com.scorg.forms.models.profile.ProfilesModel;
import com.scorg.forms.preference.AppPreferencesManager;
import com.scorg.forms.singleton.Device;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Config;
import com.scorg.forms.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;

public class RequestManager extends ConnectRequest implements Connector, RequestTimer.RequestTimerListener {
    private static final int CONNECTION_TIME_OUT = 1000 * 60;
    private static final int N0OF_RETRY = 0;
    private final String TAG = this.getClass().getName();
    private final Gson gson;
    private AppDBHelper dbHelper;
    private String requestTag;
    private int connectionType = POST;

    private String mDataTag;
    private RequestTimer requestTimer;
    private JsonObjectRequest jsonRequest;
    private StringRequest stringRequest;

    public RequestManager(Context mContext, ConnectionListener connectionListener, String dataTag, View viewById, boolean isProgressBarShown, String mOldDataTag, int connectionType, boolean isOffline) {
        super();
        this.mConnectionListener = connectionListener;
        this.mContext = mContext;
        this.mDataTag = dataTag;
        this.mViewById = viewById;
        this.isProgressBarShown = isProgressBarShown;
        this.mOldDataTag = mOldDataTag;
        this.requestTag = String.valueOf(dataTag);
        this.requestTimer = new RequestTimer();
        this.requestTimer.setListener(this);
        this.mProgressDialog = new CustomProgressDialog(mContext);
        this.connectionType = connectionType;
        this.isOffline = isOffline;
        this.dbHelper = new AppDBHelper(mContext);

        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        this.gson = builder.create();
    }

    @Override
    public void connect() {

        RequestPool.getInstance(this.mContext).cancellAllPreviousRequestWithSameTag(requestTag);

        if (isProgressBarShown) {
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }

        if (mPostParams != null) {
            stringRequest(mURL, connectionType, mHeaderParams, mPostParams, false);
        } else if (customResponse != null) {
            jsonRequest(mURL, connectionType, mHeaderParams, customResponse, false);
        } else {
            jsonRequest();
        }
    } /*else {

            if (isOffline) {
                if (getOfflineData() != null)
                    successResponse(getOfflineData(), false);
                else
                    mConnectionListener.onResponse(ConnectionListener.NO_INTERNET, null, mOldDataTag);
            } else {
                mConnectionListener.onResponse(ConnectionListener.NO_INTERNET, null, mOldDataTag);
            }

            if (mViewById != null)
                CommonMethods.showSnack(mViewById, mContext.getString(R.string.internet));
            else
                CommonMethods.showToast(mContext, mContext.getString(R.string.internet));
        }
    }*/

    private void jsonRequest(String url, int connectionType, final Map<String, String> headerParams, CustomResponse customResponse, final boolean isTokenExpired) {

        JSONObject jsonObject = null;
        try {
            String jsonString = gson.toJson(customResponse);

            CommonMethods.log(TAG, "jsonRequest:--" + jsonString);

            if (!jsonString.equals("null"))
                jsonObject = new JSONObject(jsonString);
        } catch (JSONException | JsonSyntaxException e) {
            e.printStackTrace();
        }

        jsonRequest = new JsonObjectRequest(connectionType, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        successResponse(response.toString(), isTokenExpired);
                        if (isOffline)
                            dbHelper.insertData(mDataTag, response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorResponse(error, isTokenExpired);
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headerParams == null) {
                    return Collections.emptyMap();
                } else {
                    return headerParams;
                }

            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(CONNECTION_TIME_OUT, N0OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonRequest.setTag(requestTag);
        requestTimer.start();
        RequestPool.getInstance(this.mContext).addToRequestQueue(jsonRequest);
    }


    private void jsonRequest() {

        Gson gson = new Gson();
        JSONObject jsonObject = null;
        try {
            String jsonString = gson.toJson(customResponse);

            CommonMethods.log(TAG, "jsonRequest:--" + jsonString);

            if (!jsonString.equals("null"))
                jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        jsonRequest = new JsonObjectRequest(connectionType, this.mURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        successResponse(response.toString(), false);
                        if (isOffline)
                            dbHelper.insertData(mDataTag, response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorResponse(error, false);
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (mHeaderParams == null) {
                    return Collections.emptyMap();
                } else {
                    return mHeaderParams;
                }

            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(CONNECTION_TIME_OUT, N0OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonRequest.setTag(requestTag);
        requestTimer.start();
        RequestPool.getInstance(this.mContext).addToRequestQueue(jsonRequest);
    }

    private void stringRequest(String url, int connectionType, final Map<String, String> headerParams, final Map<String, String> postParams, final boolean isTokenExpired) {

        // ganesh for string request and delete method with string request

        stringRequest = new StringRequest(connectionType, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        successResponse(response, isTokenExpired);
                        if (isOffline)
                            dbHelper.insertData(mDataTag, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        errorResponse(error, isTokenExpired);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headerParams;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return postParams;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(CONNECTION_TIME_OUT, N0OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setTag(requestTag);
        requestTimer.start();
        RequestPool.getInstance(this.mContext).addToRequestQueue(stringRequest);
    }

    private void successResponse(String response, boolean isTokenExpired) {
        requestTimer.cancel();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        parseJson(fixEncoding(response), isTokenExpired);
    }

    private void errorResponse(VolleyError error, boolean isTokenExpired) {

        requestTimer.cancel();

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        try {

//            VolleyError error1 = new VolleyError(new String(error.networkResponse.data));
//            error = error1;
//            CommonMethods.log("Error Message", error.getMessage() + "\n error Localize message" + error.getLocalizedMessage());
            CommonMethods.log(TAG, "Goes into error response condition");

            if (error instanceof TimeoutError) {

                if (error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found") || error.getMessage().equalsIgnoreCase("invalid_grant")) {
                    if (!isTokenExpired) {
                        tokenRefreshRequest();
                    }
                }

//                if (error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found") || error.getMessage().equalsIgnoreCase("invalid_grant")) {
//                    if (mViewById != null)
//                        CommonMethods.showSnack(mViewById, mContext.getString(R.string.authentication));
//                    else
//                        CommonMethods.showToast(mContext, mContext.getString(R.string.authentication));
//                } else if (error.getMessage().equalsIgnoreCase("javax.net.ssl.SSLHandshakeException: java.security.cert.CertPathValidatorException: Trust anchor for certification path not found.")) {
//                    showErrorDialog("Something went wrong.");
//                }

                if (mViewById != null)
                    CommonMethods.showSnack(mViewById, mContext.getString(R.string.timeout));
                else
                    CommonMethods.showToast(mContext, mContext.getString(R.string.timeout));

            } else if (error instanceof NoConnectionError) {

                if (error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found") || error.getMessage().equalsIgnoreCase("invalid_grant")) {
                    if (!isTokenExpired) {
                        tokenRefreshRequest();
                    }
                }

                if (isOffline && getOfflineData() != null)
                    successResponse(getOfflineData(), false);

                if (mViewById != null)
                    CommonMethods.showSnack(mViewById, mContext.getString(R.string.internet));
                else {
                    mConnectionListener.onResponse(ConnectionListener.NO_CONNECTION_ERROR, null, mOldDataTag);
                }

            } else if (error instanceof ServerError) {
                if (isTokenExpired) {
                    // Redirect to SplashScreen then Login
//                    Intent intent = new Intent(mContext, LoginActivity.class);
//                    mContext.startActivity(intent);
//                    ((AppCompatActivity) mContext).finishAffinity();

//                    PreferencesManager.clearSharedPref(mContext);
//                    Intent intent = new Intent(mContext, SplashScreenActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    mContext.startActivity(intent);

                    //  loginRequest();
                } else {
                    mConnectionListener.onResponse(ConnectionListener.SERVER_ERROR, null, mOldDataTag);
                    CommonMethods.showToast(mContext, mContext.getResources().getString(R.string.server_error) + " " + (error.networkResponse == null ? "" : error.networkResponse.statusCode));
                }
            } else if (error instanceof NetworkError) {

                if (isOffline) {
                    successResponse(getOfflineData(), false);
                } else {
                    mConnectionListener.onResponse(ConnectionListener.NO_INTERNET, null, mOldDataTag);
                }

                if (mViewById != null)
                    CommonMethods.showSnack(mViewById, mContext.getString(R.string.internet));
                else
                    CommonMethods.showToast(mContext, mContext.getString(R.string.internet));
            } else if (error instanceof ParseError) {
                mConnectionListener.onResponse(ConnectionListener.PARSE_ERR0R, null, mOldDataTag);
            } else if (error instanceof AuthFailureError) {
                if (!isTokenExpired) {
                    tokenRefreshRequest();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getOfflineData() {
        if (dbHelper.dataTableNumberOfRows(this.mDataTag) > 0) {
            Cursor cursor = dbHelper.getData(this.mDataTag);
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(AppDBHelper.COLUMN_DATA));
        } else {
            return null;
        }
    }

    private String fixEncoding(String response) {
        try {
            byte[] u = response.getBytes("ISO-8859-1");
            response = new String(u, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    @Override
    public void parseJson(String data, boolean isTokenExpired) {
        try {
            CommonMethods.log(TAG, data);
            Gson gson = new Gson();

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(data);
                Common common = gson.fromJson(jsonObject.optString("common"), Common.class);
                if (!common.isSuccess()) {
                    CommonMethods.showToast(mContext, common.getStatusMessage());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*MessageModel messageModel = gson.fromJson(data, MessageModel.class);
            if (!messageModel.getCommon().getStatusCode().equals(Constants.SUCCESS))
                CommonMethods.showToast(mContext, messageModel.getCommon().getStatusMessage());*/

            if (isTokenExpired) {
                // This success response is for refresh token
                // Need to Add
               /* LoginModel loginModel = gson.fromJson(data, LoginModel.class);
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.AUTHTOKEN, loginModel.getDoctorLoginData().getAuthToken(), mContext);
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.LOGIN_STATUS, Constants.YES, mContext);
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.DOC_ID, String.valueOf(loginModel.getDoctorLoginData().getDocDetail().getDocId()), mContext);

                mHeaderParams.put(Constants.AUTHORIZATION_TOKEN, loginModel.getDoctorLoginData().getAuthToken());

                connect();*/

            } else {
                // This success response is for respective api's

                switch (this.mDataTag) {

                    case Constants.TASK_LOGIN: // This is for get archived list
                        LoginModel loginModel = gson.fromJson(data, LoginModel.class);
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, loginModel, mOldDataTag);
                        break;

                    case Constants.TASK_CHECK_SERVER_CONNECTION: //This is for get archived list
                        IpTestResponseModel ipTestResponseModel = gson.fromJson(data, IpTestResponseModel.class);
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, ipTestResponseModel, mOldDataTag);
                        break;

                    case Constants.GET_PROFILE_INFO: //This is for get archived list
                        ProfilesModel profilesModel = gson.fromJson(data, ProfilesModel.class);
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, profilesModel, mOldDataTag);
                        break;

                    case Constants.GET_PROFILE: //This is for get archived list
                        PatientData patientData = gson.fromJson(data, PatientData.class);
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, patientData, mOldDataTag);
                        break;

                    case Constants.POST_PERSONAL_DATA: //This is for get archived list
                        CommonResponse commonResPersonal = gson.fromJson(data, CommonResponse.class);
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, commonResPersonal, mOldDataTag);
                        break;

                    case Constants.POST_FORM_DATA: //This is for get archived list
                        CommonResponse commonResForm = gson.fromJson(data, CommonResponse.class);
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, commonResForm, mOldDataTag);
                        break;

                    case Constants.GET_MASTER_DATA: //This is for get archived list
                        MasterDataModel masterDataModel = gson.fromJson(data, MasterDataModel.class);
                        this.mConnectionListener.onResponse(ConnectionListener.RESPONSE_OK, masterDataModel, mOldDataTag);
                        break;

                    default:
                }
            }

        } catch (JsonSyntaxException e) {
            CommonMethods.log(TAG, "JsonException" + e.getMessage());
            mConnectionListener.onResponse(ConnectionListener.PARSE_ERR0R, null, mOldDataTag);
        }

    }

    @Override
    public void setPostParams(CustomResponse customResponse) {
        this.customResponse = customResponse;
    }

    @Override
    public void setPostParams(Map<String, String> postParams) {
        this.mPostParams = postParams;
    }

    @Override
    public void setHeaderParams(Map<String, String> headerParams) {
        this.mHeaderParams = headerParams;
    }

    @Override
    public void abort() {
        if (jsonRequest != null)
            jsonRequest.cancel();
        if (stringRequest != null)
            stringRequest.cancel();
    }

    @Override
    public void setUrl(String url) {
        this.mURL = url;
    }

    @Override
    public void onTimeout(RequestTimer requestTimer) {
        if (mContext instanceof AppCompatActivity) {
            ((AppCompatActivity) this.mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }
            });
        }

        RequestPool.getInstance(mContext)
                .cancellAllPreviousRequestWithSameTag(requestTag);
        mConnectionListener.onTimeout(this);
    }

    public void showErrorDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void tokenRefreshRequest() {
        loginRequest();
    }

    private void loginRequest() {
        CommonMethods.log(TAG, "Refresh token while sending refresh token api: ");
        String url = AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, mContext) + Config.LOGIN_URL;

        LoginRequestModel loginRequestModel = new LoginRequestModel();

        loginRequestModel.setUserName(AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.EMAIL, mContext));
        loginRequestModel.setPassword(AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.PASSWORD, mContext));
        if (!(Constants.BLANK.equalsIgnoreCase(loginRequestModel.getUserName()) &&
                Constants.BLANK.equalsIgnoreCase(loginRequestModel.getPassword()))) {
            Map<String, String> headerParams = new HashMap<>();
            headerParams.putAll(mHeaderParams);
            Device device = Device.getInstance(mContext);

            headerParams.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            headerParams.put(Constants.DEVICE_ID, device.getDeviceId());
            headerParams.put(Constants.OS, device.getOS());
            headerParams.put(Constants.OSVERSION, device.getOSVersion());
            headerParams.put(Constants.DEVICE_TYPE, device.getDeviceType());
            CommonMethods.log(TAG, "setHeaderParams:" + headerParams.toString());
            jsonRequest(url, POST, headerParams, loginRequestModel, true);
        } else {
            mConnectionListener.onResponse(ConnectionListener.PARSE_ERR0R, null, mOldDataTag);
        }
    }
}