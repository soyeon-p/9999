package com.example.ssz.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ssz.R;
import com.example.ssz.db.DBConnect;
import com.example.ssz.db.dto.RouteDTO;
import com.example.ssz.naveropenapi.ApiSecretKey;
import com.example.ssz.naveropenapi.Geocoding;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.MarkerIcons;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class make_marker extends AppCompatActivity implements OnMapReadyCallback {
    private ArrayList<String> roadAddressGetUsingCamera;
    private ArrayList<String> storeNameGetUsingCamera;
    private ArrayList<String> timestampGetUsingCamera;

    private Button createRouteBtn;
    private static NaverMap naverMap;
    private ArrayList<LatLng> location;
    private ArrayList<LatLng> clickMarkerLocation;
    private MapView mapView;
    private Set<LatLng> clickMarkerTest;

    private ArrayList<String> cameraStoreName;

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

        roadAddressGetUsingCamera.add(getIntent().getStringExtra("address"));
        cameraStoreName.add(getIntent().getStringExtra("storeName"));
        Toast.makeText(getApplicationContext(), cameraStoreName.get(0), Toast.LENGTH_SHORT).show();

        createRouteBtn = findViewById(R.id.route_view);
        createRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouteDTO routeDTO = new RouteDTO();
                routeDTO.setLocation(clickMarkerLocation);

                DBConnect.saveRoute(routeDTO);

                Intent intent = new Intent(make_marker.this, Comment.class);
                startActivity(intent);
            }
        });
    }

    private void initialize() {
        roadAddressGetUsingCamera = new ArrayList<>();
        cameraStoreName = new ArrayList<>();
        clickMarkerTest = new HashSet<>();
        location = new ArrayList<>();
        clickMarkerLocation = new ArrayList<>();

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
            //roadAddressGetUsingCamera.add("인천 미추홀구 인하로 53");
            //roadAddressGetUsingCamera.add("인천 남구 경인남길30번길 61");
            //roadAddressGetUsingCamera.add("서울 중구 세종대로110");
            //roadAddressGetUsingCamera.add("서울 중구 세종대로40");
            for (int i = 0; i < roadAddressGetUsingCamera.size(); i++) {
                LatLng locationCoordinate = Geocoding.convertAddressToCoordinate(roadAddressGetUsingCamera.get(i));
                if (locationCoordinate != null) {
                    location.add(locationCoordinate);
                }
            }
        }).start();
    }

    private void setMarker() {
        for (int i = 0; i < location.size(); i++) {
            Marker marker = new Marker();
            setClickMarkerListener(marker);
            LatLng markerLocation = new LatLng(location.get(i).longitude, location.get(i).latitude);
            marker.setPosition(markerLocation);
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
                    //System.out.println("+++ click marker position" + marker.getPosition());
                    clickMarkerLocation.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));

                    marker.setIcon(MarkerIcons.BLACK);
                    marker.setIconTintColor(Color.RED);
                }
                return true;
            }
        });
    }
}