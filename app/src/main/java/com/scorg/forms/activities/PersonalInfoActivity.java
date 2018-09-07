package com.scorg.forms.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.scorg.forms.R;
import com.scorg.forms.customui.CustomProgressDialog;
import com.scorg.forms.database.AppDBHelper;
import com.scorg.forms.fragments.ContainerFragment;
import com.scorg.forms.fragments.FormFragment;
import com.scorg.forms.fragments.NewRegistrationFragment;
import com.scorg.forms.fragments.ProfilePageFragment;
import com.scorg.forms.helpers.LoginHelper;
import com.scorg.forms.helpers.PatientHelper;
import com.scorg.forms.interfaces.CheckIpConnection;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.interfaces.HelperResponse;
import com.scorg.forms.models.CommonResponse;
import com.scorg.forms.models.PatientData;
import com.scorg.forms.models.form.Field;
import com.scorg.forms.models.form.Form;
import com.scorg.forms.models.form.FormsData;
import com.scorg.forms.models.form.request.FieldRequest;
import com.scorg.forms.models.form.request.FormRequest;
import com.scorg.forms.models.form.request.Header;
import com.scorg.forms.models.login.IpTestResponseModel;
import com.scorg.forms.models.profile.ProfileData;
import com.scorg.forms.models.profile.ProfileList;
import com.scorg.forms.models.profile.ProfilesModel;
import com.scorg.forms.preference.AppPreferencesManager;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Constants;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.scorg.forms.activities.FormsActivity.FORM;
import static com.scorg.forms.activities.FormsActivity.FORM_INDEX;
import static com.scorg.forms.fragments.ProfilePageFragment.PERSONAL_INFO_FORM;
import static com.scorg.forms.preference.AppPreferencesManager.PREFERENCES_KEY.PROFILE_PHOTO;

public class PersonalInfoActivity extends AppCompatActivity implements FormFragment.ButtonClickListener, NewRegistrationFragment.OnRegistrationListener, ProfilePageFragment.ButtonClickListener, ContainerFragment.OnContainerLoadListener, HelperResponse {

    private static final String TAG = "PersonalInfo";
    private static final int FORM_REQUEST = 25454;
    public static final String PATIENT_NAME = "patient_name";
    public static final String PROFILE_ID = "profile_id";
    private RelativeLayout bottomTabLayout;

    private String[] relationArray = {"SELF", "CHILD", "SPOUSE", "FATHER", "MOTHER", "BROTHER", "SISTER"};

    // show
    private CustomProgressDialog customProgressDialog;
    private Timer timer = new Timer();
    private TabLayout formTabLayout;
    private Context mContext;
    private LoginHelper mLoginHelper;

    private Menu menu;
    private FormsData formsData;
    private PatientHelper mPatientHelper;

    private String mobileText;
    private ImageView smallLogo;
    private TextView toolbarTitleTextView;

    private FormFragment formFragment;
    private ProfilePageFragment profilePageFragment;
    private ContainerFragment containerFragment;
    private NewRegistrationFragment newRegistrationFragment;

    private String patientName;
    private String profileId = "-1";

    @SuppressWarnings("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = PersonalInfoActivity.this;
        mPatientHelper = new PatientHelper(mContext, this);
        mLoginHelper = new LoginHelper(mContext);

        smallLogo = findViewById(R.id.logo);
        toolbarTitleTextView = findViewById(R.id.toolbarTitleTextView);

        String clinicName = AppPreferencesManager.getString(AppPreferencesManager.CLINIC_KEY.CLINIC_NAME, mContext);
        String clinicLogoSmall = AppPreferencesManager.getString(AppPreferencesManager.CLINIC_KEY.CLINIC_SMALL_LOGO, mContext);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.error(R.drawable.ic_small_icon_default);
        requestOptions.placeholder(R.drawable.ic_small_icon_default);

        Glide.with(mContext)
                .load(clinicLogoSmall)
                .apply(requestOptions)
                .into(smallLogo);

        toolbarTitleTextView.setText(clinicName);

      /*  if (Device.getInstance(mContext).getDeviceType().equals(PHONE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.tablet_message))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }*/

        customProgressDialog = new CustomProgressDialog(mContext);
        customProgressDialog.setCancelable(false);

        bottomTabLayout = findViewById(R.id.bottomTabLayout);

        addRegisterFragment();

    }

