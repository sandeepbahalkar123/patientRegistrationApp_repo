package com.scorg.forms.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.scorg.forms.R;
import com.scorg.forms.customui.FlowLayout;
import com.scorg.forms.customui.FlowRadioGroup;
import com.scorg.forms.helpers.PatientHelper;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.interfaces.HelperResponse;
import com.scorg.forms.models.CommonResponse;
import com.scorg.forms.models.form.Field;
import com.scorg.forms.models.form.Page;
import com.scorg.forms.models.form.Section;
import com.scorg.forms.models.master.MasterDataModel;
import com.scorg.forms.models.master.request.MasterDataRequest;
import com.scorg.forms.preference.AppPreferencesManager;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Config;
import com.scorg.forms.util.Constants;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;
import static com.scorg.forms.fragments.FormFragment.FORM_NAME;
import static com.scorg.forms.fragments.FormFragment.FORM_NUMBER;
import static com.scorg.forms.fragments.ProfilePageFragment.PERSONAL_INFO_FORM;
import static com.scorg.forms.util.Constants.DATE_PATTERN.DD_MM_YYYY;

/**
 * A placeholder fragment containing a simple view.
 */
@RuntimePermissions
public class PageFragment extends Fragment implements HelperResponse {

    private static final String TAG = "PageFragment";
    private DatePickerDialog datePickerDialog;
    private TextView mTitleTextView;
    private LinearLayout mSectionsContainer;
    private LayoutInflater mInflater;
    private String mReceivedFormName;

    private ImageView profilePhoto;
    private TextView profilePhotoEditButton;
    private Section sectionHasProfilePhoto;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String PAGE_NUMBER = "section_number";
    public static final String PAGE = "page";
    private int pageNumber;
    private int formNumber;
    private Page page;
    private PatientHelper patientHelper;
    //    private HashMap<String, ArrayList<String>> tableDataList = new HashMap<>();
    private Field tempField;

