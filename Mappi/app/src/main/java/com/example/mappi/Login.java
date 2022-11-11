package com.example.mappi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    //variables
    ImageView btnSignInAuth;
    EditText edPassword1;
    EditText edUsernameEmail;
    Button signInButton;

    TextView txtSignUp;
    TextView txtSignIn;

    String userEmail;
    String userPassword;

    FirebaseAuth firebaseAuth;

    Intent intent;

    private final String emailRegex = "[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+";
    private final String passwordRegex ="^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{4,6}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //type cast
        edPassword1 = findViewById(R.id.edPassword1);
        edUsernameEmail = findViewById(R.id.edUsernameEmail);
        txtSignUp = findViewById(R.id.txtSignUp);
        txtSignIn = findViewById(R.id.txtSignIn);
        signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validations
                userEmail = edUsernameEmail.getText().toString();
                userPassword = edPassword1.getText().toString();
                if (userEmail.matches(emailRegex) || userPassword.matches(passwordRegex)) {
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            //NOTE switch to Intent to home navigation
                            Toast.makeText(Login.this, "Logged In", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, MapNav.class);
                            startActivity(intent);

                        }
                    });
                }
                else{
                    Toast.makeText(Login.this, "Email or password is incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

    }

        //public void SignInButton(View view){
        //    Intent intent = new Intent(Login.this, MapNav.class);
        //    startActivity(intent);
        //}

}