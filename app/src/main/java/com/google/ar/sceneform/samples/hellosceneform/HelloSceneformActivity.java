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
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.ar.sceneform.samples.hellosceneform.services.image_export.ImageExporter;
import com.google.ar.sceneform.samples.hellosceneform.services.image_export.PlaneAnchorsToPointsMapper;
import com.google.ar.sceneform.samples.hellosceneform.services.image_export.Point;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.OutputStream;
import java.util.Collection;

public class HelloSceneformActivity extends AppCompatActivity {
    private static final String TAG = HelloSceneformActivity.class.getSimpleName();

    private ArFragment arFragment;
    private ModelRenderable brushRenderable;

    private double testLatitude = 53.890447;
    private double testLongitude = 27.5687115;

    private double currentLatitude = 53.8903810;
    private double currentLongitude = 27.5685724;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ux);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        CreateBrushRenderable();

        AddLocationButtonHandler();
        AddPushButtonHandler();
        AddArFragmentListeners();

    }

    private void AddLocationButtonHandler() {
        final Button button = findViewById(R.id.location);
        button.setOnClickListener(v -> {
            double distance = LocationHelper.distance(testLatitude, currentLatitude, testLongitude, currentLongitude, 0.0,0.0);
            double bearing = LocationHelper.bearing(testLatitude, currentLatitude, testLongitude, currentLongitude);

            Session session = arFragment.getArSceneView().getSession();
            Pose pose = arFragment.getArSceneView().getArFrame().getCamera().getPose();

            pose.compose(Pose.makeTranslation(0, 0.5f, 0));

            AnchorNode anchorNode = new AnchorNode(session.createAnchor(pose));
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            TransformableNode brush = new TransformableNode(arFragment.getTransformationSystem());
            brush.setParent(anchorNode);
            brush.setRenderable(brushRenderable);
            brush.select();
        });
    }

    private void AddPushButtonHandler() {
        final Button button = findViewById(R.id.post_image);
        button.setOnClickListener(v -> {
            Collection<Plane> planes = arFragment.getArSceneView().getSession().getAllTrackables(Plane.class);

            for(Plane plane : planes) {
                Collection<Point> points = PlaneAnchorsToPointsMapper.Map(plane);
                OutputStream outputStream = ImageExporter.Export(points);
            }
        });
    }

    private void SetupLocationListener(TextView locationTextView) {
        ArPermissionHelper.requestPermission(this);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                locationTextView.setText(location.toString());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
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

                Plane plane = (Plane) hitResult.getTrackable();

                if(plane.getType() == Plane.Type.VERTICAL) {
                    continue;
                }

                Log.d(TAG, String.valueOf(plane.hashCode()));

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

    private void CreateBrushRenderable() {
        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.RED))
                .thenAccept(
                        material -> {
                            brushRenderable =
                                    ShapeFactory.makeSphere(0.1f, new Vector3(0.0f, 0.15f, 0.0f), material);
                        });
    }
}
