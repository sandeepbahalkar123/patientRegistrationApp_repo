package com.scorg.forms.helpers;

import android.content.Context;

import com.android.volley.Request;
import com.scorg.forms.interfaces.ConnectionListener;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.interfaces.HelperResponse;
import com.scorg.forms.models.form.request.FormRequest;
import com.scorg.forms.models.form.request.GetPatientInfo;
import com.scorg.forms.models.master.request.MasterDataRequest;
import com.scorg.forms.network.ConnectRequest;
import com.scorg.forms.network.ConnectionFactory;
import com.scorg.forms.preference.AppPreferencesManager;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Config;

import static com.scorg.forms.util.Constants.GET_MASTER_DATA;
import static com.scorg.forms.util.Constants.GET_PROFILE;
import static com.scorg.forms.util.Constants.GET_PROFILE_INFO;
import static com.scorg.forms.util.Constants.POST_FORM_DATA;
import static com.scorg.forms.util.Constants.POST_PERSONAL_DATA;

public class PatientHelper implements ConnectionListener {
    private String TAG = this.getClass().getName();
    private Context mContext;
    private HelperResponse mHelperResponseManager;

    public PatientHelper(Context context, HelperResponse helperResponse) {
        this.mContext = context;
        this.mHelperResponseManager = helperResponse;
    }

    @Override
    public void onResponse(int responseResult, CustomResponse customResponse, String mOldDataTag) {
        switch (responseResult) {
            case ConnectionListener.RESPONSE_OK:
                mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
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

    public void getPatientProfileInfo(String mobileText) {
        int doctorId = AppPreferencesManager.getInt(AppPreferencesManager.CLINIC_KEY.CLINIC_DOCTOR_ID, mContext);
        int locationId = AppPreferencesManager.getInt(AppPreferencesManager.CLINIC_KEY.CLINIC_LOCATION_ID, mContext);
        int clinicId = AppPreferencesManager.getInt(AppPreferencesManager.CLINIC_KEY.CLINIC_ID, mContext);

        GetPatientInfo getPatientInfo = new GetPatientInfo();
        getPatientInfo.setMobileNumber(mobileText);
        getPatientInfo.setDocId(doctorId);
        getPatientInfo.setLocationId(locationId);
        getPatientInfo.setHospitalId(clinicId);

        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, GET_PROFILE_INFO, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(getPatientInfo);
        mConnectionFactory.setUrl(Config.GET_PROFILE_LIST);
        mConnectionFactory.createConnection(GET_PROFILE_INFO);
    }

    public void getPatientProfile(String mobileText, String profileId) {

        // Stored profile Mobile and Id in Preferences.

        AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.PROFILE_ID, profileId, mContext);
        AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.MOBILE, mobileText, mContext);

        int doctorId = AppPreferencesManager.getInt(AppPreferencesManager.CLINIC_KEY.CLINIC_DOCTOR_ID, mContext);
        int locationId = AppPreferencesManager.getInt(AppPreferencesManager.CLINIC_KEY.CLINIC_LOCATION_ID, mContext);
        int clinicId = AppPreferencesManager.getInt(AppPreferencesManager.CLINIC_KEY.CLINIC_ID, mContext);

        GetPatientInfo getPatientInfo = new GetPatientInfo();
        getPatientInfo.setMobileNumber(mobileText);
        getPatientInfo.setDocId(doctorId);
        getPatientInfo.setLocationId(locationId);
        getPatientInfo.setHospitalId(clinicId);
        getPatientInfo.setProfileId(Integer.parseInt(profileId));

        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, GET_PROFILE, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(getPatientInfo);
        mConnectionFactory.setUrl(Config.GET_PROFILE);
        mConnectionFactory.createConnection(GET_PROFILE);
    }

    public void getMasterData(MasterDataRequest masterDataRequest) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, GET_MASTER_DATA, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(masterDataRequest);
        mConnectionFactory.setUrl(Config.GET_MASTER_DATA);
        mConnectionFactory.createConnection(GET_MASTER_DATA);
    }

    public void postPersonalInfo(FormRequest formRequest) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, POST_PERSONAL_DATA, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(formRequest);
        mConnectionFactory.setUrl(Config.POST_PERSONAL_DATA);
        mConnectionFactory.createConnection(POST_PERSONAL_DATA);
    }

    public void postFromData(FormRequest form) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, POST_FORM_DATA, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(form);
        mConnectionFactory.setUrl(Config.POST_FORM_DATA);
        mConnectionFactory.createConnection(POST_FORM_DATA);
    }
}
