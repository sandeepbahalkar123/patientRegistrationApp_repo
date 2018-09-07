package com.scorg.forms.models.form;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Undertaking implements Parcelable {

    @SerializedName("content")
    @Expose
    private String content;
    public final static Parcelable.Creator<Undertaking> CREATOR = new Creator<Undertaking>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Undertaking createFromParcel(Parcel in) {
            return new Undertaking(in);
        }

        public Undertaking[] newArray(int size) {
            return (new Undertaking[size]);
        }

    };

    protected Undertaking(Parcel in) {
        this.content = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Undertaking() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(content);
    }

    public int describeContents() {
        return 0;
    }

}