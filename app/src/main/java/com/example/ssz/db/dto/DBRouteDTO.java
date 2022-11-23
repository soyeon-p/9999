package com.example.ssz.db.dto;

import com.naver.maps.geometry.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

public class DBRouteDTO implements Serializable {
    public String uuid;
    public int route_id;
    public String comment;
    public int like_count;
    public ArrayList<DBRouteLocationDTO> location;
}
