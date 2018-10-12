package com.scorg.regform.models.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LoginData implements Parcelable {

    @SerializedName("docId")
    @Expose
    private int docId;
    @SerializedName("drName")
    @Expose
    private String drName;
    @SerializedName("clinicList")
    @Expose
    private ArrayList<ClinicList> clinicList = new ArrayList<ClinicList>();
    public final static Parcelable.Creator<LoginData> CREATOR = new Creator<LoginData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public LoginData createFromParcel(Parcel in) {
            return new LoginData(in);
        }

        public LoginData[] newArray(int size) {
            return (new LoginData[size]);
        }

    };

    protected LoginData(Parcel in) {
        this.docId = ((int) in.readValue((int.class.getClassLoader())));
        this.drName = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.clinicList, (ClinicList.class.getClassLoader()));
    }

    public LoginData() {
    }

    public ArrayList<ClinicList> getClinicList() {
        return clinicList;
    }

    public void setClinicList(ArrayList<ClinicList> clinicList) {
        this.clinicList = clinicList;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public String getDrName() {
        return drName;
    }

    public void setDrName(String drName) {
        this.drName = drName;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(docId);
        dest.writeValue(drName);
        dest.writeList(clinicList);
    }

    public int describeContents() {
        return 0;
    }
}