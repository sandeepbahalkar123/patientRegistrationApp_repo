package com.scorg.regform.models.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ClinicList implements Parcelable {

    @SerializedName("clinicId")
    @Expose
    private int clinicId;
    @SerializedName("clinicName")
    @Expose
    private String clinicName = "";
    @SerializedName("clinicLocationBrandingInfo")
    @Expose
    private ArrayList<ClinicLocationBrandingInfo> clinicLocationBrandingInfo = new ArrayList<ClinicLocationBrandingInfo>();
    public final static Parcelable.Creator<ClinicList> CREATOR = new Creator<ClinicList>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ClinicList createFromParcel(Parcel in) {
            return new ClinicList(in);
        }

        public ClinicList[] newArray(int size) {
            return (new ClinicList[size]);
        }

    };

    protected ClinicList(Parcel in) {
        this.clinicId = ((int) in.readValue((int.class.getClassLoader())));
        this.clinicName = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.clinicLocationBrandingInfo, (ClinicLocationBrandingInfo.class.getClassLoader()));
    }

    public ClinicList() {
    }

    public int getClinicId() {
        return clinicId;
    }

    public void setClinicId(int clinicId) {
        this.clinicId = clinicId;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public ArrayList<ClinicLocationBrandingInfo> getClinicLocationBrandingInfo() {
        return clinicLocationBrandingInfo;
    }

    public void setClinicLocationBrandingInfo(ArrayList<ClinicLocationBrandingInfo> clinicLocationBrandingInfo) {
        this.clinicLocationBrandingInfo = clinicLocationBrandingInfo;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(clinicId);
        dest.writeValue(clinicName);
        dest.writeList(clinicLocationBrandingInfo);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return clinicName;
    }
}