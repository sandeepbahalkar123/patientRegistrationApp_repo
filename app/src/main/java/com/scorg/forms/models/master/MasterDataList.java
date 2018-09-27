package com.scorg.forms.models.master;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.forms.models.form.ValuesObject;

import java.util.ArrayList;

public class MasterDataList {

    @SerializedName("masterData")
    @Expose
    private ArrayList<ValuesObject> masterData = new ArrayList<>();

    public ArrayList<ValuesObject> getMasterData() {
        return masterData;
    }

    public void setMasterData(ArrayList<ValuesObject> masterData) {
        this.masterData = masterData;
    }
}