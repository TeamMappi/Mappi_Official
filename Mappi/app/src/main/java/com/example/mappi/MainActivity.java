package com.example.mappi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

            //timer variable
            int DISPLAY_LENGTH = 8000;
            new Handler().postDelayed(()->{

            //create your intent service to navigate to whichever screen
            Intent splashIntent = new Intent(MainActivity.this, Login.class);
            startActivity(splashIntent);
            this.finish();
        },DISPLAY_LENGTH);

    }
/*
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, Login.class));



        }
    }*/
}