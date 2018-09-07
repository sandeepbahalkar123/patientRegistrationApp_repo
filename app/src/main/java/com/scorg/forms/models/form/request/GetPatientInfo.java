package com.scorg.forms.models.form.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.forms.interfaces.CustomResponse;

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
    @SerializedName("hospitalId")
    @Expose
    private int hospitalId;
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
