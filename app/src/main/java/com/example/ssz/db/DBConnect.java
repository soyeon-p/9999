package com.example.ssz.db;

import androidx.annotation.NonNull;

import com.example.ssz.db.dto.DBRouteDTO;
import com.example.ssz.db.dto.DBRouteLocationDTO;
import com.example.ssz.db.dto.RouteDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.geometry.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DBConnect {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static String testUserUUID = "DIFJ092JFDS0";
    private static String testUserId = "Oia";
    private static String testUserPassword = "1234";
    private static boolean isStart;
    private static int routeId;

    public static void saveComment(String comment) {
        readRoutCnt(new DBIntCallback() {
            @Override
            public void onCallback(int routeCnt) {
                db.collection("route").document("route" + routeCnt).update("comment", comment);
            }
        });
    }

    public static void saveRoute(RouteDTO routeDTO) {
        readRoutCnt(new DBIntCallback() {
            @Override
            public void onCallback(int routeCnt) {
                routeCnt++;
                routeDTO.setRoute_id(routeCnt);
                db.collection("route").document("route" + routeCnt).set(routeDTO);
            }
        });
    }

    public static void readRoutCnt(DBIntCallback dbIntCallback) {
        db.collection("route").get().addOnSuccessListener(command -> {
            dbIntCallback.onCallback(command.getDocuments().size());
        });
    }

    public static void readComment(DBStringCallback dbStringCallback, int routeId) {
        db.collection("route").document("route_id" + routeId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                dbStringCallback.onCallback(documentSnapshot.get("comment").toString());
            }
        });
    }

    public static void readRoute(DBArrayCallback dbArrayCallback) {
        isStart = true;
        db.collection("route").orderBy("like_count", Query.Direction.DESCENDING).limit(3)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<ArrayList<LatLng>> route = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            ArrayList<LatLng> road = new ArrayList<>();
                            if (isStart) {
                                routeId = Integer.parseInt(documentSnapshot.get("route_id").toString());
                                isStart = false;
                            }
                            documentSnapshot.get("route_id");
                            ArrayList<DBRouteLocationDTO> dbRouteLocationDTOArrayList = documentSnapshot.toObject(DBRouteDTO.class).location;
                            for (DBRouteLocationDTO dto : dbRouteLocationDTOArrayList) {
                                LatLng location = new LatLng(dto.latitude, dto.longitude);
                                road.add(location);
                            }
                            route.add(road);
                        }
                        dbArrayCallback.onCallback(route, routeId);
                    }
                });
    }
}
