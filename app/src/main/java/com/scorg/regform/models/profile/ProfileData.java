package com.scorg.regform.models.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileData {

    @SerializedName("profileId")
    @Expose
    private String profileId;

    @SerializedName("hospitalPatId")
    @Expose
    private String hospitalPatId;

    @SerializedName("relation")
    @Expose
    private String relation;

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getHospitalPatId() {
        return hospitalPatId;
    }

    public void setHospitalPatId(String hospitalPatId) {
        this.hospitalPatId = hospitalPatId;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

}