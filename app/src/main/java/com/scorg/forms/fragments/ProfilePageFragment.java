package com.scorg.forms.fragments;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.scorg.forms.R;
import com.scorg.forms.models.form.Field;
import com.scorg.forms.models.form.Form;
import com.scorg.forms.models.form.FormsData;
import com.scorg.forms.models.form.Page;
import com.scorg.forms.models.form.Section;
import com.scorg.forms.models.form.ValuesObject;
import com.scorg.forms.preference.AppPreferencesManager;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Constants;

import java.util.ArrayList;

import static com.scorg.forms.fragments.FormFragment.FORM_NUMBER;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfilePageFragment extends Fragment {

    //    private FormFragment mParentFormFragment;
    private ButtonClickListener mListener;

    public static final int PERSONAL_INFO_FORM = 20;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
//    private static final String PAGE_NUMBER = "section_number";
    private static final String PERSONAL_INFO = "personal_info";
    //    private int pageNumber;
    private int formNumber;
    private FormsData formsData;
    private ImageView profilePhoto;
    public Section sectionHasProfilePhoto;

    public ProfilePageFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfilePageFragment newInstance(int formNumber, FormsData formsData, boolean isNew) {
        ProfilePageFragment fragment = new ProfilePageFragment();
        Bundle args = new Bundle();
        args.putInt(FORM_NUMBER, formNumber);
        args.putParcelable(PERSONAL_INFO, formsData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            formsData = getArguments().getParcelable(PERSONAL_INFO);
//            pageNumber = getArguments().getInt(PAGE_NUMBER);
            formNumber = getArguments().getInt(FORM_NUMBER);
        }
    }

    @SuppressWarnings("CheckResult")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_form_dashboard, container, false);

        //--------
