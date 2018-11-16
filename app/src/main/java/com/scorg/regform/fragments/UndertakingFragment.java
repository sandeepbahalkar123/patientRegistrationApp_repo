package com.scorg.regform.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatImageView;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.scorg.regform.R;
import com.scorg.regform.customui.CustomButton;
import com.scorg.regform.customui.CustomTextView;
import com.scorg.regform.customui.FlowLayout;
import com.scorg.regform.customui.FlowRadioGroup;
import com.scorg.regform.models.CommonResponse;
import com.scorg.regform.models.form.Field;
import com.scorg.regform.models.form.Page;
import com.scorg.regform.models.form.ValuesObject;
import com.scorg.regform.preference.AppPreferencesManager;
import com.scorg.regform.util.CommonMethods;
import com.scorg.regform.util.Config;
import com.scorg.regform.util.Constants;
import com.scorg.regform.util.GlideApp;
import com.scorg.regform.util.ImageUtil;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;
import static com.scorg.regform.activities.PersonalInfoActivity.PATIENT_NAME;
import static com.scorg.regform.fragments.FormFragment.FORM_NAME;
import static com.scorg.regform.fragments.FormFragment.FORM_NUMBER;
import static com.scorg.regform.fragments.PageFragment.PAGE;
import static com.scorg.regform.fragments.PageFragment.PAGE_NUMBER;

@RuntimePermissions
public class UndertakingFragment extends Fragment {

    public static final int MAX_ATTACHMENT_COUNT = 1;
    private static final String TAG = "Undertaking";
    private static final String PATIENT_NAME_TO_REPLACE = "patientName";
    private static final String CLINIC_NAME_TO_REPLACE = "clinicName";

    private AppCompatImageView mProfilePhoto;
    private String mReceivedDate;
    private Page page;
    private int pageNumber;
    private int formNumber;
    private String mReceivedFormName;
    private ProfilePhotoUpdater mListener;
    private String patientName;
    //    private OnSubmitListener mListener;
    //    private ImageView mLogo;
    private CustomTextView mTitleTextView;
    private CustomTextView mDateTextView;
    private CustomTextView mContentTextView;

    // Content View Elements
    private SignaturePad mSignature_pad;
    private CustomButton mClearButton;
    private TextView mEditButton;
    private TextView mPatientName;

    private LinearLayout fieldsContainer;

    public UndertakingFragment() {
        // Required empty public constructor
    }

