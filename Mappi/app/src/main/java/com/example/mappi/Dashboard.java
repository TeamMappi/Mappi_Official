package com.example.mappi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dashboard extends AppCompatActivity {
    int onStartCount = 0;

    FirebaseUser firebaseUser;

    TextView usernameTextView;

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(firebaseUser.getDisplayName());

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            int res = checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE);
            if (res != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE}, 123);
            }
        }

        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        } else if (onStartCount == 1) {
            onStartCount++;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "READ_PHONE_STATE Denied", Toast.LENGTH_SHORT)
                            .show();
                } else {
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void clickedNavigation(View view) {
        Intent intent = new Intent(Dashboard.this, MapNav.class);
        startActivity(intent);
    }

    public void clickedProfile(View view) {
        Intent intent = new Intent(Dashboard.this, SettingsPage.class);
        startActivity(intent);
    }

    public void clickedParking(View view) {
        Intent intent = new Intent(Dashboard.this, Park.class);
        startActivity(intent);
    }

    public void clickedLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Dashboard.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void clickedFavourites(View view) {
        Intent intent = new Intent(Dashboard.this, FavouritePages.class);
        startActivity(intent);
    }

    public void clickedHistory(View view) {
        Intent intent = new Intent(Dashboard.this, History.class);
        startActivity(intent);
    }

}