package com.example.ssz.db;

import com.naver.maps.geometry.LatLng;

import java.util.ArrayList;

public interface DBArrayCallback {
    void onCallback(ArrayList<ArrayList<LatLng>> route, int routeId);
}
