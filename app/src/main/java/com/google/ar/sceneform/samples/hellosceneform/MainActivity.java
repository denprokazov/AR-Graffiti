package com.google.ar.sceneform.samples.hellosceneform;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.map_screen);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(mapboxMap -> drawPolygon(mapboxMap, new LatLng(45.522585,-122.685699)));
    }

    private void drawPolygon(MapboxMap mapboxMap, LatLng center) {
        List<LatLng> polygon = new ArrayList<>();
        double delta = 0.005;
        polygon.add(new LatLng(center.getLatitude()+delta, center.getLongitude()-delta));
        polygon.add(new LatLng(center.getLatitude()+delta, center.getLongitude()+2*delta));
        polygon.add(new LatLng(center.getLatitude()-2*delta, center.getLongitude() + 2*delta));
        polygon.add(new LatLng(center.getLatitude()-2*delta, center.getLongitude()-2*delta));
        polygon.add(new LatLng(center.getLatitude()+delta, center.getLongitude()-2*delta));
        mapboxMap.addPolygon(new PolygonOptions()
                .addAll(polygon)
                .fillColor(Color.parseColor("#3bb2d0")));
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
