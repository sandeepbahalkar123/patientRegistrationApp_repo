package com.scorg.forms.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.scorg.forms.R;
import com.scorg.forms.fragments.FeedbackFormFragment;
import com.scorg.forms.fragments.FormFragment;
import com.scorg.forms.fragments.UndertakingFragment;
import com.scorg.forms.helpers.PatientHelper;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.interfaces.HelperResponse;
import com.scorg.forms.models.CommonResponse;
import com.scorg.forms.models.form.Form;
import com.scorg.forms.models.form.request.FormRequest;
import com.scorg.forms.models.form.request.Header;
import com.scorg.forms.preference.AppPreferencesManager;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Constants;
import com.scorg.forms.util.GlideApp;

import static com.scorg.forms.activities.PersonalInfoActivity.PATIENT_NAME;
import static com.scorg.forms.activities.PersonalInfoActivity.PROFILE_ID;
import static com.scorg.forms.preference.AppPreferencesManager.PREFERENCES_KEY.PROFILE_PHOTO;

public class FormsActivity extends AppCompatActivity implements FormFragment.ButtonClickListener, FeedbackFormFragment.ButtonClickListener, HelperResponse, UndertakingFragment.ProfilePhotoUpdater {

    public static final String FORM = "form";
    public static final String FORM_INDEX = "form_index";
    private static final String TAG = "Form";
    private Context mContext;
    private Form form;
    private int formIndex;
    private Intent mIntent = new Intent();

    private boolean isDataUpdated;
    private String patientName;
    private String profileId = "-1";

    @SuppressWarnings("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);
        mContext = FormsActivity.this;

        Form form = getIntent().getParcelableExtra(FORM);
        formIndex = getIntent().getIntExtra(FORM_INDEX, 0);

        patientName = getIntent().getStringExtra(PATIENT_NAME);
        profileId = getIntent().getStringExtra(PROFILE_ID);

        if (form.getFormName().toLowerCase().contains("feedback")) {
            FeedbackFormFragment feedbackFormFragment = FeedbackFormFragment.newInstance(formIndex, form, false, form.getDate());
            addFormFragment(feedbackFormFragment, form.getFormName(), formIndex);
        } else {
            FormFragment formFragment = FormFragment.newInstance(formIndex, form, false, form.getDate(), patientName);
            addFormFragment(formFragment, form.getFormName(), formIndex);
        }

        //-------

        String clinicName = AppPreferencesManager.getString(AppPreferencesManager.CLINIC_KEY.CLINIC_NAME, mContext);
        String clinicAddress = AppPreferencesManager.getString(AppPreferencesManager.CLINIC_KEY.CLINIC_ADDRESS, mContext);
        String clinicLogoSmall = AppPreferencesManager.getString(AppPreferencesManager.CLINIC_KEY.CLINIC_SMALL_LOGO, mContext);

        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(clinicName + (clinicAddress.isEmpty() ? "" : ", ") + clinicAddress);
        //-------
        ImageView logo = findViewById(R.id.logo);

        int iconSize = getResources().getDimensionPixelSize(R.dimen.icon_size);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.override(iconSize, iconSize);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.placeholder(R.drawable.small_app);
        requestOptions.error(R.drawable.small_app);
        requestOptions.skipMemoryCache(true);

        GlideApp.with(FormsActivity.this)
                .load(clinicLogoSmall)
                .apply(requestOptions)
                .into(logo);
        //-------

        ImageView backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void addFormFragment(Fragment formFragment, String formName, int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, formFragment, formName + position);
        transaction.commit();
        Log.d(TAG, "Added " + formName + " fragment");
    }

    @Override
    public void onBackPressed() {
        if (isDataUpdated)
            setResult(RESULT_OK, mIntent);
        super.onBackPressed();
    }


    @Override
    public void backClick(int formNumber) {

    }

    @Override
    public void nextClick(int formNumber) {

    }

    @Override
    public void submitClick(int formNumber, Form form) {
        String mobileNumber = AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.MOBILE, mContext);

        int doctorId = AppPreferencesManager.getInt(AppPreferencesManager.CLINIC_KEY.CLINIC_DOCTOR_ID, mContext);
        int clinicPatId = AppPreferencesManager.getInt(AppPreferencesManager.PREFERENCES_KEY.HOSPITAL_PAT_ID, mContext);

        FormRequest formRequest = new FormRequest();
        Header header = new Header();
        header.setMobileNumber(mobileNumber);
        header.setProfileId(profileId);
        header.setDocId(doctorId);
        header.setHospitalPatId(clinicPatId);

        formRequest.setHeader(header);
        formRequest.setForm(form);

        PatientHelper patientHelper = new PatientHelper(mContext, this);
        patientHelper.postFromData(formRequest);

        this.form = form;
    }


    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        switch (mOldDataTag) {
            case Constants.SAVE_FORM_DATA:
                CommonResponse commonResponse = (CommonResponse) customResponse;
                if (commonResponse.getCommon().isSuccess()) {
                    mIntent.putExtra(FORM, form);
                    mIntent.putExtra(FORM_INDEX, formIndex);

                    isDataUpdated = true;

                    onBackPressed();
                }
                CommonMethods.showToast(mContext, commonResponse.getCommon().getStatusMessage());
                break;
        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {

    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {

    }

    @Override
    public void updateProfilePhoto(String filePath) {
        mIntent.putExtra(PROFILE_PHOTO, filePath);
        isDataUpdated = true;
    }
}
