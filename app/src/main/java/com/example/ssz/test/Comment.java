package com.example.ssz.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ssz.R;
import com.example.ssz.db.DBConnect;
import com.example.ssz.db.dto.RouteDTO;
import com.google.type.LatLng;

import java.util.ArrayList;
import java.util.Vector;

public class Comment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.ssz.R.layout.activity_comment);

        EditText editText = findViewById(R.id.comment);
        Button saveRouteInfo = (Button) findViewById(R.id.c_route);
        saveRouteInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = "";
                String cmt = editText.getText().toString();
                if (cmt != null) comment = cmt;
                DBConnect.saveComment(comment);
                Intent go = new Intent(Comment.this, Home.class);
                startActivity(go);
            }
        });
    }
}