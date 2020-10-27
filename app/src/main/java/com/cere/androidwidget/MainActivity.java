package com.cere.androidwidget;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt1).setOnClickListener(view -> {
            startActivity(new Intent(this, PullToRefreshActivity.class));
        });
        findViewById(R.id.bt2).setOnClickListener(view -> {
            startActivity(new Intent(this, BottomSheetActivity.class));
        });
    }
}