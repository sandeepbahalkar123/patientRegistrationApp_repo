package com.scorg.forms.models.master.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.forms.interfaces.CustomResponse;

public class MasterDataRequest implements CustomResponse {

    @SerializedName("dataTable")
    @Expose
    private String dataTable;

    @SerializedName("selectedValue")
    @Expose
    private String selectedValue;

    public String getDataTable() {
        return dataTable;
    }

    public void setDataTable(String dataTable) {
        this.dataTable = dataTable;
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }
}