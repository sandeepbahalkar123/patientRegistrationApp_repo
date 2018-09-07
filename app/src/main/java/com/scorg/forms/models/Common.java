package com.scorg.forms.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Common implements Parcelable {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("statusMessage")
    @Expose
    private String statusMessage;
    public final static Parcelable.Creator<Common> CREATOR = new Creator<Common>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Common createFromParcel(Parcel in) {
            return new Common(in);
        }

        public Common[] newArray(int size) {
            return (new Common[size]);
        }

    };

    protected Common(Parcel in) {
        this.success = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.statusCode = ((int) in.readValue((int.class.getClassLoader())));
        this.statusMessage = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Common() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(success);
        dest.writeValue(statusCode);
        dest.writeValue(statusMessage);
    }

    public int describeContents() {
        return 0;
    }

}