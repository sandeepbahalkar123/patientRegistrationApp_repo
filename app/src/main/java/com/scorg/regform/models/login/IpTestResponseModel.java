package com.scorg.regform.models.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.regform.interfaces.CustomResponse;
import com.scorg.regform.models.Common;

import java.io.Serializable;

public class IpTestResponseModel implements CustomResponse, Serializable {

    @SerializedName("common")
    @Expose
    private Common common;

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

}