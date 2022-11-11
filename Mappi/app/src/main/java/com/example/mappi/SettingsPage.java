package com.example.mappi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsPage extends AppCompatActivity {

    TextInputLayout preferencesEditText;
    TextInputLayout landmarksEditText;
    TextInputLayout transEditText;
    AutoCompleteTextView preferencesAutoCompleteTextView;
    AutoCompleteTextView landmarksAutoCompleteTextView;
    AutoCompleteTextView transAutoCompleteTextView;
    ProgressBar progressBar;
    TextView usernameTextView;
    TextView emailTextView;
    TextView numberTextView;
    ImageView exitBtn;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    private FirebaseUser firebaseUser;

    int ONE_MEGABYTE = 1024 * 1024;

    int onStartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        String[] preferences = {"Kilometres", "Miles"};
        String[] landmarks = {"Historical", "Modern", "Popular"};
        String[] trans = {"Car", "Cycle", "Walk"};

        preferencesEditText = findViewById(R.id.outerPreferencesEditText);
        landmarksEditText = findViewById(R.id.outerLandmarkEditText);
        transEditText = findViewById(R.id.outerTransEditText);
        preferencesAutoCompleteTextView = findViewById(R.id.innerPreferencesEditText);
        landmarksAutoCompleteTextView = findViewById(R.id.innerLandmarkEditText);
        transAutoCompleteTextView = findViewById(R.id.innerTransEditText);
        progressBar = findViewById(R.id.progressBar);
        exitBtn = findViewById(R.id.exitBtn);
        usernameTextView = findViewById(R.id.usernameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        numberTextView = findViewById(R.id.numberTextView);

        ArrayAdapter<String> preferencesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, preferences);
        ArrayAdapter<String> landmarksArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, landmarks);
        ArrayAdapter<String> transArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, trans);
        preferencesAutoCompleteTextView.setAdapter(preferencesArrayAdapter);
        landmarksAutoCompleteTextView.setAdapter(landmarksArrayAdapter);
        transAutoCompleteTextView.setAdapter(transArrayAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPreference", MODE_PRIVATE);
        String preference = sharedPreferences.getString("preference", "");
        preferencesAutoCompleteTextView.setHint(preference);
        String landmark = sharedPreferences.getString("landmark", "");
        String transMode = sharedPreferences.getString("trans", "");
        if (transMode.isEmpty()) {
            transAutoCompleteTextView.setHint("Please choose");
        }
        preferencesAutoCompleteTextView.setHint(preference);
        landmarksAutoCompleteTextView.setHint(landmark);
        transAutoCompleteTextView.setHint(transMode);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        usernameTextView.setText(firebaseUser.getDisplayName());
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        emailTextView.setText(firebaseUser.getEmail());

        StorageReference numberStorageReference = storageReference.child(firebaseUser.getUid() + "/number.txt");
        numberStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                String number = new String(bytes);
                numberTextView.setText(number);
            }
        });

        onStartCount = 1;

        if (savedInstanceState == null) {
            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        } else {
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

    public void clickedBack(View view) {
        this.finish();

    }

    public void Exit(View view) {

        progressBar.setVisibility(View.VISIBLE);

        String preference = preferencesAutoCompleteTextView.getText().toString();
        String landmark = landmarksAutoCompleteTextView.getText().toString();
        String transMode = transAutoCompleteTextView.getText().toString();

        FirebaseInteraction firebaseInteraction = new FirebaseInteraction(storageReference, firebaseUser);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreference", MODE_PRIVATE);
        String originalPreference = "NO";
        String originalLandmark = "NO";
        if (preference.equals("") || preference.isEmpty()) {
            originalPreference = sharedPreferences.getString("preference", "");
            firebaseInteraction.uploadToFirebase("preference", originalPreference);
        } if (landmark.equals("") || landmark.isEmpty()) {
            originalLandmark = sharedPreferences.getString("landmark", "");
            firebaseInteraction.uploadToFirebase("landmark", originalLandmark);
        } if (originalPreference.equals("NO")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("preference", preference);
            editor.apply();
            firebaseInteraction.uploadToFirebase("preference", preference);
        } if (originalLandmark.equals("NO")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("landmark", landmark);
            editor.apply();
            firebaseInteraction.uploadToFirebase("landmark", landmark);
        }

        sharedPreferences.edit().putString("trans", transMode).apply();

        Intent intent = new Intent(SettingsPage.this, MapNav.class);
        startActivity(intent);
        this.finish();
    }
}