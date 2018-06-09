package com.google.ar.sceneform.samples.hellosceneform.services.image_export;

import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.core.Plane;

import java.util.ArrayList;
import java.util.Collection;

public class PlaneAnchorsToPointsMapper {
    public static Collection<Point> Map(Plane plane) {
        Collection<Point> points = new ArrayList<Point>();

        for(Anchor anchor : plane.getAnchors()) {
            // TODO: ITS FOR HORIZONTAL, CHANGE FOR VERTICAL
            points.add(new Point(anchor.getPose().tx(), anchor.getPose().ty(), 0.25f, 0));
        }

        return points;
    }
}
