package com.google.ar.sceneform.samples.hellosceneform.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AreaResponceMessage {

    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("gang")
    @Expose
    private String gang;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(Integer longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(Integer latitude) {
        this.latitude = latitude;
    }

    public String getGang() {
        return gang;
    }

    public void setGang(String gang) {
        this.gang = gang;
    }

}