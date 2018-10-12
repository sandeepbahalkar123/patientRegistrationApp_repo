package com.scorg.regform.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.scorg.regform.R;
import com.scorg.regform.customui.CustomButton;
import com.scorg.regform.customui.CustomEditText;
import com.scorg.regform.helpers.LoginHelper;
import com.scorg.regform.interfaces.CustomResponse;
import com.scorg.regform.interfaces.HelperResponse;
import com.scorg.regform.models.login.ClinicList;
import com.scorg.regform.models.login.ClinicLocationBrandingInfo;
import com.scorg.regform.models.login.LoginModel;
import com.scorg.regform.preference.AppPreferencesManager;
import com.scorg.regform.util.CommonMethods;
import com.scorg.regform.util.Constants;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements HelperResponse {

    // Content View Elements

    private CustomEditText mUserNameEditText;
    private CustomEditText mPasswordEditText;
    private CustomButton mLoginButton;
    private Context mContext;

    // End Of Content View Elements

    private void bindViews() {
        mUserNameEditText = findViewById(R.id.userNameEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mLoginButton = findViewById(R.id.loginButton);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = LoginActivity.this;

        bindViews();

        mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_GO)
                    go();
                return false;
            }

        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go();
            }
        });
    }

    private void go() {

        String username = mUserNameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        if (validate(username, password)) {
            LoginHelper loginHelper = new LoginHelper(mContext);
            loginHelper.doLogin(username, password);
        }
    }

    // validation for mobileNo on click of otp Button
    private boolean validate(String username, String password) {
        String message = null;
        if (username.isEmpty()) {
            message = getString(R.string.username_invalid);
            mUserNameEditText.requestFocus();
        } else if (password.isEmpty()) {
            message = getString(R.string.enter_password);
            mPasswordEditText.requestFocus();
        }

        if (message != null)
            CommonMethods.showToast(mContext, message);

        return message == null;
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(Constants.TASK_LOGIN)) {
            //After login user navigated to HomepageActivity
            LoginModel loginModel = (LoginModel) customResponse;
            if (loginModel.getCommon().isSuccess()) {

                if (!loginModel.getData().getClinicList().isEmpty()) {
                    if (loginModel.getData().getClinicList().size() == 1) {

                        ClinicList clinicDetails = loginModel.getData().getClinicList().get(0);

                        if (!clinicDetails.getClinicLocationBrandingInfo().isEmpty()) {
                            if (clinicDetails.getClinicLocationBrandingInfo().size() == 1) {
                                storeInfo(loginModel.getData().getDocId(), loginModel.getData().getDrName(), clinicDetails, clinicDetails.getClinicLocationBrandingInfo().get(0));
                            } else {
                                showLocationDialog(loginModel.getData().getDocId(), loginModel.getData().getDrName(), loginModel.getData().getClinicList());
                            }
                        } else {
                            CommonMethods.showToast(mContext, "Location Information not found.");
                        }
                    } else {
                        showLocationDialog(loginModel.getData().getDocId(), loginModel.getData().getDrName(), loginModel.getData().getClinicList());
                    }
                } else {
                    CommonMethods.showToast(mContext, "Clinic Information not found.");
                }

            } else {
                CommonMethods.showToast(mContext, loginModel.getCommon().getStatusMessage());
            }
        }
    }

    private void storeInfo(int docId, String docName, ClinicList clinicDetails, ClinicLocationBrandingInfo clinicLocationBrandingInfo) {

        AppPreferencesManager.putInt(AppPreferencesManager.CLINIC_KEY.CLINIC_ID, clinicDetails.getClinicId(), mContext);
        AppPreferencesManager.putString(AppPreferencesManager.CLINIC_KEY.CLINIC_NAME, clinicDetails.getClinicName(), mContext);

        AppPreferencesManager.putInt(AppPreferencesManager.CLINIC_KEY.CLINIC_DOCTOR_ID, docId, mContext);
        AppPreferencesManager.putString(AppPreferencesManager.CLINIC_KEY.CLINIC_DOCTOR_NAME, docName, mContext);
        AppPreferencesManager.putString(AppPreferencesManager.CLINIC_KEY.CLINIC_ADDRESS, clinicLocationBrandingInfo.getAddress(), mContext);
        AppPreferencesManager.putString(AppPreferencesManager.CLINIC_KEY.CLINIC_BIG_LOGO, clinicLocationBrandingInfo.getClinicBigLogoUrl(), mContext);
        AppPreferencesManager.putString(AppPreferencesManager.CLINIC_KEY.CLINIC_SMALL_LOGO, clinicLocationBrandingInfo.getClinicSmallLogoUrl(), mContext);
        AppPreferencesManager.putString(AppPreferencesManager.CLINIC_KEY.CLINIC_TAG_LINE, clinicLocationBrandingInfo.getTagline(), mContext);
        AppPreferencesManager.putInt(AppPreferencesManager.CLINIC_KEY.CLINIC_LOCATION_ID, clinicLocationBrandingInfo.getLocationId(), mContext);

        AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.LOGIN_STATUS, Constants.YES, mContext);

        AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.USER_NAME, mUserNameEditText.getText().toString(), mContext);
        AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.PASSWORD, mPasswordEditText.getText().toString(), mContext);

        Intent intentObj = new Intent(LoginActivity.this, PersonalInfoActivity.class);
        startActivity(intentObj);
        finish();
    }

    // Selection

    public ClinicLocationBrandingInfo clinicLocInfo;
    public ClinicList clinicDetails;

    public void showLocationDialog(final int docId, final String drName, final ArrayList<ClinicList> clinicList) {

        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.clinic_location_dialog);

        Spinner dropDownClinic = dialog.findViewById(R.id.dropDownClinic);
        final Spinner dropDownLocation = dialog.findViewById(R.id.dropDownLocation);

        if (clinicList.size() > 0)
            if (!clinicList.get(0).toString().equals("Select Clinic")) {
                ClinicList clinicL = new ClinicList();
                clinicL.setClinicId(0);
                clinicL.setClinicName("Select Clinic");
                clinicList.add(0, clinicL);
            }

        ArrayAdapter<?> adapterClinic = new ArrayAdapter<>(dropDownClinic.getContext(), R.layout.dropdown_item, clinicList);
        dropDownClinic.setAdapter(adapterClinic);

        dropDownClinic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    // set latest value
                    final ClinicList clinicL = clinicList.get(position);
                    if (clinicL.getClinicLocationBrandingInfo().size() > 0)
                        if (!clinicL.getClinicLocationBrandingInfo().get(0).toString().equals("Select Location")) {
                            ClinicLocationBrandingInfo clinicLocationBrandingInfo = new ClinicLocationBrandingInfo();
                            clinicLocationBrandingInfo.setAddress("Select Location");
                            clinicL.getClinicLocationBrandingInfo().add(0, clinicLocationBrandingInfo);
                        }

                    ArrayAdapter<?> adapter = new ArrayAdapter<>(dropDownLocation.getContext(), R.layout.dropdown_item, clinicL.getClinicLocationBrandingInfo());
                    dropDownLocation.setAdapter(adapter);
                    dropDownLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            if (position != 0) {
                                // set latest value
                                clinicDetails = clinicL;
                                clinicLocInfo = clinicL.getClinicLocationBrandingInfo().get(position);
                            } else clinicLocInfo = null;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // bindview
        final Button okButton = dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clinicDetails != null && clinicLocInfo != null) {
                    dialog.dismiss();
                    storeInfo(docId, drName, clinicDetails, clinicLocInfo);
                }
                else
                    CommonMethods.showToast(okButton.getContext(), "Please Select Clinic and Location");
            }
        });

        dialog.show();
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        CommonMethods.showToast(mContext, errorMessage);
    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(mContext, serverErrorMessage);
    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(mContext, serverErrorMessage);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.exit_message))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
