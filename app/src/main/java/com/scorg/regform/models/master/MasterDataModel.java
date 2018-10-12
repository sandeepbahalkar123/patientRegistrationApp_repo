package com.scorg.regform.models.master;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.regform.interfaces.CustomResponse;
import com.scorg.regform.models.Common;

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