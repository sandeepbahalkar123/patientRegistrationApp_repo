package com.scorg.forms.models.form;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FormsData implements Parcelable {

    @SerializedName("isRegisteredUser")
    @Expose
    private boolean isRegisteredUser;
    @SerializedName("isClinicReg")
    @Expose
    private boolean isClinicReg;
    @SerializedName("personalInfo")
    @Expose
    private Form personalInfo;
    @SerializedName("forms")
    @Expose
    private ArrayList<Form> forms = new ArrayList<Form>();
    public final static Parcelable.Creator<FormsData> CREATOR = new Creator<FormsData>() {

        @SuppressWarnings({
                "unchecked"
        })
        public FormsData createFromParcel(Parcel in) {
            return new FormsData(in);
        }

        public FormsData[] newArray(int size) {
            return (new FormsData[size]);
        }

    };

    protected FormsData(Parcel in) {
        this.isRegisteredUser = ((boolean) in.readValue((String.class.getClassLoader())));
        this.isClinicReg = ((boolean) in.readValue((String.class.getClassLoader())));
        this.personalInfo = ((Form) in.readValue((Form.class.getClassLoader())));
        in.readList(this.forms, (Form.class.getClassLoader()));
    }

    public FormsData() {
    }

    public Form getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(Form personalInfo) {
        this.personalInfo = personalInfo;
    }

    public ArrayList<Form> getForms() {
        return forms;
    }

    public void setForms(ArrayList<Form> forms) {
        this.forms = forms;
    }

    public boolean isRegisteredUser() {
        return isRegisteredUser;
    }

    public void setRegisteredUser(boolean registeredUser) {
        isRegisteredUser = registeredUser;
    }

    public boolean isClinicReg() {
        return isClinicReg;
    }

    public void setClinicReg(boolean clinicReg) {
        isClinicReg = clinicReg;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(isRegisteredUser);
        dest.writeValue(isClinicReg);
        dest.writeValue(personalInfo);
        dest.writeList(forms);
    }

    public int describeContents() {
        return 0;
    }

}