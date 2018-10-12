package com.scorg.regform.models.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProfileList {

    @SerializedName("isRegisteredUser")
    @Expose
    private boolean registeredUser;
    @SerializedName("profiles")
    @Expose
    private List<ProfileData> profiles = new ArrayList<ProfileData>();

    public boolean isRegisteredUser() {
        return registeredUser;
    }

    public void setRegisteredUser(boolean isRegisteredUser) {
        this.registeredUser = isRegisteredUser;
    }

    public List<ProfileData> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<ProfileData> profiles) {
        this.profiles = profiles;
    }

}