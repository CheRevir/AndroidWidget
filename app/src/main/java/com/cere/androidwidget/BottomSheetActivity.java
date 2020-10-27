package com.cere.androidwidget;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cere.widget.BottomSheetLayout;

/**
 * Created by CheRevir on 2020/10/27
 */
public class BottomSheetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet);
        BottomSheetLayout layout = findViewById(R.id.layout);
        TextView textView = findViewById(R.id.bottom_sheet_text);

        textView.setOnClickListener(view -> {
            if (layout.getState() == BottomSheetLayout.STATE_COLLAPSED) {
                layout.setState(BottomSheetLayout.STATE_EXPANSION);
            } else {
                layout.setState(BottomSheetLayout.STATE_COLLAPSED);
            }
        });
        findViewById(R.id.bottom_sheet_bt).setOnClickListener(view -> {
            layout.setState(-layout.getState());
        });
        layout.setHeight(500);
        layout.setOnBottomSheetStateChangeListener(state -> {
            Log.e("TAG", "MainActivity -> onCreate: " + state);
        });
    }
}
