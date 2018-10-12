package com.scorg.regform.models.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DoctorLoginData implements Parcelable {

    @SerializedName("authToken")
    @Expose
    private String authToken;
    public final static Creator<DoctorLoginData> CREATOR = new Creator<DoctorLoginData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public DoctorLoginData createFromParcel(Parcel in) {
            return new DoctorLoginData(in);
        }

        public DoctorLoginData[] newArray(int size) {
            return (new DoctorLoginData[size]);
        }

    };

    protected DoctorLoginData(Parcel in) {
        this.authToken = ((String) in.readValue((String.class.getClassLoader())));
    }

    public DoctorLoginData() {
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(authToken);
    }

    public int describeContents() {
        return 0;
    }

}