//        configureViewsOfParentFragment();
        //------------

        TextView editButton = rootView.findViewById(R.id.editButton);
        Drawable leftDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_edit);
        editButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.editClick(20);
            }
        });

        /// Form Tab

        TabLayout formTabLayout = rootView.findViewById(R.id.formTabLayout);

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

            View tabView = getLayoutInflater().inflate(R.layout.custom_tab_personal_form, null);

            ImageView formIcon = tabView.findViewById(R.id.formIcon);
            TextView titleTextView = tabView.findViewById(R.id.titleTextView);

            Glide.with(getContext())
                    .load(form.getFormIcon())
                    .apply(requestOptions)
                    .thumbnail(.5f)
                    .into(formIcon);

            titleTextView.setText(form.getFormName());

            TabLayout.Tab customTab = formTabLayout.newTab().setCustomView(tabView);
            formTabLayout.addTab(customTab);
        }

        formTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mListener.openForm(tab, formsData.getForms().get(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mListener.openForm(tab, formsData.getForms().get(tab.getPosition()));
            }
        });

        TextView titleTextView = rootView.findViewById(R.id.titleView);
        titleTextView.setPaintFlags(titleTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        titleTextView.setText(getString(R.string.personal_information));

        LinearLayout sectionsContainer = rootView.findViewById(R.id.sectionsContainer);
//        titleTextView.setText(page.getPageName());

        View sectionLayout = inflater.inflate(R.layout.profile_section_layout, sectionsContainer, false);

        for (int pageIndex = 0; pageIndex < formsData.getPersonalInfo().getPages().size(); pageIndex++) {

            Page page = formsData.getPersonalInfo().getPages().get(pageIndex);

            for (int sectionIndex = 0; sectionIndex < page.getSection().size(); sectionIndex++) {

                Section section = page.getSection().get(sectionIndex);
                if (section.getProfilePhoto() != null) {

                    sectionHasProfilePhoto = section;
                    profilePhoto = sectionLayout.findViewById(R.id.profilePhoto);
                    profilePhoto.setVisibility(View.VISIBLE);

                    setUpdatedProfilePhoto();
                }

                View fieldsContainer = sectionLayout.findViewById(R.id.fieldsContainer);

                ArrayList<Field> fields = section.getFields();

                for (int fieldsIndex = 0; fieldsIndex < fields.size(); fieldsIndex++) {
                    final Field field = fields.get(fieldsIndex);
                    if (field.isIncludeInShortDescription())
                        addField(fieldsContainer, sectionIndex, fields, field, fieldsIndex, inflater, -1);
                }

                TextView mobileText = sectionLayout.findViewById(R.id.mobileText);
                mobileText.setText(AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.MOBILE, getContext()));
                Drawable leftDrawablePhone = AppCompatResources.getDrawable(getContext(), R.drawable.ic_phone_iphone_24dp);
                mobileText.setCompoundDrawablesWithIntrinsicBounds(leftDrawablePhone, null, null, null);

            }
        }

        sectionsContainer.addView(sectionLayout);

        return rootView;
    }

    private void addField(final View fieldsContainer, final int sectionIndex, final ArrayList<Field> fields, final Field field, final int fieldsIndex, final LayoutInflater inflater, int indexToAddView) {
        switch (field.getType()) {

            case Constants.TYPE.TEXT_BOX_GROUP: {

                if (!field.getValue().toString().isEmpty()) {

                    View fieldLayout = inflater.inflate(R.layout.label_layout, (LinearLayout) fieldsContainer, false);
                    TextView labelView = fieldLayout.findViewById(R.id.labelView);
                    labelView.setText(field.getName());
                    TextView textBox = fieldLayout.findViewById(R.id.labelValueText);
                    textBox.setId(CommonMethods.generateViewId());
                    // set pre value
                    textBox.setText(field.getValue().toString());
                    // Add Parent First
                    if (indexToAddView != -1)
                        ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                    else ((LinearLayout) fieldsContainer).addView(fieldLayout);

                    ArrayList<Field> moreFields = field.getTextBoxGroup();

                    for (int moreFieldIndex = 0; moreFieldIndex < moreFields.size(); moreFieldIndex++) {
                        if (indexToAddView != -1)
                            addField(fieldsContainer, sectionIndex, fields, moreFields.get(moreFieldIndex), fieldsIndex, inflater, indexToAddView + moreFieldIndex + 1);
                        else
                            addField(fieldsContainer, sectionIndex, fields, moreFields.get(moreFieldIndex), fieldsIndex, inflater, -1);
                    }
                }

                break;
            }

            case Constants.TYPE.AUTO_COMPLETE: {

                if (!field.getValue().toString().isEmpty()) {

                    View fieldLayout = inflater.inflate(R.layout.label_layout, (LinearLayout) fieldsContainer, false);
                    TextView labelView = fieldLayout.findViewById(R.id.labelView);
                    labelView.setText(field.getName());
                    TextView textBox = fieldLayout.findViewById(R.id.labelValueText);
                    textBox.setId(CommonMethods.generateViewId());
                    // set pre value
                    textBox.setText(field.getValue().toString());

                    if (indexToAddView != -1)
                        ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                    else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                }

                break;
            }

            case Constants.TYPE.TEXT_BOX: {

                if (!field.getValue().getName().isEmpty()) {

                    View fieldLayout = inflater.inflate(R.layout.label_layout, (LinearLayout) fieldsContainer, false);
                    TextView labelView = fieldLayout.findViewById(R.id.labelView);
                    labelView.setText(field.getName());
                    TextView textBox = fieldLayout.findViewById(R.id.labelValueText);
                    textBox.setId(CommonMethods.generateViewId());
                    // set pre value
                    textBox.setText(field.getValue().toString());

                    if (indexToAddView != -1)
                        ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                    else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                }

                break;
            }

            case Constants.TYPE.RADIO_BUTTON: {

                if (!field.getValue().toString().isEmpty()) {

                    View fieldLayout = inflater.inflate(R.layout.label_layout, (LinearLayout) fieldsContainer, false);
                    TextView labelView = fieldLayout.findViewById(R.id.labelView);
                    labelView.setText(field.getName());
                    TextView textBox = fieldLayout.findViewById(R.id.labelValueText);
                    textBox.setId(CommonMethods.generateViewId());
                    // set pre value
                    textBox.setText(field.getValue().toString());

                    if (indexToAddView != -1)
                        ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                    else ((LinearLayout) fieldsContainer).addView(fieldLayout);

                }

                break;
            }
            case Constants.TYPE.CHECKBOX: {

                ArrayList<ValuesObject> dataList = field.getDataList();
                ArrayList<ValuesObject> values = field.getValues();

                StringBuilder valueText = new StringBuilder();

                for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
                    String data = dataList.get(dataIndex).toString();
                    // set pre value
                    for (ValuesObject value : values)
                        if (value.toString().equals(data)) {
                            if (!valueText.toString().isEmpty())
                                valueText.append(", ").append(value);
                            else valueText.append(value);
                        }
                }

                if (!valueText.toString().isEmpty()) {

                    View fieldLayout = inflater.inflate(R.layout.label_layout, (LinearLayout) fieldsContainer, false);
                    TextView labelView = fieldLayout.findViewById(R.id.labelView);
                    labelView.setText(field.getName());
                    TextView textBox = fieldLayout.findViewById(R.id.labelValueText);
                    textBox.setId(CommonMethods.generateViewId());
                    textBox.setText(valueText.toString());

                    if (indexToAddView != -1)
                        ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                    else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                }

                break;
            }
            case Constants.TYPE.DROPDOWN: {

                if (!field.getValue().toString().isEmpty()) {

                    View fieldLayout = inflater.inflate(R.layout.label_layout, (LinearLayout) fieldsContainer, false);
                    TextView labelView = fieldLayout.findViewById(R.id.labelView);
                    labelView.setText(field.getName());
                    TextView textBox = fieldLayout.findViewById(R.id.labelValueText);
                    textBox.setId(CommonMethods.generateViewId());
                    // set pre value
                    textBox.setText(field.getValue().toString());

                    if (indexToAddView != -1)
                        ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                    else ((LinearLayout) fieldsContainer).addView(fieldLayout);

                }

                break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfilePageFragment.ButtonClickListener) {
            mListener = (ProfilePageFragment.ButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ButtonClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setUpdatedProfilePhoto() {

        RequestOptions requestOpt = new RequestOptions();
        requestOpt.dontAnimate();
        requestOpt.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOpt.skipMemoryCache(true);
        requestOpt.error(R.drawable.ic_camera);
        requestOpt.placeholder(R.drawable.ic_camera);

        Glide.with(getContext())
                .load(sectionHasProfilePhoto.getProfilePhoto())
                .apply(requestOpt)
                .thumbnail(.5f)
                .into(profilePhoto);
    }

   /* */

    /**
     * This is use to visible/invisible views in parent FormFragmnet.java.
     * For now, tablayout,footerButton and footer tabs is gone.
     * Dont call this in case not required.
     *//*
    private void configureViewsOfParentFragment() {
        //-----
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof FormFragment) {
            mParentFormFragment = (FormFragment) parentFragment;
            mParentFormFragment.manageProfileFragmentViews();
        }
    }*/

    // Listener
    public interface ButtonClickListener {
        void editClick(int formNumber);

        void openForm(TabLayout.Tab tab, Form form);
    }
}