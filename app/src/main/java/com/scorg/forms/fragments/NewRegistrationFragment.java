package com.scorg.forms.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.scorg.forms.R;
import com.scorg.forms.activities.PersonalInfoActivity;
import com.scorg.forms.preference.AppPreferencesManager;
import com.scorg.forms.util.Valid;

public class NewRegistrationFragment extends Fragment {

    private OnRegistrationListener mListener;

    private EditText mobileText;

    public NewRegistrationFragment() {
        // Required empty public constructor
    }


    public static NewRegistrationFragment newInstance() {
        return new NewRegistrationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_registration, container, false);

        mobileText = rootView.findViewById(R.id.mobileText);
        Button getInfoButton = rootView.findViewById(R.id.getInfoButton);
        TextView clinicTagLineTextView = rootView.findViewById(R.id.clinicTagLine);
        ImageView clinicLogoBigImageView = rootView.findViewById(R.id.clinicLogoBig);
        TextView clinicNameTextView = rootView.findViewById(R.id.clinicName);

        String clinicName = AppPreferencesManager.getString(AppPreferencesManager.CLINIC_KEY.CLINIC_NAME, getContext());
        String clinicAddress = AppPreferencesManager.getString(AppPreferencesManager.CLINIC_KEY.CLINIC_ADDRESS, getContext());
        String clinicLogoBig = AppPreferencesManager.getString(AppPreferencesManager.CLINIC_KEY.CLINIC_BIG_LOGO, getContext());
        String clinicTagLine = AppPreferencesManager.getString(AppPreferencesManager.CLINIC_KEY.CLINIC_TAG_LINE, getContext());
        String clinicLogSmall = AppPreferencesManager.getString(AppPreferencesManager.CLINIC_KEY.CLINIC_SMALL_LOGO, getContext());

        if (clinicTagLine.isEmpty())
            clinicTagLineTextView.setVisibility(View.GONE);
        else
            clinicTagLineTextView.setText(clinicTagLine);

        if (clinicName.isEmpty())
            clinicNameTextView.setVisibility(View.GONE);
        else
            clinicNameTextView.setText(clinicName + (clinicAddress.isEmpty() ? "" : ", ") + clinicAddress);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.placeholder(R.drawable.imgpsh_fullsize);
        requestOptions.error(R.drawable.imgpsh_fullsize);

        Glide.with(getContext())
                .load(clinicLogoBig)
                .apply(requestOptions)
                .into(clinicLogoBigImageView);


        getInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go();
            }
        });

        mobileText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_GO)
                    go();
                return false;
            }

        });

        return rootView;
    }


    private void go() {
        String mobile = mobileText.getText().toString().trim();
        if (Valid.validateMobileNo(mobile, getContext(), true))
            mListener.onClickGetInfo(mobile);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnRegistrationListener) {
            mListener = (OnRegistrationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRegistrationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof PersonalInfoActivity)
            ((PersonalInfoActivity) getActivity()).setMenuVisibility(false);
        mListener = null;
    }

    public void resetMobile() {
        mobileText.setText("");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRegistrationListener {
        void onClickGetInfo(String mobileText);
    }
}
