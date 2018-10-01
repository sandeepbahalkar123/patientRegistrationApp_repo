package com.scorg.forms.models.form;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ValidateData {

    @SerializedName("isExists")
    @Expose
    private Boolean isExists;

    public Boolean getIsExists() {
        return isExists;
    }

    public void setIsExists(Boolean isExists) {
        this.isExists = isExists;
    }

}