package com.example.ssz.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssz.AppData;
import com.example.ssz.R;
import com.example.ssz.db.DBArrayCallback;
import com.example.ssz.db.DBConnect;
import com.example.ssz.db.DBStringCallback;
import com.example.ssz.naveropenapi.ApiSecretKey;
import com.example.ssz.naveropenapi.Direction;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.PathOverlay;

import java.util.ArrayList;

public class UserPage3 extends AppCompatActivity implements OnMapReadyCallback {
    private Button like;
    private TextView tv_count;
    private int count = 0;

    Dialog user_pg4;  //코멘트 다이얼로그

    private static NaverMap naverMap;
    private MapView mapView;
    private int[] routeColor;
    private ArrayList<PathOverlay> pathOverlays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.ssz.R.layout.activity_user_pg3);

        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient(ApiSecretKey.X_NCP_APIGW_API_KEY_ID));
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        user_pg4 = new Dialog(UserPage3.this);
        user_pg4.setContentView(R.layout.activity_user_pg4);

        pathOverlays = new ArrayList<>();
        routeColor = new int[3];
        routeColor[0] = Color.rgb(255, 0, 0);
        routeColor[1] = Color.rgb(255, 204, 0);
        routeColor[2] = Color.rgb(255, 255, 0);
    }

    public void showComment() { //dialog design 함수

        user_pg4.show(); //dialog 띄우기

        TextView comment =user_pg4.findViewById(R.id.textView5);
        System.out.println("+++getComment" + ((AppData)getApplication()).getComment());
        comment.setText(((AppData) getApplication()).getComment());

        Button like = user_pg4.findViewById(R.id.like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UserPage3.this, "like", Toast.LENGTH_SHORT).show();
                tv_count = user_pg4.findViewById(R.id.tv_count);
                if (count == 0) {
                    count++;
                    tv_count.setText("1");
                } else if (count == 1) {
                    count--;
                    tv_count.setText("0");
                }
            }
        });

        Button done = user_pg4.findViewById(R.id.done); // x 버튼
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_pg4.dismiss();
            }
        });
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        setNaverMap(naverMap);
        printRoute();
    }

    private void setNaverMap(NaverMap naverMap) {
        this.naverMap = naverMap;
        //배경 지도 선택
        naverMap.setMapType(NaverMap.MapType.Navi);
        // 건물 표시
        naverMap.setLayerGroupEnabled(naverMap.LAYER_GROUP_BUILDING, true);
    }

    private void printRoute() {
        LatLng startLocation = ((AppData) (getApplication())).getStartLocation();
        DBConnect.readRoute(new DBArrayCallback() {
            @Override
            public void onCallback(ArrayList<ArrayList<LatLng>> route, int routeId) {
                {
                    new Thread(() -> {
                        if (route.size() == 0) {
                            Toast.makeText(getApplication(), "해당 위치로 시작하는 경로가 없습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UserPage3.this, UserPage2.class);
                            intent.putExtra("error", "해당 위치로 시작하는 경로가 없습니다.");
                            intent.putExtra("errorFlag", true);
                            startActivity(intent);
                        } else {
                            for (int i = 0; i < route.size(); i++) {
                                if (route.get(i).get(0).latitude == startLocation.latitude &&
                                        route.get(i).get(0).longitude == startLocation.longitude) {
                                    PathOverlay pathOverlay = new PathOverlay();
                                    ArrayList<LatLng> waypoint = Direction.getWaypoint(route.get(i));

                                    if (waypoint != null) {
                                        pathOverlay.setCoords(waypoint);
                                        pathOverlay.setOutlineWidth(10);
                                        pathOverlay.setColor(routeColor[i]);
                                        pathOverlay.setOnClickListener(overlay -> {
                                            /*Intent intent = new Intent(UserPage3.this, UserPage4.class);
                                            intent.putExtra("routeId", routeId);
                                            startActivity(intent);*/
                                            ((AppData)getApplication()).setRouteId(routeId);
                                            DBConnect.readComment(new DBStringCallback() {
                                                @Override
                                                public void onCallback(String comment) {
                                                    ((AppData) getApplication()).setComment(comment);
                                                    showComment();
                                                }
                                            }, ((AppData)getApplication()).getRouteId());
                                            return true;
                                        });

                                        pathOverlays.add(pathOverlay);

                                        runOnUiThread(() -> {
                                            pathOverlay.setMap(naverMap);
                                        });
                                    }
                                }
                            }
                        }
                    }).start();
                }
            }
        });
    }
}