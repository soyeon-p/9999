package com.example.ssz.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ssz.R;

public class MakeRoute extends AppCompatActivity {
    private Button makeroute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.ssz.R.layout.activity_makeroute);

        makeroute = findViewById(R.id.makeroute);
        makeroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(com.example.ssz.test.MakeRoute.this, Comment.class); //코멘트 달기로 넘어감
                startActivity(intent);
            }
        });
    }
}