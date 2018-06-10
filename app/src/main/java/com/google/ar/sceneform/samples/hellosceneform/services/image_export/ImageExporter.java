package com.google.ar.sceneform.samples.hellosceneform.services.image_export;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collection;

public class ImageExporter {
    public static int PIXEL_PER_METER = 1000;

    public static Bitmap Export(Collection<Point> points) {
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

        int width = (int)((maxX + maxXSize / 2 - minX - minXSize / 2) * PIXEL_PER_METER);
        int height = (int)((maxY + maxYSize / 2 - minY - minYSize / 2) * PIXEL_PER_METER);

        width = width > 0 ? width : 1;
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        for(Point point : points) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);

            float left = (point.x - point.size / 2 - minX) * PIXEL_PER_METER;
            float right = (point.x + point.size / 2 - minX) * PIXEL_PER_METER;
            float top = (point.y - point.size / 2 - minY) * PIXEL_PER_METER;
            float bottom = (point.y + point.size / 2 - minY) * PIXEL_PER_METER;

            canvas.drawOval(
                    left,
                    top,
                    right,
                    bottom,
                    paint);
        }

        return bitmap;
    }
}