    public PageFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PageFragment newInstance(int formNumber, int pageNumber, Page page, String mReceivedFormName) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(FORM_NUMBER, formNumber);
        args.putInt(PAGE_NUMBER, pageNumber);
        args.putParcelable(PAGE, page);
        args.putString(FORM_NAME, mReceivedFormName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page = getArguments().getParcelable(PAGE);
            pageNumber = getArguments().getInt(PAGE_NUMBER);
            formNumber = getArguments().getInt(FORM_NUMBER);
            mReceivedFormName = getArguments().getString(FORM_NAME);
        }
    }

    @SuppressWarnings("CheckResult")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pages, container, false);

        mInflater = inflater;
        mTitleTextView = rootView.findViewById(R.id.titleView);
        mSectionsContainer = rootView.findViewById(R.id.sectionsContainer);

        patientHelper = new PatientHelper(getContext(), PageFragment.this);

        initializeDataViews();
        return rootView;
    }

    private void initializeDataViews() {

        if (formNumber == PERSONAL_INFO_FORM)
            mTitleTextView.setText(page.getPageName());
        else mTitleTextView.setText(mReceivedFormName + ": " + page.getPageName());

        for (int sectionIndex = 0; sectionIndex < page.getSection().size(); sectionIndex++) {
            View sectionLayout = mInflater.inflate(R.layout.section_layout, mSectionsContainer, false);

            Section section = page.getSection().get(sectionIndex);

            if (section.getProfilePhoto() != null) {

                sectionHasProfilePhoto = section;

                View profilePhotoLayout = sectionLayout.findViewById(R.id.profilePhotoLayout);
                profilePhoto = sectionLayout.findViewById(R.id.profilePhoto);
                profilePhotoEditButton = sectionLayout.findViewById(R.id.editButton);

                profilePhotoEditButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String profileId = AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.PROFILE_ID, getContext());
                        if (profileId.equalsIgnoreCase("-1"))
                            CommonMethods.showToast(getContext(), getResources().getString(R.string.need_to_save_profile));
                        else
                            PageFragmentPermissionsDispatcher.onPickPhotoWithCheck(PageFragment.this);
                    }
                });

                Drawable leftDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_photo_camera);
                profilePhotoEditButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);

                TextView mobileText = sectionLayout.findViewById(R.id.mobileText);
                mobileText.setText(AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.MOBILE, getContext()));
                Drawable leftDrawablePhone = AppCompatResources.getDrawable(getContext(), R.drawable.ic_phone_iphone_24dp);
                mobileText.setCompoundDrawablesWithIntrinsicBounds(leftDrawablePhone, null, null, null);
                profilePhotoLayout.setVisibility(View.VISIBLE);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.dontAnimate();
                requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                requestOptions.skipMemoryCache(true);
                requestOptions.error(R.drawable.ic_camera);
                requestOptions.placeholder(R.drawable.ic_camera);

                Glide.with(getContext())
                        .load(section.getProfilePhoto())
                        .apply(requestOptions)
                        .thumbnail(.5f)
                        .into(profilePhoto);
            } else {
                View profilePhotoLayout = sectionLayout.findViewById(R.id.profilePhotoLayout);
                profilePhotoLayout.setVisibility(View.GONE);
            }

            View fieldsContainer = sectionLayout.findViewById(R.id.fieldsContainer);

            ArrayList<Field> fields = section.getFields();

            TextView sectionTitle = sectionLayout.findViewById(R.id.sectionTitleView);
            sectionTitle.setText(section.getSectionName());

            for (int fieldsIndex = 0; fieldsIndex < fields.size(); fieldsIndex++) {
                final Field field = fields.get(fieldsIndex);
                addField(fieldsContainer, sectionIndex, fields, field, fieldsIndex, mInflater, -1);
            }
            mSectionsContainer.addView(sectionLayout);
        }
    }

    private void addField(final View fieldsContainer, final int sectionIndex, final ArrayList<Field> fields, final Field field, final int fieldsIndex, final LayoutInflater inflater, int indexToAddView) {
        switch (field.getType()) {

            case Constants.TYPE.TEXT_BOX_GROUP: {
                // Added Extended Layout
                final View fieldLayout = inflater.inflate(R.layout.field_autocomplete_textbox_layout, (LinearLayout) fieldsContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                final AutoCompleteTextView textBox = fieldLayout.findViewById(R.id.editText);
                textBox.setId(CommonMethods.generateViewId());
                field.setFieldId(textBox.getId());

                if (!field.getHint().equalsIgnoreCase(""))
                    textBox.setHint(field.getHint());

                final TextView editTextError = fieldLayout.findViewById(R.id.editTextError);
                editTextError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(editTextError.getId());

                final ArrayList<String> dataList = field.getDataList();
                final ArrayList<String> dataListTemp = field.getDataListTemp();

                if (!dataList.isEmpty() && dataListTemp.isEmpty())
                    dataListTemp.addAll(dataList);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, dataListTemp);
                textBox.setAdapter(adapter);

                // set pre value
                textBox.setText(field.getValue());
                final String preValue = field.getValue();

                if (field.getLength() > 0) {
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(field.getLength());
                    textBox.setFilters(fArray);
                }

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

                textBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        editTextError.setText("");
                        textBox.setBackgroundResource(R.drawable.edittext_selector);
                        // set latest value
                        field.setValue(String.valueOf(editable).trim());

                        field.setUpdated(!preValue.equalsIgnoreCase(field.getValue()));
                    }
                });


                textBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (hasFocus) {
                            if (!field.getDataTable().isEmpty() && dataListTemp.isEmpty()) {
                                tempField = field;
                                MasterDataRequest masterDataRequest = new MasterDataRequest();
                                masterDataRequest.setDataTable(field.getDataTable());
                                patientHelper.getMasterData(masterDataRequest);
                            }
                        }
                    }
                });

                ImageView plusButton = fieldLayout.findViewById(R.id.plusButton);

                plusButton.setVisibility(View.VISIBLE);
                plusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            final Field fieldGroupNew = (Field) field.clone();
                            fields.add(fields.indexOf(field) + 1, fieldGroupNew);
                            fieldGroupNew.setValue("");
                            fieldGroupNew.setMandatory(false);

                            ArrayList<Field> clonedMoreFields = new ArrayList<Field>();
                            for (Field field1 :
                                    field.getTextBoxGroup()) {
                                Field cloneField = (Field) field1.clone();
                                cloneField.setValue("");
                                cloneField.setMandatory(false);
                                clonedMoreFields.add(cloneField);
                            }

                            fieldGroupNew.setTextBoxGroup(clonedMoreFields);

                            addField(fieldsContainer, sectionIndex, fields, fieldGroupNew, fieldsIndex, inflater, ((LinearLayout) fieldLayout.getParent()).indexOfChild(fieldLayout) + 1 + fieldGroupNew.getTextBoxGroup().size());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;
            }

            case Constants.TYPE.AUTO_COMPLETE: {
                // Added Extended Layout
                final View fieldLayout = inflater.inflate(R.layout.field_autocomplete_textbox_layout, (LinearLayout) fieldsContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                final AutoCompleteTextView textBox = fieldLayout.findViewById(R.id.editText);
                RelativeLayout.LayoutParams textBoxParams = (RelativeLayout.LayoutParams) textBox.getLayoutParams();
                textBox.setId(CommonMethods.generateViewId());
                field.setFieldId(textBox.getId());

                if (!field.getHint().equalsIgnoreCase(""))
                    textBox.setHint(field.getHint());

                final TextView editTextError = fieldLayout.findViewById(R.id.editTextError);
                editTextError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(editTextError.getId());

                // set pre value
                textBox.setText(field.getValue());
                final String preValue = field.getValue();

                if (field.getLength() > 0) {
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(field.getLength());
                    textBox.setFilters(fArray);
                }

                final ArrayList<String> dataList = field.getDataList();
                final ArrayList<String> dataListTemp = field.getDataListTemp();

                if (!dataList.isEmpty() && dataListTemp.isEmpty())
                    dataListTemp.addAll(dataList);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, dataListTemp);
                textBox.setAdapter(adapter);

                textBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        editTextError.setText("");
                        textBox.setBackgroundResource(R.drawable.edittext_selector);
                        // set latest value
                        field.setValue(String.valueOf(editable).trim());

                        field.setUpdated(!preValue.equalsIgnoreCase(field.getValue()));
                    }
                });

                textBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                        if (arg1 == EditorInfo.IME_ACTION_DONE)
                            CommonMethods.hideKeyboard(getContext());
                        return false;
                    }
                });

                textBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (hasFocus) {
                            if (!field.getDataTable().isEmpty() && dataListTemp.isEmpty()) {
                                tempField = field;
                                MasterDataRequest masterDataRequest = new MasterDataRequest();
                                masterDataRequest.setDataTable(field.getDataTable());
                                patientHelper.getMasterData(masterDataRequest);
                            }
                        }
                    }
                });

                switch (field.getInputType()) {
                    case Constants.INPUT_TYPE.EMAIL:
                        textBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;
                    case Constants.INPUT_TYPE.MOBILE:
                        textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.mobile_size);
                        textBox.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    case Constants.INPUT_TYPE.NAME:
                        textBox.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                        break;
                    case Constants.INPUT_TYPE.PIN_CODE:
                        textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.pincode_size);
                        textBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                    case Constants.INPUT_TYPE.TEXT_BOX_BIG:
                        textBox.setSingleLine(false);
                        textBox.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        textBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        textBox.setMaxLines(10);
                        break;
                    case Constants.INPUT_TYPE.NUMBER:
                        textBox.setGravity(Gravity.END);
                        textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.number_size);
                        textBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                }

                ImageView plusButton = fieldLayout.findViewById(R.id.plusButton);
                plusButton.setVisibility(View.GONE);

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);

                break;
            }

            case Constants.TYPE.TEXT_BOX: {
                View fieldLayout = inflater.inflate(R.layout.field_textbox_layout, (LinearLayout) fieldsContainer, false);

                final TextView labelView = fieldLayout.findViewById(R.id.labelView);

                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                final EditText textBox = fieldLayout.findViewById(R.id.editText);
                LinearLayout.LayoutParams textBoxParams = (LinearLayout.LayoutParams) textBox.getLayoutParams();
                textBox.setId(CommonMethods.generateViewId());

                textBox.setEnabled(field.isEditable());

                if (!field.getHint().equals(""))
                    textBox.setHint(field.getHint());

                field.setFieldId(textBox.getId());

                final TextView editTextError = fieldLayout.findViewById(R.id.editTextError);
                editTextError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(editTextError.getId());

                // set pre value
                textBox.setText(field.getValue());
                final String preValue = field.getValue();

                if (field.getLength() > 0) {
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(field.getLength());
                    textBox.setFilters(fArray);
                }

                switch (field.getInputType()) {
                    case Constants.INPUT_TYPE.EMAIL:
                        textBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;
                    case Constants.INPUT_TYPE.DATE:
                        textBox.setCursorVisible(false);
                        textBox.setFocusableInTouchMode(false);
                        textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.date_size);

                        textBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int year;
                                int month;
                                int day;

                                if (field.getValue().isEmpty()) {
                                    Calendar now = Calendar.getInstance();
                                    year = now.get(Calendar.YEAR);
                                    month = now.get(Calendar.MONTH);
                                    day = now.get(Calendar.DAY_OF_MONTH);
                                } else {

                                    if (CommonMethods.getFormattedDate(field.getValue(), DD_MM_YYYY, "yyyy").isEmpty()) {
                                        Calendar now = Calendar.getInstance();
                                        year = now.get(Calendar.YEAR);
                                        month = now.get(Calendar.MONTH);
                                        day = now.get(Calendar.DAY_OF_MONTH);
                                    } else {
                                        year = Integer.parseInt(CommonMethods.getFormattedDate(field.getValue(), DD_MM_YYYY, "yyyy"));
                                        month = Integer.parseInt(CommonMethods.getFormattedDate(field.getValue(), DD_MM_YYYY, "MM"));
                                        day = Integer.parseInt(CommonMethods.getFormattedDate(field.getValue(), DD_MM_YYYY, "dd"));
                                    }
                                }
                                // As of version 2.3.0, `BottomSheetDatePickerDialog` is deprecated.
                                datePickerDialog = DatePickerDialog.newInstance(
                                        null, year
                                        , month - 1
                                        , day);

                                datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimary));
                                datePickerDialog.setMaxDate(Calendar.getInstance());
                                if (!datePickerDialog.isAdded()) {
                                    datePickerDialog.show(getChildFragmentManager(), getResources().getString(R.string.select_date));

                                    datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

                                            if (field.getName().equalsIgnoreCase("age") || field.getName().toLowerCase().contains("age"))
                                                textBox.setText(CommonMethods.calculateAge((monthOfYear + 1) + "/" + dayOfMonth + "/" + year, DD_MM_YYYY));
                                            else {
                                                textBox.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                                                if (field.isDobFlow()) {
                                                    Field yearsField = fields.get(fieldsIndex + 1);
                                                    Field monthsField = fields.get(fieldsIndex + 2);
                                                    Field daysField = fields.get(fieldsIndex + 3);

                                                    EditText yearFieldTextBox = fieldsContainer.findViewById(yearsField.getFieldId());
                                                    EditText monthsFieldTextBox = fieldsContainer.findViewById(monthsField.getFieldId());
                                                    EditText daysFieldTextBox = fieldsContainer.findViewById(daysField.getFieldId());

                                                    LocalDate birthdate = new LocalDate(year, (monthOfYear + 1), dayOfMonth);          //Birth date
                                                    LocalDate now = new LocalDate();                    //Today's date
                                                    Period period = new Period(birthdate, now, PeriodType.yearMonthDay());

                                                    yearFieldTextBox.setText(String.valueOf(period.getYears()));
                                                    monthsFieldTextBox.setText(String.valueOf(period.getMonths()));
                                                    daysFieldTextBox.setText(String.valueOf(period.getDays()));
                                                }
                                            }
                                        }

                                        @Override
                                        public void onDismissed() {
                                        }
                                    });
                                }
                            }
                        });

                        break;
                    case Constants.INPUT_TYPE.MOBILE:
                        textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.mobile_size);
                        textBox.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    case Constants.INPUT_TYPE.NAME:
                        textBox.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                        break;
                    case Constants.INPUT_TYPE.PIN_CODE:
                        textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.pincode_size);
                        textBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                    case Constants.INPUT_TYPE.TEXT_BOX_BIG:
                        textBox.setSingleLine(false);
                        textBox.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        textBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        textBox.setMaxLines(10);
                        break;
                    case Constants.INPUT_TYPE.NUMBER:
                        textBox.setGravity(Gravity.END);
                        textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.number_size);
                        textBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                }

                textBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        editTextError.setText("");
                        textBox.setBackgroundResource(R.drawable.edittext_selector);
                        // set latest value
                        field.setValue(String.valueOf(editable).trim());

                        field.setUpdated(!preValue.equalsIgnoreCase(field.getValue()));
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                break;
            }

            case Constants.TYPE.RADIO_BUTTON: {
                View fieldLayout = inflater.inflate(R.layout.field_radiobutton_layout, (LinearLayout) fieldsContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                final FlowRadioGroup radioGroup = fieldLayout.findViewById(R.id.radioGroup);
                radioGroup.setId(CommonMethods.generateViewId());

                final TextView radioGroupError = fieldLayout.findViewById(R.id.radioGroupError);
                radioGroupError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(radioGroupError.getId());

                ArrayList<String> dataList = field.getDataList();

                final String preValue = field.getValue();

                for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
                    String data = dataList.get(dataIndex);
                    RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.radiobutton, radioGroup, false);
                    radioButton.setId(CommonMethods.generateViewId());
                    radioButton.setText(data);

                    // set pre value
                    if (field.getValue().equalsIgnoreCase(radioButton.getText().toString()))
                        radioButton.setChecked(true);

                    radioGroup.addView(radioButton);
                }

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                        CommonMethods.hideKeyboard(getContext());

                        RadioButton radioButton = group.findViewById(checkedId);
                        field.setValue(radioButton.getText().toString());
                        radioGroupError.setText("");

                        field.setUpdated(!preValue.equalsIgnoreCase(field.getValue()));
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                break;
            }
            case Constants.TYPE.CHECKBOX: {
                View fieldLayout = inflater.inflate(R.layout.field_checkbox_layout, (LinearLayout) fieldsContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                FlowLayout checkBoxGroup = fieldLayout.findViewById(R.id.checkBoxGroup);
                checkBoxGroup.setId(CommonMethods.generateViewId());

                final TextView checkBoxGroupError = fieldLayout.findViewById(R.id.checkBoxGroupError);
                checkBoxGroupError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(checkBoxGroupError.getId());

                ArrayList<String> dataList = field.getDataList();
                final ArrayList<String> values = field.getValues();
                final ArrayList<String> preValues = new ArrayList<>(values);

                for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
                    String data = dataList.get(dataIndex);
                    final CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.checkbox, checkBoxGroup, false);

                    // set pre value
                    for (String value : values)
                        if (value.equalsIgnoreCase(data))
                            checkBox.setChecked(true);

                    checkBox.setId(CommonMethods.generateViewId());
                    checkBox.setText(data);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            CommonMethods.hideKeyboard(getContext());
                            checkBoxGroupError.setText("");

                            // set latest value

                            if (isChecked) {
                                values.add(checkBox.getText().toString());
                            } else {
                                values.remove(checkBox.getText().toString());
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
            case Constants.TYPE.DROPDOWN: {
                View fieldLayout = inflater.inflate(R.layout.field_dropdown_layout, (LinearLayout) fieldsContainer, false);

                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                final Spinner dropDown = fieldLayout.findViewById(R.id.dropDown);
                dropDown.setId(CommonMethods.generateViewId());
                field.setFieldId(dropDown.getId());

                final TextView dropDownError = fieldLayout.findViewById(R.id.dropDownError);
                dropDownError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(dropDownError.getId());

                final ArrayList<String> dataList = field.getDataList();
                final ArrayList<String> dataListTemp = field.getDataListTemp();

                if (!dataList.isEmpty() && (dataListTemp.isEmpty() || dataListTemp.size() == 1)) {
                    dataListTemp.clear();
                    dataListTemp.addAll(dataList);
                }

                if (dataListTemp.size() > 1)
                    if (!dataListTemp.get(0).toLowerCase().equals("select"))
                        dataListTemp.add(0, "Select");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(dropDown.getContext(), R.layout.dropdown_item, dataListTemp);
                dropDown.setAdapter(adapter);

                if (!field.getDataTable().isEmpty() && dataListTemp.isEmpty() && !field.getValue().isEmpty()) {
                    dataListTemp.add(field.getValue());
                    adapter.notifyDataSetChanged();
                } else dropDown.setSelection(dataListTemp.indexOf(field.getValue()));

                final String preValue = field.getValue();

                dropDown.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            CommonMethods.hideKeyboard(getContext());
                            if (!field.getDataTable().isEmpty() && (field.getDataListTemp().isEmpty() || field.getDataListTemp().size() == 1)) {
                                tempField = field;
                                MasterDataRequest masterDataRequest = new MasterDataRequest();
                                masterDataRequest.setDataTable(field.getDataTable());
                                patientHelper.getMasterData(masterDataRequest);
                            }
                        }
                        return false;
                    }
                });

                dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if (position != 0) {
                            // set latest value
                            field.setValue(dataListTemp.get(position));
                            dropDownError.setText("");
                            dropDown.setBackgroundResource(R.drawable.dropdown_selector);

                        } else if (dataListTemp.size() != 1)
                            field.setValue("");

                        field.setUpdated(!preValue.equalsIgnoreCase(field.getValue()));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                break;
            }

            case Constants.TYPE.RATING_BAR: {

                View fieldLayout = inflater.inflate(R.layout.field_ratingbar_layout, (LinearLayout) fieldsContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                final float preValue = field.getRating();

                // Add Rating Bar

                RatingBar ratingBar = fieldLayout.findViewById(R.id.ratingBar);
                ratingBar.setRating(field.getRating());
                ratingBar.setMax(field.getMaxRating() * 2);
                ratingBar.setNumStars(field.getMaxRating());

                final EditText ratingReasonTextBox = fieldLayout.findViewById(R.id.ratingReasonTextBox);
                if (!field.getHint().equals(""))
                    ratingReasonTextBox.setHint(field.getHint());

                ratingReasonTextBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // set latest value
                        field.setTextValue(String.valueOf(editable).trim());
                    }
                });

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        field.setRating(rating);
                        if (rating < 3f && rating != 0f)
                            ratingReasonTextBox.setVisibility(View.VISIBLE);
                        else ratingReasonTextBox.setVisibility(View.GONE);

                        field.setUpdated(preValue != field.getRating());
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                break;
            }
        }

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
                .pickPhoto(getActivity());*/


        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getActivity());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PageFragmentPermissionsDispatcher.onRequestPermissionsResult(PageFragment.this, requestCode, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (resultCode == RESULT_OK) {
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
                            .into(profilePhoto);

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
                        .into(profilePhoto);

                uploadProfilePhoto(filePath);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }
        }
    }

    private void uploadProfilePhoto(final String filePath) {

        String mobileNumber = AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.MOBILE, getContext());
        String profileId = AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.PROFILE_ID, getContext());

        String uploadUrl = Config.HTTP + AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, getContext()) + "/" + Config.POST_PROFILE_IMAGE;

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
                            CommonMethods.showToast(getContext(), commonResponse.getCommon().getStatusMessage());

                            sectionHasProfilePhoto.setProfilePhoto(commonResponse.getPatientData().getProfilePhoto());
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
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equals(Constants.GET_MASTER_DATA)) {
            MasterDataModel masterDataModel = (MasterDataModel) customResponse;
            if (masterDataModel.getCommon().isSuccess()) {
                final ArrayList<String> dataList = tempField.getDataListTemp();

                if ((dataList.isEmpty() || dataList.size() == 1) && !masterDataModel.getData().getMasterData().isEmpty()) {
                    dataList.clear();
                    dataList.addAll(masterDataModel.getData().getMasterData());
                }

                if (!dataList.isEmpty()) {

                    switch (tempField.getType()) {

                        case Constants.TYPE.DROPDOWN: {

                            final Spinner dropDown = mSectionsContainer.findViewById(tempField.getFieldId());
                            if (!dataList.get(0).toLowerCase().contains("select"))
                                dataList.add(0, "Select");

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(dropDown.getContext(), R.layout.dropdown_item, dataList);
                            dropDown.setAdapter(adapter);

                            // set pre value
                            dropDown.setSelection(dataList.indexOf(tempField.getValue()));

                            break;
                        }

                        case Constants.TYPE.AUTO_COMPLETE:
                        case Constants.TYPE.TEXT_BOX_GROUP: {

                            final AutoCompleteTextView textBox = mSectionsContainer.findViewById(tempField.getFieldId());

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                    android.R.layout.simple_dropdown_item_1line, dataList);
                            textBox.setAdapter(adapter);

                            break;
                        }
                    }
                } else CommonMethods.showToast(getContext(), "Empty Data");
            }
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
}