    private void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getString(R.string.logout_message))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout();
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

    private void logout() {
        AppDBHelper appDBHelper = new AppDBHelper(mContext);
        appDBHelper.deleteDatabase();

        String ipConfig = AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, mContext);
        AppPreferencesManager.clearSharedPref(mContext);
        AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, ipConfig, mContext);
        AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.IS_VALID_IP_CONFIG, Constants.TRUE, mContext);

        Intent intentObj = new Intent(mContext, LoginActivity.class);
        startActivity(intentObj);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            showAlert(getResources().getString(R.string.back_to_mobile_screen_message));
        else
            showAlert(getResources().getString(R.string.exit_message));
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PersonalInfoActivity.super.onBackPressed();
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

    private void addRegisterFragment() {
        newRegistrationFragment = NewRegistrationFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newRegistrationFragment, getResources().getString(R.string.new_registration));
        transaction.commit();
        CommonMethods.hideKeyboard(mContext);
    }

    private void addContainerFragment() {
        containerFragment = ContainerFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, containerFragment, getResources().getString(R.string.container_fragment));
        transaction.addToBackStack(getResources().getString(R.string.container_fragment));
        transaction.commit();
        CommonMethods.hideKeyboard(mContext);
    }

    private void addFormFragment() {
        formFragment = FormFragment.newInstance(PERSONAL_INFO_FORM, formsData.getPersonalInfo(), !formsData.isRegisteredUser(), "", patientName);
        FragmentTransaction transaction = containerFragment.getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.profileContainer, formFragment, getResources().getString(R.string.personal_info) + PERSONAL_INFO_FORM);
        transaction.commit();

        CommonMethods.hideKeyboard(mContext);
    }

    private void addProfileFragment() {
        setTabLayoutDisable(false, true);
        bottomTabLayout.setVisibility(GONE);
        showProgress(GONE);

        profilePageFragment = ProfilePageFragment.newInstance(PERSONAL_INFO_FORM, formsData, !formsData.isRegisteredUser());
        FragmentTransaction transaction = containerFragment.getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.profileContainer, profilePageFragment, getResources().getString(R.string.personal_info) + PERSONAL_INFO_FORM);
        transaction.commit();

        CommonMethods.hideKeyboard(mContext);
    }

    @Override
    public void openForm(TabLayout.Tab tab, Form form) {
        Intent intent = new Intent(mContext, FormsActivity.class);
        intent.putExtra(FORM, form);
        intent.putExtra(FORM_INDEX, tab.getPosition());
        if (patientName != null)
            intent.putExtra(PATIENT_NAME, patientName);
        intent.putExtra(PROFILE_ID, profileId);
        startActivityForResult(intent, FORM_REQUEST);
    }

    /*public String loadJSONFromAsset(String jsonFile) {
        String json;
        try {
            InputStream is = getAssets().open(jsonFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }*/

    @Override
    public void backClick(int formNumber) {

    }

    @Override
    public void nextClick(int formNumber) {

    }

    @Override
    public void submitClick(int formNumber, Form personalInfo) {

        if (profileId.equals("-1") || personalInfo.getRelation() == null)
            selectRelationDialog(formNumber, personalInfo);
        else
            postPersonalInfo(profileId, formNumber, personalInfo);


       /* // call api with Updated fields.
        FormRequest formRequest = new FormRequest();
        List<FieldRequest> fields = formRequest.getFields();

        for (Page page : form.getPages()) {
            for (Field field : page.getFields()) {
                addFields(fields, field);
            }

            for (Section section : page.getSection()) {
                for (Field field : section.getFields()) {
                    addFields(fields, field);
                }
            }
        }

        String mobileNumber = AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.MOBILE, mContext);

        Header header = new Header();
        header.setForm(form.getFormName());
        header.setMobileNo(mobileNumber);
        formRequest.setHeader(header);

        Gson gson = new Gson();
        String jsonString = gson.toJson(formRequest);
        CommonMethods.log(TAG, jsonString);

        if (!formRequest.getFields().isEmpty())
            patientHelper.postPersonalInfo(formRequest);
        else CommonMethods.showToast(mContext, getString(R.string.nothing_updated));*/
    }

    private void postPersonalInfo(String profileId, int formNumber, Form personalInfo) {

        FormRequest formRequest = new FormRequest();
        Header header = new Header();
        header.setMobileNumber(mobileText);
        header.setProfileId(profileId);

        formRequest.setHeader(header);
        formRequest.setPersonalInfo(personalInfo);

        mPatientHelper.postPersonalInfo(formRequest);
    }

    private void addFields(List<FieldRequest> fields, Field field) {

        if (field.isUpdated()) {
            FieldRequest fieldRequest = new FieldRequest();

            fieldRequest.setKey(field.getKey());

            switch (field.getType()) {
                case Constants.TYPE.RATING_BAR:
                    fieldRequest.setRating(field.getRating());
                    fieldRequest.setValue(null);
                    fieldRequest.setTextValue(field.getTextValue());
                    fieldRequest.setValues(null);
                    break;
                case Constants.TYPE.CHECKBOX:
                    fieldRequest.setRating(null);
                    fieldRequest.setValue(null);
                    fieldRequest.setTextValue(null);
                    fieldRequest.setValues(field.getValues());
                    break;
                default:
                    fieldRequest.setRating(null);
                    fieldRequest.setValue(field.getValue());
                    fieldRequest.setTextValue(null);
                    fieldRequest.setValues(null);
                    break;
            }

            fields.add(fieldRequest);
        }

        for (Field fieldMore : field.getTextBoxGroup())
            addFields(fields, fieldMore);
    }

    private void setTabLayoutDisable(boolean isDisable, boolean showFormTabLayout) {

        if (showFormTabLayout)
            formTabLayout.setVisibility(VISIBLE);
        else
            formTabLayout.setVisibility(View.GONE);

        LinearLayout tabStrip = ((LinearLayout) formTabLayout.getChildAt(0));
        tabStrip.setAlpha(isDisable ? .3f : 1f);
        tabStrip.setEnabled(isDisable);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(!isDisable);
        }
    }

    @Override
    public void editClick(int formNumber) {
        showProgress(VISIBLE);
        setTabLayoutDisable(true, false);
        addFormFragment();
    }


    @Override
    public void onClickGetInfo(String mobileText) {
        this.mobileText = mobileText;
        mPatientHelper.getPatientProfileInfo(mobileText);
    }

    private void showProgress(final int visible) {
        customProgressDialog.show();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // dismiss
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bottomTabLayout.setVisibility(visible);
                    }
                });
                customProgressDialog.dismiss();
            }
        }, 300);
    }

    // Menu

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting_menu, menu);

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.change_ip:
                CommonMethods.showAlertDialog(mContext, getString(R.string.server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
                    @Override
                    public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                        AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                        mLoginHelper.checkConnectionToServer(serverPath);
                        dialog.dismiss();
                    }
                }, false);
                return true;
            case R.id.logout:
                logoutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {

        switch (mOldDataTag) {
            case Constants.TASK_CHECK_SERVER_CONNECTION:
                IpTestResponseModel ipTestResponseModel = (IpTestResponseModel) customResponse;
                if (ipTestResponseModel.getCommon().isSuccess()) {
                    AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.IS_VALID_IP_CONFIG, Constants.TRUE, mContext);
                    Intent intentObj = new Intent(mContext, LoginActivity.class);
                    startActivity(intentObj);
                    finish();
                } else {
                    AppPreferencesManager.putString(Constants.LOGIN_SUCCESS, Constants.FALSE, mContext);
                    CommonMethods.showAlertDialog(mContext, getString(R.string.wrong_server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
                        @Override
                        public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                            AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                            mLoginHelper.checkConnectionToServer(serverPath);
                        }
                    }, false);
                }
                break;
            case Constants.GET_PROFILE_INFO:
                ProfilesModel profilesModel = (ProfilesModel) customResponse;
                if (profilesModel.getCommon().isSuccess()) {
                    ProfileList data = profilesModel.getData();
                    if (data.isRegisteredUser())
                        selectProfileDialog(data);
                    else {
                        profileId = "-1";
                        mPatientHelper.getPatientProfile(mobileText, profileId);
                    }
                }
                break;
            case Constants.GET_PROFILE:
                PatientData patientData = (PatientData) customResponse;
                if (patientData.getCommon().isSuccess()) {
                    formsData = patientData.getData();
                    newRegistrationFragment.resetMobile();
                    addContainerFragment();
                }
                break;
            case Constants.POST_PERSONAL_DATA:
                CommonResponse commonResPersonal = (CommonResponse) customResponse;
                if (commonResPersonal.getCommon().isSuccess()) {
                    patientName = commonResPersonal.getPatientData().getPatientName();
                    profileId = commonResPersonal.getPatientData().getProfileId();
                    AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.PROFILE_ID, profileId, mContext);
                    addProfileFragment();
                } else
                    CommonMethods.showToast(mContext, commonResPersonal.getCommon().getStatusMessage());
                break;
        }
    }

    private void selectProfileDialog(ProfileList profilesModel) {

        final Dialog dialog = new Dialog(PersonalInfoActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choose_profile_dialog);

        final RadioGroup radioGroup = dialog.findViewById(R.id.radioGroupLayout);
        radioGroup.removeAllViews();

        for (ProfileData profileData : profilesModel.getProfiles()) {
            RadioButton radioButton = new RadioButton(mContext);
            int paddingInPixel = getResources().getDimensionPixelSize(R.dimen.dp12);
            radioButton.setPadding(paddingInPixel, paddingInPixel, paddingInPixel, paddingInPixel);
            radioButton.setId(CommonMethods.generateViewId());
            radioButton.setTag(profileData);
            radioButton.setText(profileData.getRelation());

            if (profileData.getRelation().equalsIgnoreCase("new user"))
                radioButton.setTypeface(null, Typeface.BOLD);

            radioGroup.addView(radioButton);
        }

        // bindview
        final Button okButton = dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    RadioButton radioB = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                    ProfileData profileD = (ProfileData) radioB.getTag();
                    profileId = profileD.getProfileId();
                    mPatientHelper.getPatientProfile(mobileText, profileId);
                    dialog.dismiss();
                } else
                    CommonMethods.showToast(okButton.getContext(), getResources().getString(R.string.select_at_least_one_profile));
            }
        });

        dialog.show();
    }

    private void selectRelationDialog(final int formNumber, final Form personalInfo) {

        final Dialog dialog = new Dialog(PersonalInfoActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choose_profile_dialog);

        final RadioGroup radioGroup = dialog.findViewById(R.id.radioGroupLayout);
        radioGroup.removeAllViews();

        for (String relation : relationArray) {
            RadioButton radioButton = new RadioButton(mContext);
            int paddingInPixel = getResources().getDimensionPixelSize(R.dimen.dp10);
            radioButton.setPadding(paddingInPixel, paddingInPixel, paddingInPixel, paddingInPixel);
            radioButton.setId(CommonMethods.generateViewId());
            radioButton.setText(relation);
            radioGroup.addView(radioButton);
        }

        // bindview
        final Button okButton = dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    RadioButton radioB = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                    String profileRelation = (String) radioB.getText();
                    personalInfo.setRelation(profileRelation);
                    postPersonalInfo(profileId, formNumber, personalInfo);
                    dialog.dismiss();
                } else
                    CommonMethods.showToast(okButton.getContext(), getResources().getString(R.string.select_at_least_one_profile));
            }
        });

        dialog.show();
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        if (mOldDataTag.equals(Constants.TASK_CHECK_SERVER_CONNECTION)) {
            AppPreferencesManager.putString(Constants.LOGIN_SUCCESS, Constants.FALSE, mContext);
            CommonMethods.showAlertDialog(mContext, getString(R.string.wrong_server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
                @Override
                public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                    AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                    mLoginHelper.checkConnectionToServer(serverPath);
                }
            }, false);
        }
    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        if (mOldDataTag.equals(Constants.TASK_CHECK_SERVER_CONNECTION)) {
            AppPreferencesManager.putString(Constants.LOGIN_SUCCESS, Constants.FALSE, mContext);
            CommonMethods.showAlertDialog(mContext, getString(R.string.wrong_server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
                @Override
                public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                    AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                    mLoginHelper.checkConnectionToServer(serverPath);
                }
            }, false);
        }
    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        if (mOldDataTag.equals(Constants.TASK_CHECK_SERVER_CONNECTION)) {
            AppPreferencesManager.putString(Constants.LOGIN_SUCCESS, Constants.FALSE, mContext);
            CommonMethods.showAlertDialog(mContext, getString(R.string.wrong_server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
                @Override
                public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                    AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                    mLoginHelper.checkConnectionToServer(serverPath);
                }
            }, false);
        }
    }

    public void setMenuVisibility(boolean isVisible) {
        for (int i = 0; i < menu.size(); i++)
            menu.getItem(i).setVisible(isVisible);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FORM_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                if (data.getParcelableExtra(FORM) != null) {
                    int formIndex = data.getIntExtra(FORM_INDEX, 0);
                    formsData.getForms().set(formIndex, (Form) data.getParcelableExtra(FORM));
                }

                if (data.getStringExtra(PROFILE_PHOTO) != null) {
                    if (profilePageFragment != null) {
                        if (profilePageFragment.sectionHasProfilePhoto != null)
                            profilePageFragment.sectionHasProfilePhoto.setProfilePhoto(data.getStringExtra(PROFILE_PHOTO));
                        profilePageFragment.setUpdatedProfilePhoto();
                    }
                }
            }
        }

        if (formFragment != null)
            formFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void generateForm() {
        // Form Tab
        formTabLayout = findViewById(R.id.formTabLayout);

        int iconSize = getResources().getDimensionPixelSize(R.dimen.icon_size);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.override(iconSize, iconSize);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.error(R.drawable.ic_assignment);
        requestOptions.placeholder(R.drawable.ic_assignment);

        for (int formIndex = 0; formIndex < formsData.getForms().size(); formIndex++) {

            Form form = formsData.getForms().get(formIndex);

            View tabView = getLayoutInflater().inflate(R.layout.custom_tab_form, null);

            ImageView formIcon = tabView.findViewById(R.id.formIcon);
            TextView titleTextView = tabView.findViewById(R.id.titleTextView);

            Glide.with(mContext)
                    .load(form.getFormIcon())
                    .apply(requestOptions)
                    .into(formIcon);

            titleTextView.setText(form.getFormName());

            TabLayout.Tab customTab = formTabLayout.newTab().setCustomView(tabView);
            formTabLayout.addTab(customTab);
        }

        formTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                openForm(tab, formsData.getForms().get(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                openForm(tab, formsData.getForms().get(tab.getPosition()));
            }
        });

        if (formsData.isRegisteredUser())
            addProfileFragment();
        else addFormFragment();
    }
}
