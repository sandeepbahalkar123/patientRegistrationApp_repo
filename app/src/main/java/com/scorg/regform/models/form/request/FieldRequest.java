package com.scorg.regform.models.form.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.regform.models.form.ValuesObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganeshshirole on 16/11/17.
 */

public class FieldRequest {

    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("value")
    @Expose
    private ValuesObject value;
    @SerializedName("values")
    @Expose
    private List<ValuesObject> values = new ArrayList<ValuesObject>();

    @SerializedName("rating")
    @Expose
    private Float rating = 0f;

    @SerializedName("textValue")
    @Expose
    private String textValue;

    public FieldRequest() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ValuesObject getValue() {
        return value;
    }

    public void setValue(ValuesObject value) {
        this.value = value;
    }

    public List<ValuesObject> getValues() {
        return values;
    }

    public void setValues(List<ValuesObject> values) {
        this.values = values;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

}