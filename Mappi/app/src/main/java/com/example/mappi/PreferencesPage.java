package com.example.mappi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class PreferencesPage extends AppCompatActivity {
    Button registerButton;

    ImageView planets_gif;

    TextInputLayout preferencesEditText;
    TextInputLayout landmarksEditText;

    AutoCompleteTextView preferencesAutoCompleteTextView;
    AutoCompleteTextView landmarksAutoCompleteTextView;

    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    int onStartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_page);

        registerButton = findViewById(R.id.registerButton);
        planets_gif = findViewById(R.id.planets_gif);
        preferencesEditText = findViewById(R.id.outerPreferencesEditText);
        landmarksEditText = findViewById(R.id.outerLandmarkEditText);
        preferencesAutoCompleteTextView = findViewById(R.id.innerPreferencesEditText);
        landmarksAutoCompleteTextView = findViewById(R.id.innerLandmarkEditText);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        String[] preferences = {"Kilometres", "Miles"};
        String[] landmarks = {"Historical", "Modern", "Recreational"};

        ArrayAdapter<String> preferencesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, preferences);
        ArrayAdapter<String> landmarksArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, landmarks);
        preferencesAutoCompleteTextView.setAdapter(preferencesArrayAdapter);
        landmarksAutoCompleteTextView.setAdapter(landmarksArrayAdapter);

        preferencesAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (preferencesAutoCompleteTextView.getText().toString().equals("Preferences")) {
                        preferencesEditText.setError("Choose a preference");
                    } else {
                        preferencesEditText.setError(null);
                    }
                } catch (Exception exception) {
                    preferencesEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        landmarksAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (landmarksAutoCompleteTextView.getText().toString().equals("Favourite Landmark")) {
                        landmarksEditText.setError("Choose your favourite landmark");
                    } else {
                        landmarksEditText.setError(null);
                    }
                } catch (Exception exception) {
                    landmarksEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        onStartCount = 1;

        if (savedInstanceState == null) {
            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        } else {
            onStartCount = 2;
        }
    }

    protected void onStart() {
        super.onStart();
        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        } else if (onStartCount == 1) {
            onStartCount++;
        }
    }

    public void registerButton(View v) {

        {
            boolean canRegister = true;

            try {
                if (preferencesAutoCompleteTextView.getText().toString().isEmpty()) {
                    preferencesEditText.setError("Choose a preference");
                    canRegister = false;
                }
                if (landmarksAutoCompleteTextView.getText().toString().isEmpty()) {
                    landmarksEditText.setError("Choose your favourite landmark");
                    canRegister = false;
                } else {
                    preferencesEditText.setError(null);
                    landmarksEditText.setError(null);
                }
            } catch (
                    Exception exception) {
                preferencesEditText.setError(null);
                landmarksEditText.setError(null);
            }

            if (canRegister) {
                progressBar.setVisibility(View.VISIBLE);
                registerButton.setEnabled(false);

                String preference = preferencesAutoCompleteTextView.getText().toString();
                String favouriteLandmark = landmarksAutoCompleteTextView.getText().toString();

                SharedPreferences sharedPreferences = getSharedPreferences("RegistrationDetails", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("preference", preference);
                editor.putString("landmark", favouriteLandmark);

                editor.apply();

                Intent intent = new Intent(PreferencesPage.this, Login.class);
                startActivity(intent);


            }

        }
    }
}