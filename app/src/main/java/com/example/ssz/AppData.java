package com.example.ssz;

import android.app.Application;

import com.naver.maps.geometry.LatLng;

import java.util.ArrayList;

public class AppData extends Application {
    private LatLng startLocation;
    private ArrayList<String> cameraLocation;

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public LatLng getStartLocation() {
        return this.startLocation;
    }

    public void setCameraLocation(ArrayList<String> cameraLocation) {
        this.cameraLocation = cameraLocation;
    }

    public ArrayList<String> getCameraLocation() {
        return this.cameraLocation;
    }
}
