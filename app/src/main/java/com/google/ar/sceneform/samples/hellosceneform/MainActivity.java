package com.google.ar.sceneform.samples.hellosceneform;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private List<LatLng> latLngs = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        latLngs.add(new LatLng(53.8999964, 27.5666644));
        latLngs.add(new LatLng(53.899654, 27.56756));
        
        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.map_screen);
        mapView = findViewById(R.id.mapView);
        ImageButton arButton = findViewById(R.id.openArButton);

        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(mapboxMap -> {
            for (LatLng latLng: latLngs) {
                drawPolygon(mapboxMap, latLng, "#E82020");
            }
        });

        arButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HelloSceneformActivity.class);
            startActivity(intent);
        });

//        Thread thread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try  {
//                    OkHttpClient client = new OkHttpClient();
//
//                    Request request = new Request.Builder()
//                            .url("http://176.9.2.82:6778/map/zones/")
//                            .get()
//                            .build();
//
//                    Response response = client.newCall(request).execute();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        thread.start();


    }

    private void drawPolygon(MapboxMap mapboxMap, LatLng center, String color) {
        List<LatLng> polygon = new ArrayList<>();
        double delta = 0.005;
        polygon.add(new LatLng(center.getLatitude()+delta, center.getLongitude()-delta));
        polygon.add(new LatLng(center.getLatitude()+delta, center.getLongitude()+2*delta));
        polygon.add(new LatLng(center.getLatitude()-2*delta, center.getLongitude() + 2*delta));
        polygon.add(new LatLng(center.getLatitude()-2*delta, center.getLongitude()-2*delta));
        polygon.add(new LatLng(center.getLatitude()+delta, center.getLongitude()-2*delta));
        mapboxMap.addPolygon(new PolygonOptions()
                .addAll(polygon)
                .fillColor(Color.parseColor(color)).alpha(0.5f));
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
