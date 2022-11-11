package com.example.mappi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    int onStartCount = 0;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        listView = findViewById(R.id.listView);

        ArrayList<String> arrayList = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("Last Trip", Context.MODE_PRIVATE);
        String distance = sharedPreferences.getString("Distance", "");
        String duration = sharedPreferences.getString("Duration", "");
        double durationDouble = Double.parseDouble(duration);
        int minutes = (int)durationDouble % 3600 / 60;

        arrayList.add("Last Trip:\n\nDistance: " + distance + " km\nDuration: " + minutes + " minutes");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        onStartCount = 1;

        if (savedInstanceState == null)
        {
            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        } else
        {
            onStartCount = 2;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        } else if (onStartCount == 1) {
            onStartCount++;
        }
    }

}