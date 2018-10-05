package com.scorg.forms.models.form;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Field implements Parcelable, Cloneable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("isMandatory")
    @Expose
    private boolean isMandatory;
    @SerializedName("length")
    @Expose
    private int length;
    @SerializedName("value")
    @Expose
    private ValuesObject value = new ValuesObject();
    @SerializedName("values")
    @Expose
    private ArrayList<ValuesObject> values = new ArrayList<>();
    @SerializedName("inputType")
    @Expose
    private String inputType = "";
    @SerializedName("dataList")
    @Expose
    private ArrayList<ValuesObject> dataList = new ArrayList<>();
    @SerializedName("dataTable")
    @Expose
    private String dataTable = "";
    @SerializedName("moreField")
    @Expose
    private ArrayList<Field> textBoxGroup = new ArrayList<>();
    @SerializedName("isIncludeInShortDescription")
    @Expose
    private boolean isIncludeInShortDescription = true;

    @SerializedName("isEditable")
    @Expose
    private boolean isEditable = true;

    @SerializedName("maxRating")
    @Expose
    private int maxRating;
    @SerializedName("rating")
    @Expose
    private float rating;

    @SerializedName("textValue")
    @Expose
    private String textValue = "";

    @SerializedName("showWhenSelect")
    @Expose
    private String showWhenSelect;

    @SerializedName("hint")
    @Expose
    private String hint = "";

    @SerializedName("unit")
    @Expose
    private String unit = "";

    @SerializedName("matrix")
    @Expose
    private int matrix;

    @SerializedName("key")
    @Expose
    private String key = "";

    @SerializedName("dobFlow")
    @Expose
    private boolean dobFlow;

    @SerializedName("regularExpression")
    @Expose
    private String regularExpression = "";

    @SerializedName("checkServerValidation")
    @Expose
    private boolean checkServerValidation;

    private int fieldId;
    private int fieldParentId;
    private int visibility = View.VISIBLE;

    private int errorViewId;
//    private int otherTextBoxParentId;
    private boolean isUpdated = false;
    private ArrayList<ValuesObject> dataListTemp = new ArrayList<>();

    public final static Parcelable.Creator<Field> CREATOR = new Creator<Field>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Field createFromParcel(Parcel in) {
            return new Field(in);
        }

        public Field[] newArray(int size) {
            return (new Field[size]);
        }

    };

    private Field(Parcel in) {
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.isMandatory = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.length = ((int) in.readValue((int.class.getClassLoader())));
        this.value = ((ValuesObject) in.readValue((ValuesObject.class.getClassLoader())));
        in.readList(this.values, (java.lang.String.class.getClassLoader()));
        this.inputType = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.dataList, (java.lang.String.class.getClassLoader()));
        this.dataTable = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.textBoxGroup, (Field.class.getClassLoader()));
        this.isIncludeInShortDescription = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.isEditable = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.maxRating = ((int) in.readValue((int.class.getClassLoader())));
        this.rating = ((float) in.readValue((float.class.getClassLoader())));
        this.textValue = ((String) in.readValue((String.class.getClassLoader())));
        this.showWhenSelect = ((String) in.readValue((String.class.getClassLoader())));
        this.hint = ((String) in.readValue((String.class.getClassLoader())));
        this.unit = ((String) in.readValue((String.class.getClassLoader())));
        this.matrix = ((int) in.readValue((int.class.getClassLoader())));
        this.key = ((String) in.readValue((String.class.getClassLoader())));
        this.dobFlow = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.fieldId = ((int) in.readValue((int.class.getClassLoader())));
        this.fieldParentId = ((int) in.readValue((int.class.getClassLoader())));
        this.visibility = ((int) in.readValue((int.class.getClassLoader())));
        this.errorViewId = ((int) in.readValue((int.class.getClassLoader())));
        this.isUpdated = ((boolean) in.readValue((boolean.class.getClassLoader())));

        this.regularExpression = ((String) in.readValue((String.class.getClassLoader())));
        this.checkServerValidation = ((boolean) in.readValue((boolean.class.getClassLoader())));

        in.readList(this.dataListTemp, (java.lang.String.class.getClassLoader()));
    }

    public Field() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public ValuesObject getValue() {
        return value == null ? new ValuesObject() : value;
    }

    public void setValue(ValuesObject value) {
        this.value = value;
    }

    public ArrayList<ValuesObject> getValues() {
        return values;
    }

    public void setValues(ArrayList<ValuesObject> values) {
        this.values = values;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public ArrayList<ValuesObject> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<ValuesObject> dataList) {
        this.dataList = dataList;
    }

    public String getDataTable() {
        return dataTable;
    }

    public void setDataTable(String dataTable) {
        this.dataTable = dataTable;
    }

    public ArrayList<Field> getTextBoxGroup() {
        return textBoxGroup;
    }

    public void setTextBoxGroup(ArrayList<Field> textBoxGroup) {
        this.textBoxGroup = textBoxGroup;
    }

    public boolean isIncludeInShortDescription() {
        return isIncludeInShortDescription;
    }

    public void setIncludeInShortDescription(boolean includeInShortDescription) {
        isIncludeInShortDescription = includeInShortDescription;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public int getMaxRating() {
        return maxRating;
    }

    public void setMaxRating(int maxRating) {
        this.maxRating = maxRating;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String others) {
        this.textValue = others;
    }

    public String getShowWhenSelect() {
        return showWhenSelect;
    }

    public void setShowWhenSelect(String showWhenSelect) {
        this.showWhenSelect = showWhenSelect;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getUnit() {
        return unit;
    }

    public int getMatrix() {
        return matrix;
    }

    public void setMatrix(int matrix) {
        this.matrix = matrix;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isDobFlow() {
        return dobFlow;
    }

    public void setDobFlow(boolean dobFlow) {
        this.dobFlow = dobFlow;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public int getFieldParentId() {
        return fieldParentId;
    }

    public void setFieldParentId(int fieldParentId) {
        this.fieldParentId = fieldParentId;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getErrorViewId() {
        return errorViewId;
    }

    public void setErrorViewId(int errorViewId) {
        this.errorViewId = errorViewId;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public String getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
    }

    public boolean isCheckServerValidation() {
        return checkServerValidation;
    }

    public void setCheckServerValidation(boolean checkServerValidation) {
        this.checkServerValidation = checkServerValidation;
    }

    public ArrayList<ValuesObject> getDataListTemp() {
        return dataListTemp;
    }

    public void setDataListTemp(ArrayList<ValuesObject> dataListTemp) {
        this.dataListTemp = dataListTemp;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(type);
        dest.writeValue(isMandatory);
        dest.writeValue(length);
        dest.writeValue(value);
        dest.writeList(values);
        dest.writeValue(inputType);
        dest.writeList(dataList);
        dest.writeValue(dataTable);
        dest.writeList(textBoxGroup);
        dest.writeValue(isIncludeInShortDescription);
        dest.writeValue(isEditable);
        dest.writeValue(maxRating);
        dest.writeValue(rating);
        dest.writeValue(textValue);
        dest.writeValue(showWhenSelect);
        dest.writeValue(hint);
        dest.writeValue(unit);
        dest.writeValue(matrix);
        dest.writeValue(key);
        dest.writeValue(dobFlow);
        dest.writeValue(fieldId);
        dest.writeValue(fieldParentId);
        dest.writeValue(visibility);
        dest.writeValue(errorViewId);
        dest.writeValue(isUpdated);
        dest.writeValue(regularExpression);
        dest.writeValue(checkServerValidation);
        dest.writeList(dataListTemp);
    }

    public int describeContents() {
        return 0;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}