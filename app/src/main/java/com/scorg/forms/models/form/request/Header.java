package com.scorg.forms.models.form.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Header {
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("profileId")
    @Expose
    private String profileId;
    @SerializedName("docId")
    @Expose
    private int docId;
    @SerializedName("clinicId")
    @Expose
    private int clinicId;
    @SerializedName("hospitalPatId")
    @Expose
    private int hospitalPatId;

    public String getMobileNumber() {
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public int getClinicId() {
        return clinicId;
    }

    public void setClinicId(int clinicId) {
        this.clinicId = clinicId;
    }

    public int getHospitalPatId() {
        return hospitalPatId;
    }

    public void setHospitalPatId(int hospitalPatId) {
        this.hospitalPatId = hospitalPatId;
    }
}