package com.example.ssz.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ssz.R;

public class UserPage4 extends AppCompatActivity {
    private Button done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.ssz.R.layout.activity_user_pg4);

        Intent intent = getIntent();
        String routeInfo = intent.getStringExtra("routeInfo");

        TextView textView = findViewById(R.id.textView5);
        textView.setText(routeInfo);

        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dismiss();
            }
        });
    }
}