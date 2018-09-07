package com.scorg.forms.models.form.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.models.form.Form;

public class FormRequest implements CustomResponse{

    @SerializedName("header")
    @Expose
    private Header header;
    @SerializedName("personalInfo")
    @Expose
    private Form personalInfo;
    @SerializedName("form")
    @Expose
    private Form form;
    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Form getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(Form personalInfo) {
        this.personalInfo = personalInfo;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }
}