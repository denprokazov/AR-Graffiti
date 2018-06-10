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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.samples.hellosceneform.helpers.ArPermissionHelper;
import com.google.ar.sceneform.samples.hellosceneform.helpers.LocationHelper;
import com.google.ar.sceneform.samples.hellosceneform.models.Graffiti;
import com.google.ar.sceneform.samples.hellosceneform.models.GraffitiParentNode;
import com.google.ar.sceneform.samples.hellosceneform.services.image_export.ImageExporter;
import com.google.ar.sceneform.samples.hellosceneform.services.image_export.PlaneAnchorsToPointsMapper;
import com.google.ar.sceneform.samples.hellosceneform.services.image_export.Point;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Map;

public class HelloSceneformActivity extends AppCompatActivity {
    private static final String TAG = HelloSceneformActivity.class.getSimpleName();

    private ArFragment arFragment;
    private ModelRenderable brushRenderable;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private ViewRenderable grafittiViewRenderable;

    private int mCurrentColor;
    
    double graffitiLatitude = 53.8902966;
    double graffitiLongtitude = 27.5689460;

    private final Gson gson = new Gson();

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sceneform);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        SetupLocationListener();
        CreateBrushRenderable();
        AddArFragmentListeners();

        AddSeekBarChangeListener();

        MediaManager.init(this);

        ViewRenderable.builder()
                .setView(this, R.layout.graffity)
                .build()
                .thenAccept(renderable -> grafittiViewRenderable = renderable);

    }

    public void AddSeekBarChangeListener() {
        SeekBar seekBar = (SeekBar) findViewById(R.id.mySeekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CreateBrushRenderable();
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
        float BRUSH_SIZE = 0.075f;

        return (float) seekBar.getProgress() / 100 + BRUSH_SIZE;
    }

    private void SetupLocationListener() {
        ArPermissionHelper.requestPermission(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        final Activity activity = this;

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longtitude = location.getLongitude();

                String toast = String.format("Real Lat, Lang   : %9.7f %9.7f", latitude, longtitude);

                Toast.makeText(activity, toast, Toast.LENGTH_LONG);

                double distance = LocationHelper.distance(latitude, graffitiLatitude, longtitude, graffitiLongtitude, 0.0, 0.0);
                double bearing = LocationHelper.bearing(latitude, graffitiLatitude, longtitude, graffitiLongtitude);

                float xTranslation = (float) (Math.sin(bearing) * distance);
                float zTranslation = (float) (Math.cos(bearing) * distance);

                toast = String.format("Real Lat, Lang   : %9.7f %9.7f", latitude, longtitude);
                String toast2 = String.format("Goal Lat, Lang   : %9.7f %9.7f", graffitiLatitude, graffitiLongtitude);
                String toast3 = String.format("Distance, Bearing: %9.7f %9.7f", distance, bearing);
                String toast4 = String.format("XCoord,   ZCoord : %9.7f %9.7f", xTranslation, zTranslation);

                ((TextView) activity.findViewById(R.id.log_gps)).setText(String.format("%s\n%s\n%s\n%s", toast, toast2, toast3, toast4));
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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private void AddArFragmentListeners() {
        arFragment.getArSceneView().getScene().setOnTouchListener((hitTestResult, motionEvent) -> {
            if (brushRenderable == null) {
                return true;
            }

            Frame frame = arFragment.getArSceneView().getArFrame();

            for (HitResult hitResult : frame.hitTest(motionEvent)) {
                if (!(hitResult.getTrackable() instanceof Plane)) {
                    continue;
                }

                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setWorldRotation(new Quaternion());
                anchorNode.setLocalRotation(new Quaternion());
                anchorNode.setParent(arFragment.getArSceneView().getScene());

                Node locationView = new Node();
                locationView.setParent(anchorNode);
                locationView.setWorldRotation(new Quaternion());
                locationView.setLocalRotation(new Quaternion());
                locationView.setRenderable(brushRenderable);
            }

            return true;
        });
    }

    private void CreateBrushRenderable() {
        int R = mCurrentColor % 256;
        int G = (mCurrentColor / 256) % 256;
        int B = (mCurrentColor / 256 / 256) % 256;
        int A = 0;

        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.argb(A, R, G, B)))
                .thenAccept(
                        material -> {
                            brushRenderable =
                                    ShapeFactory.makeSphere(GetNewBrushSize(), new Vector3(0.0f, 0.1f, 0.0f), material);
                        });
    }

    public void uploadImage(View view) {
        Log.d(TAG, arFragment.getArSceneView().getScene().getCamera().getWorldPosition().toString());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(view.getContext(), "LOCATION IS NOT PERMITTED", Toast.LENGTH_LONG).show();
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double latitude = location.getLatitude();
        double longtitude = location.getLongitude();

        String toast = String.format("Lat, Lang: %9.7f %9.7f", latitude, longtitude);

        Collection<Plane> planes = arFragment.getArSceneView().getSession().getAllTrackables(Plane.class);

        Collection<Point> points = PlaneAnchorsToPointsMapper.Map(arFragment.getArSceneView().getSession().getAllAnchors());
        Bitmap bitmap = ImageExporter.Export(points);

//        float[] translations = plane.getCenterPose().getTranslation();
//
//        float x = plane.getCenterPose().tx();
//        float z = plane.getCenterPose().tz();

//        Log.d(TAG, translations.toString());
//
//        double graffityLatitude = latitude + z / 111111.1;
//        double graffityLongtitude = longtitude + x / 111111.1 * Math.cos(graffityLatitude);

        String uuid = UUID.randomUUID().toString();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();

        final Context context = this;

        String requestId = MediaManager.get().upload(byteArray)
                .unsigned("kxwlvi9l")
                .option("public_id", uuid)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {

                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {

                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Toast.makeText(context, "SUCCESS IMAGE UPLOAD", Toast.LENGTH_LONG).show();
                        for(Anchor anchor : arFragment.getArSceneView().getSession().getAllAnchors()) {
                            anchor.detach();
                        }
                        uploadGraffiti((String)resultData.get("secure_url"));
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(context, "ERROR IMAGE UPLOAD", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {

                    }
                })
                .dispatch();
    }

    @SuppressLint("MissingPermission")
    public void placeOnGps(View view) {
        new DownloadGraffitiesTask().execute();
    }

    public void uploadGraffiti(String imageUrl) {

        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");

                String a = String.format("{\n\t\"image\": \"%s\",\n\t\"id\": \"%s\",\n\t\"userid\": \"123\",\n\t\"longitude\": %s,\n\t\"latitude\": %s,\n\t\"height\": 0,\n\t\"message\": \"sobaka\",\n\t\"gang\": \"kek\"\n}",
                        imageUrl.toString(), UUID.randomUUID().toString(), Double.toString(location.getLongitude()), Double.toString(location.getLatitude()));
                RequestBody body = RequestBody.create(mediaType, a);
                Request request = new Request.Builder()
                        .url("http://176.9.2.82:6778/graffity")
                        .post(body)
                        .addHeader("content-type", "application/json")
                        .addHeader("authorization", "Bearer 123")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    Log.d(TAG, response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @SuppressLint("MissingPermission")
    public GraffitiParentNode updateGraffitiesFromServer() {
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double latitude = location.getLatitude();
        double longtitude = location.getLongitude();

        LatLng userPosition = new LatLng(latitude, longtitude);

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                String.format("{\"longitude\": %s," +
                "\"latitude\": %s}", Double.toString(userPosition.getLatitude()),
                        Double.toString(userPosition.getLongitude())));
        Request request = new Request.Builder()
                .url("http://176.9.2.82:6778/near/graffity/")
                .post(body)
                .addHeader("content-type", "application/json")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();

            String responseBody = response.body().string();
            GraffitiParentNode graffitiParentNodeParentNode = gson.fromJson(responseBody, GraffitiParentNode.class);

            return graffitiParentNodeParentNode;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
}

    public void clickRedButton(View view) {
        mCurrentColor = android.graphics.Color.RED;
        CreateBrushRenderable();
    }

    public void clickGreenButton(View view) {
        mCurrentColor = android.graphics.Color.GREEN;
        CreateBrushRenderable();
    }

    public void clickBlueButton(View view) {
        mCurrentColor = android.graphics.Color.BLUE;
        CreateBrushRenderable();
    }

    @SuppressLint("MissingPermission")
    private void updateGrafiitiFromServer(Graffiti graffiti) {
        if(grafittiViewRenderable == null) {
            return;
        }

        final ImageView view = grafittiViewRenderable.getView().findViewById(R.id.graffity_in_ar);
        ImageView testView = findViewById(R.id.test_image_loading);

        new DownloadImageTask(view).execute(graffiti.getImage());
        new DownloadImageTask(testView).execute(graffiti.getImage());

        @SuppressLint("MissingPermission")
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double userLatitude = location.getLatitude();
        double userLongtitude = location.getLongitude();

        double distance = LocationHelper.distance(userLatitude, graffitiLatitude, userLongtitude, graffitiLongtitude, 0.0, 0.0);
        double bearing = LocationHelper.bearing(userLatitude, graffitiLatitude, userLongtitude, graffitiLongtitude);

        if(arFragment.getArSceneView().getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
            return;
        }

//        Toast.makeText(this, String.format("Dist, bear: %f %f", distance, bearing), Toast.LENGTH_LONG).show();

        float xTranslation = (float) (Math.sin(bearing) * distance);
        float zTranslation = (float) (Math.cos(bearing) * distance);

        Node graffitiArNode = new Node();

        graffitiArNode.setRenderable(grafittiViewRenderable);
//        graffiti.setWorldPosition(new Vector3(xTranslation, 0, zTranslation));
        graffitiArNode.setWorldPosition(new Vector3(4, 0, 0));
        graffitiArNode.setWorldRotation(new Quaternion());
        graffitiArNode.setLocalRotation(new Quaternion());
        graffitiArNode.setParent(arFragment.getArSceneView().getScene());

//        graffitiArNode.setOnTapListener((hitTestResult, motionEvent) -> Toast.makeText(this, "CLICK ON AR GRAFFITI", Toast.LENGTH_LONG));
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        @WorkerThread
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        @MainThread
        protected void onPostExecute(Bitmap result) {
            Toast.makeText(bmImage.getContext(), "I DOWNLOADED BITMAP", Toast.LENGTH_LONG);
            bmImage.setImageBitmap(result);
        }
    }

    private class DownloadGraffitiesTask extends AsyncTask<Void, Void, GraffitiParentNode> {
        @Override
        @WorkerThread
        protected GraffitiParentNode doInBackground(Void... voids)  {
            return updateGraffitiesFromServer();
        }

        @Override
        @MainThread
        protected void onPostExecute(GraffitiParentNode graffitiParentNode) {
            for(Graffiti graffiti : graffitiParentNode.getMessage()) {
                updateGrafiitiFromServer(graffiti);
            }
        }
    }
}