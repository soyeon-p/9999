package com.example.ssz.db.dto;

import com.naver.maps.geometry.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class RouteDTO implements Serializable {
    private String uuid;
    private int route_id;
    private String comment;
    private int like_count;
    private ArrayList<LatLng> location;

    public RouteDTO() {
        this.uuid = "admin";
        this.route_id = 1;
        this.comment = "";
        this.location = new ArrayList<>();
        this.like_count = 0;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setComment(String content) {
        this.comment = content;
    }

    public void setLocation(ArrayList<LatLng> location) {
        this.location = location;
    }

    public void setRoute_id(int routeId) {
        this.route_id = routeId;
    }

    public int getRoute_id() {
        return this.route_id;
    }

    public String getComment() {
        return this.comment;
    }

    public int getLike_count() {
        return this.like_count;
    }

    public String getUuid() {
        return this.uuid;
    }

    public ArrayList<LatLng> getLocation() {
        return this.location;
    }
}
