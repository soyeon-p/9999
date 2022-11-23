package com.example.ssz.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ssz.AppData;
import com.example.ssz.R;
import com.example.ssz.naveropenapi.ApiSecretKey;
import com.example.ssz.naveropenapi.Geocoding;
import com.example.ssz.naveropenapi.ReverseGeocoding;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

public class UserPage2 extends AppCompatActivity implements OnMapReadyCallback {
    private static NaverMap naverMap;
    private MapView mapView;
    private Marker startMarker;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.ssz.R.layout.activity_user_pg2);

        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient(ApiSecretKey.X_NCP_APIGW_API_KEY_ID));
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserPage2.this, UserPage3.class);
                startActivity(intent);
            }
        });
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

    private void setClickListenerWhenCLickSymbolInNaverMap() {
        naverMap.setOnSymbolClickListener(symbol -> {
            Marker marker = new Marker();
            marker.setPosition(symbol.getPosition());
            new Thread(() -> {
                ReverseGeocoding.convertCoordinateToAddress(symbol.getPosition());
                String roadAddress = ReverseGeocoding.getRoadAddress();
                LatLng locationCoordinate = null;
                if (roadAddress != null) {
                    locationCoordinate = Geocoding.convertAddressToCoordinate(roadAddress);
                    if (locationCoordinate != null) {
                        ((AppData) getApplication()).setStartLocation(locationCoordinate);
                        this.startMarker = marker;
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
}