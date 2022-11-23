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
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.MarkerIcons;

import java.util.ArrayList;
import java.util.Vector;

public class make_marker extends AppCompatActivity implements OnMapReadyCallback {
    private Button createRouteBtn;
    private static NaverMap naverMap;
    private ArrayList<LatLng> location;
    private Vector<Marker> marker;
    private int[] markerColor;
    private PathOverlay pathOverlay;
    private MapView mapView;

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
                routeDTO.setLocation(location);

                DBConnect.saveRoute(routeDTO);

                Intent intent = new Intent(make_marker.this, Comment.class);
                startActivity(intent);
            }
        });
    }

    private void initialize() {
        location = new ArrayList<>();
        marker = new Vector<>();
        pathOverlay = new PathOverlay();
        markerColor = new int[7];
        markerColor[0] = Color.rgb(255, 0, 0);
        markerColor[1] = Color.rgb(255, 204, 0);
        markerColor[2] = Color.rgb(255, 255, 0);
        markerColor[3] = Color.rgb(0, 255, 0);
        markerColor[4] = Color.rgb(0, 0, 255);
        markerColor[5] = Color.rgb(0, 0, 102);
        markerColor[6] = Color.rgb(100, 0, 255);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        setNaverMap(naverMap);
        setClickListenerWhenCLickSymbolInNaverMap();
    }

    private void setNaverMap(NaverMap naverMap) {
        this.naverMap = naverMap;
        //배경 지도 선택
        naverMap.setMapType(NaverMap.MapType.Navi);
        // 건물 표시
        naverMap.setLayerGroupEnabled(naverMap.LAYER_GROUP_BUILDING, true);
    }

    private void clickMarker() {

    }

    private void setClickListenerWhenCLickSymbolInNaverMap() {
        naverMap.setOnSymbolClickListener(symbol -> {
            Marker marker = new Marker();
            marker.setPosition(symbol.getPosition());
            setClickMarkerListener(marker);
            new Thread(() -> {
                ReverseGeocoding.convertCoordinateToAddress(symbol.getPosition());
                String roadAddress = ReverseGeocoding.getRoadAddress();
                LatLng locationCoordinate = null;
                if (roadAddress != null) {
                    locationCoordinate = Geocoding.convertAddressToCoordinate(roadAddress);
                    if (locationCoordinate != null) {
                        this.location.add(locationCoordinate);
                        this.marker.add(marker);
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplication(), "선택할 수 없는 지역입니다.", Toast.LENGTH_SHORT).show();
                            marker.setMap(null);
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplication(), "선택할 수 없는 지역입니다.", Toast.LENGTH_SHORT).show();
                        marker.setMap(null);
                    });
                }
            }).start();
            marker.setMap(naverMap);
            return true;
        });
    }

    // 마커 클릭 시 색 변경.
    private void setClickMarkerListener(Marker marker) {
        marker.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                marker.setIcon(MarkerIcons.BLACK);
                marker.setIconTintColor(markerColor[location.size()]);
                Toast.makeText(getApplication(), "click marker", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}