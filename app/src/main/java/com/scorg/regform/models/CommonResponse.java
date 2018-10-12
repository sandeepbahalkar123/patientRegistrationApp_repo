package com.scorg.regform.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.regform.interfaces.CustomResponse;

public class CommonResponse implements CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;

    @SerializedName("patientData")
    @Expose
    private PatientInfo patientData;

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public PatientInfo getPatientData() {
        return patientData;
    }

    public void setPatientData(PatientInfo patientData) {
        this.patientData = patientData;
    }

}