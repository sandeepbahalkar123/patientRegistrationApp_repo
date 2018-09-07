package com.scorg.forms.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.models.form.FormsData;

public class PatientData implements Parcelable, CustomResponse{

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("data")
    @Expose
    private FormsData data;
    public final static Parcelable.Creator<PatientData> CREATOR = new Creator<PatientData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PatientData createFromParcel(Parcel in) {
            return new PatientData(in);
        }

        public PatientData[] newArray(int size) {
            return (new PatientData[size]);
        }

    };

    protected PatientData(Parcel in) {
        this.common = ((Common) in.readValue((Common.class.getClassLoader())));
        this.data = ((FormsData) in.readValue((FormsData.class.getClassLoader())));
    }

    public PatientData() {
    }

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public FormsData getData() {
        return data;
    }

    public void setData(FormsData data) {
        this.data = data;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(common);
        dest.writeValue(data);
    }

    public int describeContents() {
        return 0;
    }

}