package com.google.ar.sceneform.samples.hellosceneform.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AreaResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private List<AreaResponceMessage> message = null;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<AreaResponceMessage> getMessages() {
        return message;
    }

    public void setMessage(List<AreaResponceMessage> message) {
        this.message = message;
    }

}

