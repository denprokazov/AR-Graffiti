package com.google.ar.sceneform.samples.hellosceneform;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.google.ar.sceneform.samples.hellosceneform.helpers.ArPermissionHelper;
import com.google.ar.sceneform.samples.hellosceneform.models.Area;
import com.google.ar.sceneform.samples.hellosceneform.models.AreaResponceMessage;
import com.google.ar.sceneform.samples.hellosceneform.models.AreaResponse;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private List<Area> areas = new ArrayList<Area>();

    private String userId = "";
    private final Gson gson = new Gson();

    private LocationManager locationManager;
    private LocationListener locationListener;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_screen);
        ArPermissionHelper.requestPermission(this);

        
        Mapbox.getInstance(this, getString(R.string.access_token));

        mapView = findViewById(R.id.mapView);
        ImageButton arButton = findViewById(R.id.openArButton);


        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(mapboxMap -> {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, setupLocationChangeListener(mapboxMap));

            getZones(mapboxMap);

        });

        arButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HelloSceneformActivity.class);
            startActivity(intent);
        });
    }

    private void getZones(MapboxMap mapboxMap) {
        Thread thread = new Thread(() -> {
            try  {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://176.9.2.82:6778/map/zones")
                        .get()
                        .build();

                Response response = client.newCall(request).execute();

                AreaResponse areas = gson.fromJson(response.body().charStream(), AreaResponse.class);

                for (AreaResponceMessage area: areas.getMessages()) {
                    String color = "#E82020";
                    if(area.getGang().equals("Russia")) {
                        color = "#42f45f";
                    }
                    drawPolygon(mapboxMap, area.getLatitude(), area.getLongitude(), color);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    @NonNull
    private LocationListener setupLocationChangeListener(MapboxMap mapboxMap) {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double userLatitude = location.getLatitude();
                double userLongtitude = location.getLongitude();

                List<Marker> existingMarkers = mapboxMap.getMarkers();

                for(Marker marker : existingMarkers) {
                    mapboxMap.removeMarker(marker);
                }


                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(userLatitude, userLongtitude)));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    private void drawPolygon(MapboxMap mapboxMap, double latitude, double longitude, String color) {
        List<LatLng> polygons = new ArrayList<>();
        double random = Math.random() / 100;

        double delta = 0.003 + random;
        polygons.add(new LatLng(latitude+delta, longitude-delta));
        polygons.add(new LatLng(latitude+delta, longitude+2*delta));
        polygons.add(new LatLng(latitude-2*delta, longitude+ 2*delta));
        polygons.add(new LatLng(latitude-2*delta, longitude-2*delta));
        polygons.add(new LatLng(latitude+delta, longitude-2*delta));
        PolygonOptions polygonOptions = new PolygonOptions().fillColor(Color.parseColor(color)).alpha(0.05f)
                .addAll(polygons);
        mapboxMap.addPolygon(polygonOptions);
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
