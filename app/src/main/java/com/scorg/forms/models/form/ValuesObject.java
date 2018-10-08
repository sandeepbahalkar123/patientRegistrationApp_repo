package com.scorg.forms.models.form;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ValuesObject implements Parcelable, Comparable {

    public final static Parcelable.Creator<ValuesObject> CREATOR = new Creator<ValuesObject>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ValuesObject createFromParcel(Parcel in) {
            return new ValuesObject(in);
        }

        public ValuesObject[] newArray(int size) {
            return (new ValuesObject[size]);
        }

    };
    @SerializedName("id")
    @Expose
    private String id = "";
    @SerializedName("name")
    @Expose
    private String name = "";
    @SerializedName("mobile")
    @Expose
    private String mobile = "";
    @SerializedName("email")
    @Expose
    private String email = "";

    protected ValuesObject(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.mobile = ((String) in.readValue((String.class.getClassLoader())));
        this.email = ((String) in.readValue((String.class.getClassLoader())));
    }

    public ValuesObject(String id, String name) {
        this.id = id;
        this.name = name;
        this.mobile = "";
        this.email = "";
    }

    public ValuesObject() {
        this.id = "";
        this.name = "";
        this.mobile = "";
        this.email = "";
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return name;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(mobile);
        dest.writeValue(email);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return this.toString().compareToIgnoreCase(o.toString());
    }
}