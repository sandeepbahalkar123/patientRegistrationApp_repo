package com.scorg.forms.models;

/**
 * Created by ganeshshirole on 22/12/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientInfo {

    @SerializedName("profilePhoto")
    @Expose
    private String profilePhoto;

    @SerializedName("patientName")
    @Expose
    private String patientName;
    @SerializedName("profileId")
    @Expose
    private String profileId;

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }
}
