package com.google.ar.sceneform.samples.hellosceneform.services.image_export;

import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.core.Plane;

import java.util.ArrayList;
import java.util.Collection;

public class PlaneAnchorsToPointsMapper {
    public static Collection<Point> Map(Collection<Anchor> anchors) {
        Collection<Point> points = new ArrayList<Point>();

        for(Anchor anchor : anchors) {
            points.add(new Point(anchor.getPose().tx(), anchor.getPose().tz(), 0.2f, 0));
        }

        return points;
    }
}
