package com.scorg.forms.models.master;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MasterDataList {

    @SerializedName("masterData")
    @Expose
    private ArrayList<String> masterData = new ArrayList<>();

    public ArrayList<String> getMasterData() {
        return masterData;
    }

    public void setMasterData(ArrayList<String> masterData) {
        this.masterData = masterData;
    }
}