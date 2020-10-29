package com.cere.androidwidget;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cere.widget.PullToRefreshLayout;

import java.util.ArrayList;

public class PullToRefreshActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> list = new ArrayList<>(20);
        for (int i = 0; i < 1; i++) {
            list.add("adfasdf" + i);
        }

        Adapter adapter = new Adapter(this, list);
        recyclerView.setAdapter(adapter);

        PullToRefreshLayout layout = findViewById(R.id.refresh);
        /*layout.setPullToHeight(100);
        layout.setDamping(0.1f);*/
        if (list.size() < 10) {
            layout.setCanScrollDown(true);
        }
        ImageView im = findViewById(R.id.header_image);
        TextView tv = findViewById(R.id.header_text);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        layout.setOnPullToChangeListener((location, status) -> {
            if (location == PullToRefreshLayout.PullToLocation.HEADER) {
                Log.e("TAG", "MainActivity -> onCreate: " + status);
                if (status == PullToRefreshLayout.PullTaState.START || status == PullToRefreshLayout.PullTaState.END) {
                    im.clearAnimation();
                    im.setImageResource(R.drawable.ic_drop_down);
                    tv.setText("下拉刷新");
                } else if (status == PullToRefreshLayout.PullTaState.TRIGGER) {
                    im.setImageResource(R.drawable.ic_refresh);
                    im.startAnimation(animation);
                    tv.setText("正在刷新");
                    new Handler().postDelayed(() -> layout.setRefreshing(false), 3000);
                    return true;
                } else {
                    im.clearAnimation();
                    tv.setText("刷新完成");
                }
                return true;
            } else {
                if (status == PullToRefreshLayout.PullTaState.START) {
                    layout.setRefreshing(false);
                }
                Log.e("TAG", "MainActivity -> onCreate: Footer: " + status);
                return false;
            }
        });
    }
}