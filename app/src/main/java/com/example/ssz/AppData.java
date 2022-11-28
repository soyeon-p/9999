package com.example.ssz;

import android.app.Application;

import com.naver.maps.geometry.LatLng;

import java.util.ArrayList;

public class AppData extends Application {
    private LatLng startLocation;
    private ArrayList<String> roadAddressGetCamera;
    private ArrayList<String> timestampGetCamera;
    private ArrayList<String> storeNameGetCamera;
    private String comment;
    private int routeId;

    public ArrayList<String> getStoreNameGetCamera() {
        return this.storeNameGetCamera;
    }

    public void setStoreNameGetCamera(ArrayList<String> storeNameGetCamera) {
        this.storeNameGetCamera = storeNameGetCamera;
    }

    public ArrayList<String> getTimestampGetCamera() {
        return this.timestampGetCamera;
    }

    public void setTimestampGetCamera(ArrayList<String> timestampGetCamera) {
        this.timestampGetCamera = timestampGetCamera;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getRouteId() {
        return this.routeId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public LatLng getStartLocation() {
        return this.startLocation;
    }

    public void setRoadAddressGetCamera(ArrayList<String> roadAddressGetCamera) {
        this.roadAddressGetCamera = roadAddressGetCamera;
    }

    public ArrayList<String> getRoadAddressGetCamera() {
        return this.roadAddressGetCamera;
    }
}
