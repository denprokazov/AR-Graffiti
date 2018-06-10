/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.samples.hellosceneform;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.samples.hellosceneform.helpers.ArPermissionHelper;
import com.google.ar.sceneform.samples.hellosceneform.helpers.LocationHelper;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class HelloSceneformActivity extends AppCompatActivity {
    private static final String TAG = HelloSceneformActivity.class.getSimpleName();

    private ArFragment arFragment;
    private ModelRenderable brushRenderable;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private int mCurrentColor;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sceneform);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

//        AddLocationButtonHandler();
//        AddPushButtonHandler();

        CreateBrushRenderable(GetNewBrushSize());
        AddArFragmentListeners();
        SetupLocationListener();


        AddSeekBarChangeListener();
    }

    public void AddSeekBarChangeListener() {
        SeekBar seekBar = (SeekBar) findViewById(R.id.mySeekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public float GetNewBrushSize() {
        SeekBar seekBar = findViewById(R.id.mySeekBar);
        float BRUSH_SIZE = 0.125f;

        return (float)seekBar.getProgress() / 100  + BRUSH_SIZE;
    }

    @SuppressLint("MissingPermission")
    private void LoadGraffitiesFromServer() {
        double graffitiLatitude = 53.890586; //53.8902951;
        double graffitiLongtitude = 27.565978; //27.5688288;

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double userLatitude = location.getLatitude();
        double userLongtitude = location.getLongitude();

        double distance = LocationHelper.distance(userLatitude, graffitiLatitude, userLongtitude, graffitiLongtitude, 0.0,0.0);
        double bearing = LocationHelper.bearing(userLatitude, graffitiLatitude, userLongtitude, graffitiLongtitude);

        float zTranslation = (float) (Math.sin(bearing) * distance);
        float xTranslation = (float) (Math.cos(bearing) * distance);

        Toast.makeText(this, String.format("Distance, Bearing: %f %f. xTranslation, zTranslation: %f %f", distance, bearing, xTranslation, zTranslation), Toast.LENGTH_LONG).show();

//        Toast.makeText(this, String.format("xTranslation, zTranslation: %f %f", xTranslation, zTranslation), Toast.LENGTH_LONG).show();

        Session session = arFragment.getArSceneView().getSession();
        Pose pose = arFragment.getArSceneView().getArFrame().getCamera().getPose();

        Pose graffitiPose = pose.extractTranslation()
                .compose(Pose.makeTranslation(zTranslation, 0.5f, xTranslation))
                .extractTranslation();

//        Toast.makeText(this, String.format("%s", graffitiPose.getTranslation().toString()), Toast.LENGTH_LONG).show();

        AnchorNode anchorNode = new AnchorNode(session.createAnchor(graffitiPose));

        TransformableNode brush = new TransformableNode(arFragment.getTransformationSystem());
        brush.setParent(anchorNode);
        brush.setRenderable(brushRenderable);
        brush.select();
    }

    private void SetupLocationListener() {
        ArPermissionHelper.requestPermission(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        final Activity activity = this;

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double graffitiLatitude = 53.8902951; // 53.890586;
                double graffitiLongtitude = 27.5688288; // 27.565978;

                double latitude = location.getLatitude();
                double longtitude = location.getLongitude();

                double distance = LocationHelper.distance(latitude, graffitiLatitude, longtitude, graffitiLongtitude, 0.0,0.0);
                double bearing = LocationHelper.bearing(latitude, graffitiLatitude, longtitude, graffitiLongtitude);

                float xTranslation = (float) (Math.sin(bearing) * distance);
                float zTranslation = (float) (Math.cos(bearing) * distance);

                String toast = String.format( "Real Lat, Lang   : %9.7f %9.7f", latitude, longtitude);
                String toast2 = String.format("Goal Lat, Lang   : %9.7f %9.7f", graffitiLatitude, graffitiLongtitude);
                String toast3 = String.format("Distance, Bearing: %9.7f %9.7f", distance, bearing);
                String toast4 = String.format("XCoord,   ZCoord : %9.7f %9.7f", xTranslation , zTranslation);

                ((TextView)activity.findViewById(R.id.log_gps)).setText(String.format("%s\n%s\n%s\n%s", toast, toast2, toast3, toast4));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(activity, "onStatusChanged", Toast.LENGTH_SHORT);
            }

            public void onProviderEnabled(String provider) {
                Toast.makeText(activity, "onProviderEnabled", Toast.LENGTH_SHORT);
            }

            public void onProviderDisabled(String provider) {
                Toast.makeText(activity, "onProviderDisabled", Toast.LENGTH_SHORT);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1
            );
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private void AddArFragmentListeners() {
        arFragment.getArSceneView().getScene().setOnTouchListener((hitTestResult, motionEvent) -> {
            if (brushRenderable == null) {
                return true;
            }

            Frame frame = arFragment.getArSceneView().getArFrame();

            for (HitResult hitResult : frame.hitTest(motionEvent)) {
                if(!(hitResult.getTrackable() instanceof Plane)) {
                    continue;
                }

                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());

                TransformableNode locationView = new TransformableNode(arFragment.getTransformationSystem());
                locationView.setParent(anchorNode);
                locationView.setRenderable(brushRenderable);
                locationView.select();
            }

            return true;
        });
    }

    private void CreateBrushRenderable(float brushSize) {
        int R = mCurrentColor % 256;
        int G = (mCurrentColor / 256) % 256;
        int B = (mCurrentColor / 256 / 256) % 256;
        int A = (mCurrentColor / 256 / 256 / 256);

        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.argb(A, R, G, B)))
                .thenAccept(
                        material -> {
                            brushRenderable =
                                    ShapeFactory.makeSphere(brushSize, new Vector3(0.0f, 0.1f, 0.0f), material);
                        });
    }

    public void clickButton(View view) {
        Drawable background = view.getBackground();

        if (background instanceof ColorDrawable)
            mCurrentColor = ((ColorDrawable) background).getColor();

//        Log.d(TAG, String.format("COLOR PRESSED: %d", color));
        CreateBrushRenderable(GetNewBrushSize());
    }
}