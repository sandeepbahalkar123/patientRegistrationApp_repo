package com.scorg.forms.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.scorg.forms.R;
import com.scorg.forms.customui.CustomButton;
import com.scorg.forms.customui.CustomTextView;
import com.scorg.forms.models.CommonResponse;
import com.scorg.forms.models.form.Page;
import com.scorg.forms.preference.AppPreferencesManager;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Config;
import com.scorg.forms.util.Constants;
import com.scorg.forms.util.ImageUtil;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;
import static com.scorg.forms.activities.PersonalInfoActivity.PATIENT_NAME;
import static com.scorg.forms.fragments.FormFragment.FORM_NAME;
import static com.scorg.forms.fragments.FormFragment.FORM_NUMBER;
import static com.scorg.forms.fragments.PageFragment.PAGE;
import static com.scorg.forms.fragments.PageFragment.PAGE_NUMBER;

@RuntimePermissions
public class UndertakingFragment extends Fragment {

    private static final String TAG = "Undertaking";
    public static final int MAX_ATTACHMENT_COUNT = 1;
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

    // Content View Elements

    //    private ImageView mLogo;
    private CustomTextView mTitleTextView;
    private CustomTextView mDateTextView;
    private CustomTextView mContentTextView;
    private SignaturePad mSignature_pad;
    private CustomButton mClearButton;
    private TextView mEditButton;
    private TextView mPatientName;

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
                if (!page.getSignatureData().equals("")) {
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

            Glide.with(getActivity())
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

        return rootView;
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickPhoto() {
        /*FilePickerBuilder.getInstance().setMaxCount(MAX_ATTACHMENT_COUNT)
                .setSelectedFiles(new ArrayList<String>())
                .setActivityTheme(R.style.AppTheme)
                .enableVideoPicker(false)
                .enableCameraSupport(true)
                .showGifs(false)
                .showFolderView(true)
                .pickPhoto(UndertakingFragment.this);*/

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getContext(), UndertakingFragment.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UndertakingFragmentPermissionsDispatcher.onRequestPermissionsResult(UndertakingFragment.this, requestCode, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
                if (!data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).isEmpty()) {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();
                    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                    requestOptions.skipMemoryCache(true);
                    requestOptions.error(R.drawable.ic_camera);
                    requestOptions.placeholder(R.drawable.ic_camera);

                    Glide.with(getContext())
                            .load(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).get(0))
                            .apply(requestOptions)
                            .thumbnail(.5f)
                            .into(mProfilePhoto);

                    uploadProfilePhoto(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).get(0));
                }
            }
        }*/

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

                Glide.with(getContext())
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

        try {
            String uploadId = new MultipartUploadRequest(getContext(), uploadUrl)
                    .addFileToUpload(filePath, "profilePhoto")
                    .addParameter("mobileNo", mobileNumber)
                    .addParameter("profileId", profileId)
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

    // Listener
    public interface ProfilePhotoUpdater {
        void updateProfilePhoto(String filePath);
    }

}
