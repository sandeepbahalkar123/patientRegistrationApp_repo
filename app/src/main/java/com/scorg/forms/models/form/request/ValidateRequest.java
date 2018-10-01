package com.scorg.forms.models.form.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.forms.interfaces.CustomResponse;

public class ValidateRequest implements CustomResponse {

    @SerializedName("fieldValue")
    @Expose
    private String fieldValue;
    @SerializedName("fieldName")
    @Expose
    private String fieldName;
    @SerializedName("profileId")
    @Expose
    private String profileId;

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

}