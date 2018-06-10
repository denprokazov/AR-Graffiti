package com.google.ar.sceneform.samples.hellosceneform.services.image_export;

import com.cloudinary.Transformation;
import com.cloudinary.Url;
import com.cloudinary.android.MediaManager;

public class ReceiveImage {
    public static void receiveImage(String filename) {
        if(filename == null) {
            filename = "0ef1a5c8-6e07-48e4-8be3-f3da19488261.png";
        }

        String url = MediaManager.get().url().secure(true).generate();
    }
}
