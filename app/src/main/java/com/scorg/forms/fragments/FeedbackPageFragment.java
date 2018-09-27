package com.scorg.forms.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
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
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.scorg.forms.R;
import com.scorg.forms.helpers.PatientHelper;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.interfaces.HelperResponse;
import com.scorg.forms.models.form.Field;
import com.scorg.forms.models.form.Page;
import com.scorg.forms.models.form.ValuesObject;
import com.scorg.forms.models.master.MasterDataModel;
import com.scorg.forms.models.master.request.MasterDataRequest;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Constants;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import static com.scorg.forms.fragments.FormFragment.FORM_NAME;
import static com.scorg.forms.fragments.FormFragment.FORM_NUMBER;
import static com.scorg.forms.util.Constants.DATE_PATTERN.DD_MM_YYYY;

public class FeedbackPageFragment extends Fragment implements HelperResponse {

    private static final String FIELDS = "fields";
    private ArrayList<Field> fields;
    private int formNumber;
    private String mReceivedFormName;
    private DatePickerDialog datePickerDialog;
    private PatientHelper patientHelper;
    private Field tempField;
    private LinearLayout fieldsContainer;

    public FeedbackPageFragment() {
        // Required empty public constructor
    }

    public static FeedbackPageFragment newInstance(int formNumber, int position, Page page, String mReceivedFormName) {
        FeedbackPageFragment fragment = new FeedbackPageFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(FIELDS, page.getFields());
        args.putInt(FORM_NUMBER, formNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fields = getArguments().getParcelableArrayList(FIELDS);
            formNumber = getArguments().getInt(FORM_NUMBER);
            mReceivedFormName = getArguments().getString(FORM_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feedback_page, container, false);
        fieldsContainer = rootView.findViewById(R.id.fieldsContainer);

        patientHelper = new PatientHelper(getContext(), this);

        for (int fieldIndex = 0; fieldIndex < fields.size(); fieldIndex++) {
            addField(fieldsContainer, fields, fields.get(fieldIndex), fieldIndex, inflater, -1);
        }

        return rootView;
    }

    @SuppressLint("SetTextI18n")
    private void addField(final View fieldsContainer, final ArrayList<Field> fields, final Field field, final int fieldsIndex, final LayoutInflater inflater, int indexToAddView) {

        String questionNo = String.valueOf(fieldsIndex + 1) + ". ";

        switch (field.getType()) {

            case Constants.TYPE.TEXT_BOX_GROUP: {
                // Added Extended Layout
                final View fieldLayout = inflater.inflate(R.layout.feedback_autocomplete_textbox_layout, (LinearLayout) fieldsContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(questionNo + (field.isMandatory() ? "*" + field.getName() : field.getName()));

                final AutoCompleteTextView textBox = fieldLayout.findViewById(R.id.editText);
                textBox.setId(CommonMethods.generateViewId());
                field.setFieldId(textBox.getId());

                final TextView editTextError = fieldLayout.findViewById(R.id.editTextError);
                editTextError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(editTextError.getId());

                final ArrayList<ValuesObject> dataList = field.getDataList();
                final ArrayList<ValuesObject> dataListTemp = field.getDataListTemp();

                if (!dataList.isEmpty() && dataListTemp.isEmpty())
                    dataListTemp.addAll(dataList);

                ArrayAdapter<ValuesObject> adapter = new ArrayAdapter<ValuesObject>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, dataListTemp);
                textBox.setAdapter(adapter);

                // set pre value
                textBox.setText(field.getValue().toString());
                final String preValue = field.getValue().toString();

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
                        addField(fieldsContainer, fields, moreFields.get(moreFieldIndex), fieldsIndex, inflater, indexToAddView + moreFieldIndex + 1);
                    else
                        addField(fieldsContainer, fields, moreFields.get(moreFieldIndex), fieldsIndex, inflater, -1);
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
                        field.setValue(new ValuesObject(String.valueOf(editable).trim()));

                        field.setUpdated(!preValue.equalsIgnoreCase(field.getValue().toString()));
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
                                patientHelper.getMasterDataFromAPI(masterDataRequest);
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
                            fieldGroupNew.setValue(new ValuesObject());
                            fieldGroupNew.setMandatory(false);

                            ArrayList<Field> clonedMoreFields = new ArrayList<Field>();
                            for (Field field1 :
                                    field.getTextBoxGroup()) {
                                Field cloneField = (Field) field1.clone();
                                cloneField.setValue(new ValuesObject());
                                cloneField.setMandatory(false);
                                clonedMoreFields.add(cloneField);
                            }

                            fieldGroupNew.setTextBoxGroup(clonedMoreFields);

                            addField(fieldsContainer, fields, fieldGroupNew, fieldsIndex, inflater, ((LinearLayout) fieldLayout.getParent()).indexOfChild(fieldLayout) + 1 + fieldGroupNew.getTextBoxGroup().size());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;
            }

            case Constants.TYPE.AUTO_COMPLETE: {
                // Added Extended Layout
                final View fieldLayout = inflater.inflate(R.layout.feedback_autocomplete_textbox_layout, (LinearLayout) fieldsContainer, false);
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

                final ArrayList<ValuesObject> dataList = field.getDataList();
                final ArrayList<ValuesObject> dataListTemp = field.getDataListTemp();

                if (!dataList.isEmpty() && dataListTemp.isEmpty())
                    dataListTemp.addAll(dataList);

                ArrayAdapter<ValuesObject> adapter = new ArrayAdapter<ValuesObject>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, dataListTemp);
                textBox.setAdapter(adapter);

                // set pre value
                textBox.setText(field.getValue().toString());
                final String preValue = field.getValue().toString();

                if (field.getLength() > 0) {
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(field.getLength());
                    textBox.setFilters(fArray);
                }

                setAutoCompleteInputType(field, textBox);

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
                        field.setValue(new ValuesObject(String.valueOf(editable).trim()));

                        field.setUpdated(!preValue.equalsIgnoreCase(field.getValue().toString()));
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
                                patientHelper.getMasterDataFromAPI(masterDataRequest);
                            }
                        }
                    }
                });

                ImageView plusButton = fieldLayout.findViewById(R.id.plusButton);
                plusButton.setVisibility(View.GONE);

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);

                break;
            }

            case Constants.TYPE.TEXT_BOX: {
                View fieldLayout = inflater.inflate(R.layout.feedback_textbox_layout, (LinearLayout) fieldsContainer, false);

                TextView labelView = fieldLayout.findViewById(R.id.labelView);

                labelView.setText(questionNo + (field.isMandatory() ? "*" + field.getName() : field.getName()));

                final EditText textBox = fieldLayout.findViewById(R.id.editText);
                textBox.setId(CommonMethods.generateViewId());

                field.setFieldId(textBox.getId());

                final TextView editTextError = fieldLayout.findViewById(R.id.editTextError);
                editTextError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(editTextError.getId());

                // set pre value
                textBox.setText(field.getValue().toString());
                final String preValue = field.getValue().toString();

                if (field.getLength() > 0) {
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(field.getLength());
                    textBox.setFilters(fArray);
                }

                setTextBoxInputType(field, textBox, fields, fieldsIndex);

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
                        field.setValue(new ValuesObject(String.valueOf(editable).trim()));

                        field.setUpdated(!preValue.equalsIgnoreCase(field.getValue().toString()));
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                break;
            }

            case Constants.TYPE.RADIO_BUTTON: {
                addRadioButton(questionNo, fieldsContainer, field, inflater, indexToAddView, /*radio_button->*/false);
                break;
            }

            case Constants.TYPE.RADIO_BUTTON_WITH_TEXT: {
                addRadioButton(questionNo, fieldsContainer, field, inflater, indexToAddView, /*mixed_group->*/true);
                break;
            }

            case Constants.TYPE.CHECKBOX: {
                addCheckbox(questionNo, fieldsContainer, field, inflater, indexToAddView, /*mixed_group->*/false);
                break;
            }

            case Constants.TYPE.CHECKBOX_WITH_TEXT: {
                addCheckbox(questionNo, fieldsContainer, field, inflater, indexToAddView, /*mixed_group->*/true);
                break;
            }

            case Constants.TYPE.DROPDOWN: {
                addDropDown(questionNo, fieldsContainer, field, inflater, indexToAddView, /*mixed_group->*/false);
                break;
            }

            case Constants.TYPE.DROPDOWN_WITH_TEXT: {
                addDropDown(questionNo, fieldsContainer, field, inflater, indexToAddView, /*mixed_group->*/true);
                break;
            }

            case Constants.TYPE.RATING_BAR: {

                View fieldLayout = inflater.inflate(R.layout.feedback_ratingbar_layout, (LinearLayout) fieldsContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(questionNo + (field.isMandatory() ? "*" + field.getName() : field.getName()));

                // Add Rating Bar

                RatingBar ratingBar = fieldLayout.findViewById(R.id.ratingBar);
                ratingBar.setRating(field.getRating());
                ratingBar.setMax(field.getMaxRating() * 2);
                ratingBar.setNumStars(field.getMaxRating());

                final EditText ratingReasonTextBox = fieldLayout.findViewById(R.id.ratingReasonTextBox);

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

                if (!field.getHint().equals(""))
                    ratingReasonTextBox.setHint(field.getHint());

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        field.setRating(rating);
                        if (rating < 3f && rating != 0f)
                            ratingReasonTextBox.setVisibility(View.VISIBLE);
                        else ratingReasonTextBox.setVisibility(View.GONE);
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void addDropDown(String questionNo, View fieldsContainer, final Field field, LayoutInflater inflater, int indexToAddView, boolean isMixed) {

        View fieldLayout = inflater.inflate(R.layout.feedback_dropdown_layout, (LinearLayout) fieldsContainer, false);
        TextView labelView = fieldLayout.findViewById(R.id.labelView);
        labelView.setText(questionNo + (field.isMandatory() ? "*" + field.getName() : field.getName()));

        final Spinner dropDown = fieldLayout.findViewById(R.id.dropDown);
        dropDown.setId(CommonMethods.generateViewId());

        field.setFieldId(dropDown.getId());

        final EditText otherTextBox = fieldLayout.findViewById(R.id.otherTextBox);
        final TextView unitTextView = fieldLayout.findViewById(R.id.unitTextView);

        if (!field.getUnit().equals(""))
            unitTextView.setText(field.getUnit());

        final LinearLayout otherTextBoxParent = fieldLayout.findViewById(R.id.otherTextBoxParent);
//        otherTextBoxParent.setId(CommonMethods.generateViewId());
//        field.setOtherTextBoxParentId(otherTextBoxParent.getId());

        final TextView dropDownError = fieldLayout.findViewById(R.id.dropDownError);
        dropDownError.setId(CommonMethods.generateViewId());
        field.setErrorViewId(dropDownError.getId());

        boolean isOthersThere = false;

        final ArrayList<ValuesObject> dataList = field.getDataList();
        final ArrayList<ValuesObject> dataListTemp = field.getDataListTemp();

        if (!dataList.isEmpty() && (dataListTemp.isEmpty() || dataListTemp.size() == 1)) {
            dataListTemp.clear();
            dataListTemp.addAll(dataList);
        }

        if (dataListTemp.size() > 1)
            if (!dataListTemp.get(0).toString().toLowerCase().equals("select"))
                dataListTemp.add(0, new ValuesObject("Select"));

        if (!field.getShowWhenSelect().isEmpty())
            isOthersThere = true;

        ArrayAdapter<ValuesObject> adapter = new ArrayAdapter<>(dropDown.getContext(), R.layout.dropdown_item, dataListTemp);
        dropDown.setAdapter(adapter);

        if (!field.getDataTable().isEmpty() && dataListTemp.isEmpty() && !field.getValue().toString().isEmpty()) {
            dataListTemp.add(field.getValue());
            adapter.notifyDataSetChanged();
        } else dropDown.setSelection(dataListTemp.indexOf(field.getValue()));

        final String preValue = field.getValue().toString();
        final String preOtherValue = field.getTextValue();

        dropDown.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CommonMethods.hideKeyboard(getContext());
                    if (!field.getDataTable().isEmpty() && (dataListTemp.isEmpty() || dataListTemp.size() == 1)) {
                        tempField = field;
                        MasterDataRequest masterDataRequest = new MasterDataRequest();
                        masterDataRequest.setDataTable(field.getDataTable());
                        patientHelper.getMasterDataFromAPI(masterDataRequest);
                    }
                }
                return false;
            }
        });

        final boolean finalIsOthersThere = isOthersThere;
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {

                    if (finalIsOthersThere) {
                        if (dataListTemp.get(position).toString().equalsIgnoreCase(field.getShowWhenSelect()))
                            otherTextBoxParent.setVisibility(View.VISIBLE);
                        else {
                            otherTextBox.setText("");
                            otherTextBoxParent.setVisibility(View.GONE);
                        }
                    }

                    // set latest value
                    field.setValue(dataListTemp.get(position));
                    dropDownError.setText("");
                    dropDown.setBackgroundResource(R.drawable.dropdown_selector);

                } else if (dataListTemp.size() != 1)
                    field.setValue(new ValuesObject());

                field.setUpdated(!preValue.equalsIgnoreCase(field.getValue().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Add Extra edit text

        if (isMixed) {

            if (isOthersThere) {
                if (field.getValue().toString().equalsIgnoreCase(field.getShowWhenSelect())) {
                    otherTextBoxParent.setVisibility(View.VISIBLE);
                    otherTextBox.setText(field.getTextValue());
                } else otherTextBoxParent.setVisibility(View.GONE);
            } else {
                otherTextBoxParent.setVisibility(View.VISIBLE);
                otherTextBox.setText(field.getTextValue());
            }

            if (!field.getHint().equals(""))
                otherTextBox.setHint(field.getHint());
            setTextBoxInputType(field, otherTextBox, fields, -1);
            otherTextBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    field.setTextValue(String.valueOf(s));
                    field.setUpdated(!preOtherValue.equalsIgnoreCase(field.getTextValue()));
                }
            });
        }

        if (indexToAddView != -1)
            ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
        else ((LinearLayout) fieldsContainer).addView(fieldLayout);
    }

    private void setTextBoxInputType(final Field field, final EditText textBox, final ArrayList<Field> fields, final int fieldsIndex) {

        switch (field.getInputType()) {
            case Constants.INPUT_TYPE.EMAIL:
                textBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case Constants.INPUT_TYPE.DATE:
                textBox.setCursorVisible(false);
                textBox.setFocusableInTouchMode(false);

                textBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int year;
                        int month;
                        int day;

                        if (field.getValue().toString().isEmpty()) {
                            Calendar now = Calendar.getInstance();
                            year = now.get(Calendar.YEAR);
                            month = now.get(Calendar.MONTH);
                            day = now.get(Calendar.DAY_OF_MONTH);
                        } else {
                            year = Integer.parseInt(CommonMethods.getFormattedDate(field.getValue().toString(), DD_MM_YYYY, "yyyy"));
                            month = Integer.parseInt(CommonMethods.getFormattedDate(field.getValue().toString(), DD_MM_YYYY, "MM"));
                            day = Integer.parseInt(CommonMethods.getFormattedDate(field.getValue().toString(), DD_MM_YYYY, "dd"));
                        }

                        // As of version 2.3.0, `BottomSheetDatePickerDialog` is deprecated.
                        datePickerDialog = DatePickerDialog.newInstance(
                                null,
                                year,
                                month - 1,
                                day);

                        datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimary));
                        datePickerDialog.setMaxDate(Calendar.getInstance());
                        if (!datePickerDialog.isAdded()) {
                            datePickerDialog.show(getChildFragmentManager(), getResources().getString(R.string.select_date));
                            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
                                    if (field.getName().equalsIgnoreCase("age") || field.getName().toLowerCase().contains("age"))
                                        textBox.setText(CommonMethods.calculateAge((monthOfYear + 1) + "/" + dayOfMonth + "/" + year, "MM-dd-yyyy"));
                                    else {
                                        textBox.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                                        if (field.isDobFlow() && fieldsIndex != -1) {
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
//                textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.mobile_size);
                textBox.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case Constants.INPUT_TYPE.NAME:
                textBox.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                break;
            case Constants.INPUT_TYPE.PIN_CODE:
//                textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.pincode_size);
                textBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case Constants.INPUT_TYPE.TEXT_BOX_BIG:
                textBox.setSingleLine(false);
                textBox.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                textBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                textBox.setLines(6);
                textBox.setGravity(Gravity.TOP);
                textBox.setMaxLines(10);
                break;
            case Constants.INPUT_TYPE.NUMBER:
                textBox.setSingleLine(false);
//                textBox.setGravity(Gravity.END);
//                textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.number_size);
                textBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }
    }

    private void setAutoCompleteInputType(final Field field, final AutoCompleteTextView textBox) {

        switch (field.getInputType()) {
            case Constants.INPUT_TYPE.EMAIL:
                textBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case Constants.INPUT_TYPE.MOBILE:
//                textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.mobile_size);
                textBox.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case Constants.INPUT_TYPE.NAME:
                textBox.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                break;
            case Constants.INPUT_TYPE.PIN_CODE:
//                textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.pincode_size);
                textBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case Constants.INPUT_TYPE.TEXT_BOX_BIG:
                textBox.setSingleLine(false);
                textBox.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                textBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                textBox.setLines(6);
                textBox.setGravity(Gravity.TOP);
                textBox.setMaxLines(10);
                break;
            case Constants.INPUT_TYPE.NUMBER:
                textBox.setSingleLine(false);
//                textBox.setGravity(Gravity.END);
//                textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.number_size);
                textBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void addCheckbox(String questionNo, View fieldsContainer, final Field field, LayoutInflater inflater, int indexToAddView, boolean isMixed) {
        View fieldLayout = inflater.inflate(R.layout.feedback_checkbox_layout, (LinearLayout) fieldsContainer, false);
        TextView labelView = fieldLayout.findViewById(R.id.labelView);
        labelView.setText(questionNo + (field.isMandatory() ? "*" + field.getName() : field.getName()));

        TableLayout checkBoxGroup = fieldLayout.findViewById(R.id.checkBoxGroup);
        checkBoxGroup.setId(CommonMethods.generateViewId());

        final EditText otherTextBox = fieldLayout.findViewById(R.id.otherTextBox);
        final TextView unitTextView = fieldLayout.findViewById(R.id.unitTextView);
        if (!field.getUnit().equals(""))
            unitTextView.setText(field.getUnit());
        final LinearLayout otherTextBoxParent = fieldLayout.findViewById(R.id.otherTextBoxParent);

        final TextView checkBoxGroupError = fieldLayout.findViewById(R.id.checkBoxGroupError);
        checkBoxGroupError.setId(CommonMethods.generateViewId());
        field.setErrorViewId(checkBoxGroupError.getId());

        ArrayList<ValuesObject> dataList = field.getDataList();
        final ArrayList<ValuesObject> values = field.getValues();
        final ArrayList<ValuesObject> preValues = new ArrayList<>(values);

        final String preOtherValue = field.getTextValue();

        boolean isOthersThere = false;

        int matrix = field.getMatrix() == 0 ? 2 : field.getMatrix();

        for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
            String data = dataList.get(dataIndex).toString();

            if (data.equalsIgnoreCase(field.getShowWhenSelect()))
                isOthersThere = true;

            final CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.checkbox, checkBoxGroup, false);
            checkBox.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));
            checkBox.setId(CommonMethods.generateViewId());

            // set pre value
            for (ValuesObject value : values)
                if (value.toString().equalsIgnoreCase(data))
                    checkBox.setChecked(true);

            checkBox.setText(data);
            final boolean finalIsOthersThere = isOthersThere;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    CommonMethods.hideKeyboard(getContext());

                    String valueText = checkBox.getText().toString();

                    if (finalIsOthersThere) {
                        if (valueText.equalsIgnoreCase(field.getShowWhenSelect()) && isChecked)
                            otherTextBoxParent.setVisibility(View.VISIBLE);
                        else if (valueText.equalsIgnoreCase(field.getShowWhenSelect()) && !isChecked) {
                            otherTextBox.setText("");
                            otherTextBoxParent.setVisibility(View.GONE);
                        }
                    }

                    checkBoxGroupError.setText("");

                    // set latest value

                    if (isChecked) {
                        values.add(new ValuesObject(valueText));
                    } else {
                        values.remove(valueText);
                    }

                    Collections.sort(preValues);
                    Collections.sort(values);
                    field.setUpdated(!Arrays.equals(preValues.toArray(), values.toArray()));
                }
            });

            if (dataIndex % matrix == 0) {

                TableRow tableRow = new TableRow(getContext());
                tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));

                tableRow.addView(checkBox);
                checkBoxGroup.addView(tableRow);
            } else {
                TableRow tableRow = (TableRow) checkBoxGroup.getChildAt(checkBoxGroup.getChildCount() - 1);
                tableRow.addView(checkBox);
            }
        }

        // Add Extra edit text

        if (isMixed) {

            if (isOthersThere)
                if (field.getValues().contains(field.getShowWhenSelect())) {
                    otherTextBoxParent.setVisibility(View.VISIBLE);
                    otherTextBox.setText(field.getTextValue());
                } else otherTextBoxParent.setVisibility(View.GONE);
            else {
                otherTextBoxParent.setVisibility(View.VISIBLE);
                otherTextBox.setText(field.getTextValue());
            }

            if (!field.getHint().equals(""))
                otherTextBox.setHint(field.getHint());
            setTextBoxInputType(field, otherTextBox, fields, -1);
            otherTextBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    field.setTextValue(String.valueOf(s));
                    field.setUpdated(!preOtherValue.equalsIgnoreCase(field.getTextValue()));
                }
            });
        }

        if (indexToAddView != -1)
            ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
        else ((LinearLayout) fieldsContainer).addView(fieldLayout);
    }

    // Add Radio Button

    @SuppressLint("SetTextI18n")
    private void addRadioButton(String questionNo, final View fieldsContainer, final Field field, final LayoutInflater inflater, int indexToAddView, boolean isMixed) {
        View fieldLayout = inflater.inflate(R.layout.feedback_radiobutton_layout, (LinearLayout) fieldsContainer, false);
        TextView labelView = fieldLayout.findViewById(R.id.labelView);
        labelView.setText(questionNo + (field.isMandatory() ? "*" + field.getName() : field.getName()));

        final RadioGroup radioGroup = fieldLayout.findViewById(R.id.radioGroup);
        radioGroup.setId(CommonMethods.generateViewId());

        final EditText otherTextBox = fieldLayout.findViewById(R.id.otherTextBox);
        final TextView unitTextView = fieldLayout.findViewById(R.id.unitTextView);
        if (!field.getUnit().equals(""))
            unitTextView.setText(field.getUnit());
        final LinearLayout otherTextBoxParent = fieldLayout.findViewById(R.id.otherTextBoxParent);

        final TextView radioGroupError = fieldLayout.findViewById(R.id.radioGroupError);
        radioGroupError.setId(CommonMethods.generateViewId());
        field.setErrorViewId(radioGroupError.getId());

        ArrayList<ValuesObject> dataList = field.getDataList();

        final String preValue = field.getValue().toString();
        final String preOtherValue = field.getTextValue();

        boolean isOthersThere = false;

        for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
            String data = dataList.get(dataIndex).toString();

            if (data.equalsIgnoreCase(field.getShowWhenSelect()))
                isOthersThere = true;

            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.radiobutton, radioGroup, false);
            radioButton.setId(CommonMethods.generateViewId());
            radioButton.setText(data);

            // set pre value
            if (field.getValue().toString().equalsIgnoreCase(radioButton.getText().toString()))
                radioButton.setChecked(true);

            radioGroup.addView(radioButton);
        }

        final boolean finalIsOthersThere = isOthersThere;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                CommonMethods.hideKeyboard(getContext());

                String valueText = ((RadioButton) group.findViewById(checkedId)).getText().toString();

                if (finalIsOthersThere) {
                    if (valueText.equalsIgnoreCase(field.getShowWhenSelect()))
                        otherTextBoxParent.setVisibility(View.VISIBLE);
                    else {
                        otherTextBox.setText("");
                        otherTextBoxParent.setVisibility(View.GONE);
                    }
                }

                field.setValue(new ValuesObject(valueText));
                radioGroupError.setText("");

                field.setUpdated(!preValue.equalsIgnoreCase(field.getValue().toString()));
            }
        });

        // Add Extra edit text

        if (isMixed) {

            if (isOthersThere) {
                if (field.getValue().toString().equalsIgnoreCase(field.getShowWhenSelect())) {
                    otherTextBoxParent.setVisibility(View.VISIBLE);
                    otherTextBox.setText(field.getTextValue());
                } else otherTextBoxParent.setVisibility(View.GONE);
            } else {
                otherTextBoxParent.setVisibility(View.VISIBLE);
                otherTextBox.setText(field.getTextValue());
            }

            if (!field.getHint().equals(""))
                otherTextBox.setHint(field.getHint());
            setTextBoxInputType(field, otherTextBox, fields, -1);

            otherTextBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    field.setTextValue(String.valueOf(s));

                    field.setUpdated(!preOtherValue.equalsIgnoreCase(field.getTextValue()));
                }
            });
        }

        if (indexToAddView != -1)
            ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
        else ((LinearLayout) fieldsContainer).addView(fieldLayout);
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equals(Constants.GET_MASTER_DATA)) {
            MasterDataModel masterDataModel = (MasterDataModel) customResponse;
            if (masterDataModel.getCommon().isSuccess()) {
                final ArrayList<ValuesObject> dataList = tempField.getDataListTemp();

                if ((dataList.isEmpty() || dataList.size() == 1) && !masterDataModel.getData().getMasterData().isEmpty()) {
                    dataList.clear();
                    dataList.addAll(masterDataModel.getData().getMasterData());
                }

                if (!dataList.isEmpty()) {
                    switch (tempField.getType()) {

                        case Constants.TYPE.DROPDOWN: {

                            final Spinner dropDown = fieldsContainer.findViewById(tempField.getFieldId());
                            if (!dataList.get(0).toString().toLowerCase().contains("select"))
                                dataList.add(0, new ValuesObject("Select"));

                            ArrayAdapter<ValuesObject> adapter = new ArrayAdapter<>(dropDown.getContext(), R.layout.dropdown_item, dataList);
                            dropDown.setAdapter(adapter);

                            // set pre value
                            dropDown.setSelection(dataList.indexOf(tempField.getValue()));

                            break;
                        }

                        case Constants.TYPE.AUTO_COMPLETE:
                        case Constants.TYPE.TEXT_BOX_GROUP: {

                            final AutoCompleteTextView textBox = fieldsContainer.findViewById(tempField.getFieldId());

                            ArrayAdapter<ValuesObject> adapter = new ArrayAdapter<ValuesObject>(getContext(),
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
