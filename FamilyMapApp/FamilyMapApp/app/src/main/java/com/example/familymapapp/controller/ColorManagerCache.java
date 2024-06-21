package com.example.familymapapp.controller;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ColorManagerCache {
    private static ColorManagerCache instance = new ColorManagerCache();
    public static ColorManagerCache getInstance() {
        return instance;
    }
    private ColorManagerCache() {
        markerColors = new HashMap<>();
        colors = new ArrayList<>(Arrays.asList(BitmapDescriptorFactory.HUE_AZURE,
                BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_MAGENTA,
                BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_GREEN,
                BitmapDescriptorFactory.HUE_CYAN, BitmapDescriptorFactory.HUE_ROSE,
                BitmapDescriptorFactory.HUE_YELLOW, BitmapDescriptorFactory.HUE_ORANGE,
                BitmapDescriptorFactory.HUE_VIOLET));
        nextColorIndex = 0;
    }

    private Map<String, Float> markerColors;
    private ArrayList<Float> colors;
    int nextColorIndex;
    public Map<String, Float> getMarkerColors() {
        return markerColors;
    }
    public void addMarkerColor(String eventType, float color) {
        Float fColor = new Float(color);
        markerColors.put(eventType, fColor);
    }
    public float nextColor() {
        float newColor = colors.get(nextColorIndex);
        if(nextColorIndex != colors.size() - 1) {
            nextColorIndex++;
        }
        return newColor;
    }

    public void clearCache() {
        nextColorIndex = 0;
    }
}
