package com.scorg.regform.models.form;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.regform.interfaces.CustomResponse;
import com.scorg.regform.models.Common;

public class ValidateResponse implements CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("data")
    @Expose
    private ValidateData data;

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public ValidateData getData() {
        return data;
    }

    public void setData(ValidateData data) {
        this.data = data;
    }

}