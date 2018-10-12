package com.scorg.regform.models.form.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.regform.interfaces.CustomResponse;

import java.util.ArrayList;
import java.util.List;

public class UpdatedFormRequest implements CustomResponse {
    @SerializedName("header")
    @Expose
    private Header header;
    @SerializedName("results")
    @Expose
    private List<FieldRequest> fields = new ArrayList<FieldRequest>();

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public List<FieldRequest> getFields() {
        return fields;
    }

    public void setFields(List<FieldRequest> fields) {
        this.fields = fields;
    }
}