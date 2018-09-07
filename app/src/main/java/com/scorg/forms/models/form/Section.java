package com.scorg.forms.models.form;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Section implements Parcelable {

    @SerializedName("profilePhoto")
    @Expose
    private String profilePhoto;
    @SerializedName("sectionName")
    @Expose
    private String sectionName;
    @SerializedName("fields")
    @Expose
    private ArrayList<Field> fields = new ArrayList<>();
    public final static Parcelable.Creator<Section> CREATOR = new Creator<Section>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Section createFromParcel(Parcel in) {
            return new Section(in);
        }

        public Section[] newArray(int size) {
            return (new Section[size]);
        }

    };

    protected Section(Parcel in) {
        this.profilePhoto = ((String) in.readValue((String.class.getClassLoader())));
        this.sectionName = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.fields, (Field.class.getClassLoader()));
    }

    public Section() {
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public void setFields(ArrayList<Field> fields) {
        this.fields = fields;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(profilePhoto);
        dest.writeValue(sectionName);
        dest.writeList(fields);
    }

    public int describeContents() {
        return 0;
    }

}