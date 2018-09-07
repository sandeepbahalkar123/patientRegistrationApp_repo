package com.scorg.forms.models.master;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.models.Common;

public class MasterDataModel implements CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("data")
    @Expose
    private MasterDataList data;

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public MasterDataList getData() {
        return data;
    }

    public void setData(MasterDataList data) {
        this.data = data;
    }

}