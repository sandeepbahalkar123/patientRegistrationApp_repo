package com.scorg.regform.models.form.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.regform.interfaces.CustomResponse;

/**
 * Created by ganeshshirole on 24/11/17.
 */

public class GetPatientInfo implements CustomResponse {
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("docId")
    @Expose
    private int docId;
    @SerializedName("clinicId")
    @Expose
    private int hospitalId;
    @SerializedName("hospitalPatId")
    @Expose
    private int hospitalPatId = -1;
    @SerializedName("locationId")
    @Expose
    private int locationId;
    @SerializedName("profileId")
    @Expose
    private int profileId = -1;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public int getHospitalPatId() {
        return hospitalPatId;
    }

    public void setHospitalPatId(int hospitalPatId) {
        this.hospitalPatId = hospitalPatId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }
}