    public static UndertakingFragment newInstance(int formNumber, int pageNumber, Page page, String mReceivedFormName, String patientName) {

        UndertakingFragment fragment = new UndertakingFragment();
        Bundle args = new Bundle();
        args.putInt(FORM_NUMBER, formNumber);
        args.putInt(PAGE_NUMBER, pageNumber);
        args.putParcelable(PAGE, page);
        args.putString(FORM_NAME, mReceivedFormName);
        if (patientName != null)
            args.putString(PATIENT_NAME, patientName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // End Of Content View Elements

    private void bindViews(View view) {
//        mLogo = (ImageView) view.findViewById(R.id.logo);
        mProfilePhoto = view.findViewById(R.id.profilePhoto);
        mTitleTextView = view.findViewById(R.id.titleTextView);
        mDateTextView = view.findViewById(R.id.dateTextView);
        mContentTextView = view.findViewById(R.id.contentTextView);
        mSignature_pad = view.findViewById(R.id.signature_pad);
        mClearButton = view.findViewById(R.id.clearButton);
        mEditButton = view.findViewById(R.id.editButton);
        mPatientName = view.findViewById(R.id.patientName);
        fieldsContainer = view.findViewById(R.id.fieldsContainer);
    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_undertaking, container, false);
        bindViews(rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            mReceivedDate = df.format(c.getTime());

            page = getArguments().getParcelable(PAGE);
            pageNumber = getArguments().getInt(PAGE_NUMBER);
            formNumber = getArguments().getInt(FORM_NUMBER);
            mReceivedFormName = getArguments().getString(FORM_NAME);
            patientName = getArguments().getString(PATIENT_NAME);

            String clinicName = AppPreferencesManager.getString(AppPreferencesManager.CLINIC_KEY.CLINIC_NAME, getContext());
            page.setUndertakingContent(page.getUndertakingContent().replace(CLINIC_NAME_TO_REPLACE, clinicName));

            if (patientName != null) {
                page.setName(patientName);
                page.setUndertakingContent(page.getUndertakingContent().replace(PATIENT_NAME_TO_REPLACE, patientName));
            } else
                page.setUndertakingContent(page.getUndertakingContent().replace(PATIENT_NAME_TO_REPLACE, page.getName()));

            if (page.getSignatureData() != null) {
                if (!page.getSignatureData().isEmpty()) {
                    byte[] decodedString = Base64.decode(page.getSignatureData(), Base64.NO_WRAP);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    mSignature_pad.setSignatureBitmap(decodedByte);
                }
            }

            mDateTextView.setText(getString(R.string.date) + mReceivedDate);

            mContentTextView.setText(Html.fromHtml(page.getUndertakingContent()));

            mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            mTitleTextView.setText(getString(R.string.undertaking));

            Drawable leftDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_photo_camera);
            mEditButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);

            mPatientName.setText(getString(R.string.name) + ": " + page.getName());

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.dontAnimate();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            requestOptions.skipMemoryCache(true);
            requestOptions.error(R.drawable.ic_camera);
            requestOptions.placeholder(R.drawable.ic_camera);

            GlideApp.with(getActivity())
                    .load(page.getUndertakingImageUrl())
                    .apply(requestOptions)
                    .thumbnail(.5f)
                    .into(mProfilePhoto);
        }

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignature_pad.clear();
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profileId = AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.PROFILE_ID, getContext());
                if (profileId.equalsIgnoreCase("-1"))
                    CommonMethods.showToast(getContext(), getResources().getString(R.string.need_to_save_profile));
                else
                    UndertakingFragmentPermissionsDispatcher.onPickPhotoWithCheck(UndertakingFragment.this);
            }
        });

        mSignature_pad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                CommonMethods.log(TAG, "Signing");
            }

            @Override
            public void onSigned() {
                String convertBase64 = ImageUtil.convert(mSignature_pad.getSignatureBitmap());
                CommonMethods.log(TAG, "Signed " + convertBase64);
                page.setSignatureData(convertBase64);
            }

            @Override
            public void onClear() {
                page.setSignatureData("");
                CommonMethods.log(TAG, "Clear Signed");
            }
        });

        // New Added -------------------------------------------------------------

        if (page.getFields() != null) {
            if (!page.getFields().isEmpty()) {
                mPatientName.setVisibility(View.GONE);
                for (int fieldsIndex = 0; fieldsIndex < page.getFields().size(); fieldsIndex++) {
                    final Field field = page.getFields().get(fieldsIndex);
                    addField(fieldsContainer, 0, page.getFields(), field, fieldsIndex, inflater, -1);
                }
            }
        }

        // ----------------------------------------------------------------------

        return rootView;
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickPhoto() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getActivity());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UndertakingFragmentPermissionsDispatcher.onRequestPermissionsResult(UndertakingFragment.this, requestCode, grantResults);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                String filePath = result.getUri().getPath();

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.dontAnimate();
                requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                requestOptions.skipMemoryCache(true);
                requestOptions.error(R.drawable.ic_camera);
                requestOptions.placeholder(R.drawable.ic_camera);

                GlideApp.with(getContext())
                        .load(filePath)
                        .apply(requestOptions)
                        .thumbnail(.5f)
                        .into(mProfilePhoto);

                uploadProfilePhoto(filePath);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }
        }
    }

    private void uploadProfilePhoto(final String filePath) {

        String mobileNumber = AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.MOBILE, getContext());
        String uploadUrl = Config.HTTP + AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, getContext()) + "/" + Config.POST_PROFILE_IMAGE;
        String profileId = AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.PROFILE_ID, getContext());
        int hospitalPatId = AppPreferencesManager.getInt(AppPreferencesManager.PREFERENCES_KEY.HOSPITAL_PAT_ID, getContext());

        try {
            String uploadId = new MultipartUploadRequest(getContext(), uploadUrl)
                    .addFileToUpload(filePath, "profilePhoto")
                    .addHeader("mobileNo", mobileNumber)
                    .addHeader("profileId", profileId)
                    .addHeader("hospitalPatId", String.valueOf(hospitalPatId))
                    .addParameter("mobileNo", mobileNumber) // optional
                    .addParameter("profileId", profileId) // optional
                    .addParameter("hospitalPatId", String.valueOf(hospitalPatId)) // optional
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {

                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                            CommonMethods.log(TAG, serverResponse.getBodyAsString());
                            CommonMethods.showToast(getContext(), "Profile photo uploading failed.");
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            String bodyAsString = serverResponse.getBodyAsString();
                            CommonMethods.log(TAG, bodyAsString);
                            Gson gson = new Gson();
                            CommonResponse commonResponse = gson.fromJson(bodyAsString, CommonResponse.class);
                            if (commonResponse.getCommon().getStatusCode().equals(Constants.SUCCESS)) {
                                page.setUndertakingImageUrl(commonResponse.getPatientData().getProfilePhoto());
                                mListener.updateProfilePhoto(commonResponse.getPatientData().getProfilePhoto());
                            }
                            CommonMethods.showToast(getContext(), commonResponse.getCommon().getStatusMessage());
                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    }).startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfilePhotoUpdater) {
            mListener = (ProfilePhotoUpdater) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ProfilePhotoUpdater");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addField(final View fieldsContainer, final int sectionIndex, final ArrayList<Field> fields, final Field field, final int fieldsIndex, final LayoutInflater inflater, int indexToAddView) {
        switch (field.getType()) {
            case Constants.TYPE.RADIO_BUTTON: {
                View fieldLayout = inflater.inflate(R.layout.field_radiobutton_layout, (LinearLayout) fieldsContainer, false);
                fieldLayout.setId(CommonMethods.generateViewId());
                field.setFieldParentId(fieldLayout.getId());

                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                final FlowRadioGroup radioGroup = fieldLayout.findViewById(R.id.radioGroup);
                radioGroup.setId(CommonMethods.generateViewId());

                final TextView radioGroupError = fieldLayout.findViewById(R.id.radioGroupError);
                radioGroupError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(radioGroupError.getId());

                ArrayList<ValuesObject> dataList = field.getDataList();

                final ValuesObject preValue = field.getValue();

                for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
                    ValuesObject data = dataList.get(dataIndex);
                    RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.radiobutton, radioGroup, false);
                    radioButton.setId(CommonMethods.generateViewId());
                    radioButton.setText(data.getName());
                    radioButton.setTag(data);

                    // set pre value
                    if (field.getValue().getName().equalsIgnoreCase(radioButton.getText().toString()))
                        radioButton.setChecked(true);

                    radioGroup.addView(radioButton);
                }

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        CommonMethods.hideKeyboard(getContext());
                        RadioButton radioButton = group.findViewById(checkedId);
                        ValuesObject valuesObject = (ValuesObject) radioButton.getTag();
                        field.setValue(valuesObject);
                        radioGroupError.setText("");

                        field.setUpdated(!preValue.getName().equalsIgnoreCase(field.getValue().getName()));
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                break;
            }

            case Constants.TYPE.CHECKBOX: {
                View fieldLayout = inflater.inflate(R.layout.field_checkbox_layout, (LinearLayout) fieldsContainer, false);
                fieldLayout.setId(CommonMethods.generateViewId());
                field.setFieldParentId(fieldLayout.getId());

                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                FlowLayout checkBoxGroup = fieldLayout.findViewById(R.id.checkBoxGroup);
                checkBoxGroup.setId(CommonMethods.generateViewId());

                final TextView checkBoxGroupError = fieldLayout.findViewById(R.id.checkBoxGroupError);
                checkBoxGroupError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(checkBoxGroupError.getId());

                ArrayList<ValuesObject> dataList = field.getDataList();
                final ArrayList<ValuesObject> values = field.getValues();
                final ArrayList<ValuesObject> preValues = new ArrayList<>(values);

                for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
                    ValuesObject data = dataList.get(dataIndex);
                    final CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.checkbox, checkBoxGroup, false);

                    // set pre value
                    for (ValuesObject value : values)
                        if (value.getName().equalsIgnoreCase(data.getName()))
                            checkBox.setChecked(true);

                    checkBox.setId(CommonMethods.generateViewId());
                    checkBox.setText(data.getName());
                    checkBox.setTag(data);

                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            CommonMethods.hideKeyboard(getContext());
                            checkBoxGroupError.setText("");

                            // set latest value

                            if (isChecked) {
                                values.add((ValuesObject) checkBox.getTag());
                            } else {
                                values.remove(checkBox.getTag());
                            }

                            Collections.sort(preValues);
                            Collections.sort(values);
                            field.setUpdated(!Arrays.equals(preValues.toArray(), values.toArray()));
                        }
                    });
                    checkBoxGroup.addView(checkBox);
                }

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);

                break;
            }
        }
    }

    // -----------------------------------------------------------------
    // Added

    // Listener
    public interface ProfilePhotoUpdater {
        void updateProfilePhoto(String filePath);
    }

}
