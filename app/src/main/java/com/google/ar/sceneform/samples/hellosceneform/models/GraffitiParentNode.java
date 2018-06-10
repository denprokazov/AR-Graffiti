package com.google.ar.sceneform.samples.hellosceneform.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GraffitiParentNode {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private List<Graffiti> message = null;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<Graffiti> getMessage() {
        return message;
    }

    public void setMessage(List<Graffiti> message) {
        this.message = message;
    }

}