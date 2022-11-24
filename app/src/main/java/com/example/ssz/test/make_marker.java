package com.example.ssz.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ssz.AppData;
import com.example.ssz.R;
import com.example.ssz.db.DBConnect;
import com.example.ssz.db.dto.RouteDTO;
import com.example.ssz.naveropenapi.ApiSecretKey;
import com.example.ssz.naveropenapi.Direction;
import com.example.ssz.naveropenapi.Geocoding;
import com.example.ssz.naveropenapi.ReverseGeocoding;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.MarkerIcons;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class make_marker extends AppCompatActivity implements OnMapReadyCallback {
    private Button createRouteBtn;
    private static NaverMap naverMap;
    private ArrayList<LatLng> location;
    private ArrayList<LatLng> clickMarker;
    private MapView mapView;
    private Set<LatLng> clickMarkerTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initialize();

        super.onCreate(savedInstanceState);
        setContentView(com.example.ssz.R.layout.activity_make_marker);
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient(ApiSecretKey.X_NCP_APIGW_API_KEY_ID));
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        createRouteBtn = findViewById(R.id.route_view);
        createRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouteDTO routeDTO = new RouteDTO();
                routeDTO.setLocation(clickMarker);

                DBConnect.saveRoute(routeDTO);

                Intent intent = new Intent(make_marker.this, Comment.class);
                startActivity(intent);
            }
        });
    }

    private void initialize() {
        clickMarkerTest = new HashSet<>();
        location = new ArrayList<>();
        clickMarker = new ArrayList<>();

        setLocation();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        setNaverMap(naverMap);
        setMarker();
    }

    private void setNaverMap(NaverMap naverMap) {
        this.naverMap = naverMap;

        //배경 지도 선택
        naverMap.setMapType(NaverMap.MapType.Navi);
        // 건물 표시
        naverMap.setLayerGroupEnabled(naverMap.LAYER_GROUP_BUILDING, true);
    }

    private void setLocation() {
        new Thread(() -> {
            ArrayList<String> cameraLocation = new ArrayList<>();
            //cameraLocation.add("인천 미추홀구 인하로 53");
            cameraLocation.add("서울 중구 세종대로110");
            cameraLocation.add("서울 중구 세종대로40");
            for (int i = 0; i < cameraLocation.size(); i++) {
                LatLng locationCoordinate = Geocoding.convertAddressToCoordinate(cameraLocation.get(i));
                if (locationCoordinate != null) {
                    System.out.println("+++ adrress : " + locationCoordinate);
                    location.add(locationCoordinate);
                }
            }
        }).start();
    }

    private void setMarker() {
        for (int i = 0; i < location.size(); i++) {
            Marker marker = new Marker();
            setClickMarkerListener(marker);
            marker.setPosition(new LatLng(location.get(i).longitude, location.get(i).latitude));
            //clickMarker.add(marker.getPosition());
            marker.setMap(naverMap);
        }
    }



    // 마커 클릭 시 색 변경.
    private void setClickMarkerListener(Marker marker) {
        marker.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                if (!clickMarkerTest.contains(marker.getPosition())) {
                    clickMarkerTest.add(marker.getPosition());
                    System.out.println("+++ click marker position" + marker.getPosition());
                    clickMarker.add(new LatLng(marker.getPosition().longitude, marker.getPosition().latitude));
                    marker.setIcon(MarkerIcons.BLACK);
                    marker.setIconTintColor(Color.RED);
                }
/*                naverMap.setOnSymbolClickListener(symbol -> {
                    if (!clickMarkerTest.contains(symbol.getPosition())) {
                        clickMarkerTest.add(symbol.getPosition());
                        System.out.println("+++ click marker position" + symbol.getPosition());
                        clickMarker.add(symbol.getPosition());
                        marker.setIcon(MarkerIcons.BLACK);
                        marker.setIconTintColor(Color.RED);
                    }
                    return true;
                });*/
                return true;
            }
        });
    }
}