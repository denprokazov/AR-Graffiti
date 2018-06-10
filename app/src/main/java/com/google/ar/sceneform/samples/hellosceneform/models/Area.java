package com.google.ar.sceneform.samples.hellosceneform.models;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Area {
    public double Latitude;
    public double Longitude;
    public String Gang;

    public Area(LatLng coords, String gang) {
        Latitude = coords.getLatitude();
        Longitude = coords.getLongitude();

        Gang = gang;
    }
}
