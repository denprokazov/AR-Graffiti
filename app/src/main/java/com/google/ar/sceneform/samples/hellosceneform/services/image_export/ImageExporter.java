package com.google.ar.sceneform.samples.hellosceneform.services.image_export;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collection;

public class ImageExporter {
    public static int PIXEL_PER_METER = 250;

    public static OutputStream Export(Collection<Point> points) {
        float minX = 0;
        float minXSize = 0;

        float minY = 0;
        float minYSize = 0;

        float maxX = 0;
        float maxXSize = 0;

        float maxY = 0;
        float maxYSize = 0;

        for(Point point : points) {
            if(point.x < minX) {
                minX = point.x;
                minXSize = point.size;
            }

            if(point.x > maxX) {
                maxX = point.x;
                maxXSize = point.size;
            }

            if(point.y < minY) {
                minY = point.y;
                minYSize = point.size;
            }

            if(point.y > maxY) {
                maxY = point.y;
                maxYSize = point.size;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(
                (int)(maxX + maxXSize / 2 - minX - minXSize / 2) * PIXEL_PER_METER,
                (int)(maxY + maxYSize / 2 - minY - minYSize / 2) * PIXEL_PER_METER,
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);

        for(Point point : points) {
            Paint paint = new Paint();
            paint.setColor(point.color);

            float left = point.x - point.size / 2;
            float right = point.x - point.size / 2;
            float top = point.y - point.size / 2;
            float bottom = point.y - point.size / 2;

            canvas.drawOval(left, top, right, bottom, paint);
        }

        OutputStream fos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        return fos;
    }
}
