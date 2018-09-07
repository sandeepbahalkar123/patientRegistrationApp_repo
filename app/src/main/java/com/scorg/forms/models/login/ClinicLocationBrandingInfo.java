package com.scorg.forms.models.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClinicLocationBrandingInfo implements Parcelable {

    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("locationId")
    @Expose
    private int locationId;
    @SerializedName("clinicSmallLogoUrl")
    @Expose
    private String clinicSmallLogoUrl  = "";
    @SerializedName("clinicBigLogoUrl")
    @Expose
    private String clinicBigLogoUrl ="";
    @SerializedName("tagline")
    @Expose
    private String tagline = "";
    public final static Parcelable.Creator<ClinicLocationBrandingInfo> CREATOR = new Creator<ClinicLocationBrandingInfo>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ClinicLocationBrandingInfo createFromParcel(Parcel in) {
            return new ClinicLocationBrandingInfo(in);
        }

        public ClinicLocationBrandingInfo[] newArray(int size) {
            return (new ClinicLocationBrandingInfo[size]);
        }

    };

    protected ClinicLocationBrandingInfo(Parcel in) {
        this.address = ((String) in.readValue((String.class.getClassLoader())));
        this.locationId = ((int) in.readValue((int.class.getClassLoader())));
        this.clinicSmallLogoUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.clinicBigLogoUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.tagline = ((String) in.readValue((String.class.getClassLoader())));
    }

    public ClinicLocationBrandingInfo() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getClinicSmallLogoUrl() {
        return clinicSmallLogoUrl;
    }

    public void setClinicSmallLogoUrl(String clinicSmallLogoUrl) {
        this.clinicSmallLogoUrl = clinicSmallLogoUrl;
    }

    public String getClinicBigLogoUrl() {
        return clinicBigLogoUrl;
    }

    public void setClinicBigLogoUrl(String clinicBigLogoUrl) {
        this.clinicBigLogoUrl = clinicBigLogoUrl;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(address);
        dest.writeValue(locationId);
        dest.writeValue(clinicSmallLogoUrl);
        dest.writeValue(clinicBigLogoUrl);
        dest.writeValue(tagline);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return address;
    }
}