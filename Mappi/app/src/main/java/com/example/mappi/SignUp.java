package com.example.mappi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class SignUp extends AppCompatActivity {

    ImageView btnExit;
    ImageView btnSignIn;
    Button btnSignUp;
    EditText edFullName;
    EditText edUsername;
    EditText edPassword;
    EditText edConfirmPassword;
    TextView txtSignUp;

    FirebaseAuth mAuth;

    private Intent intent;

    private final String emailRegex = "[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+";

    private final String passwordRegex ="^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{4,6}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //type cast

        edFullName = findViewById(R.id.edFullName);
        edPassword = findViewById(R.id.edPassword);
        edUsername = findViewById(R.id.edUsername);
        edConfirmPassword = findViewById(R.id.edConfirmPassword);
        txtSignUp = findViewById(R.id.txtSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch(view.getId()) {
                    case R.id.btnSignUp:
                        registerUser();
                        break;
                }

            }

        });
    }







    private void registerUser(){
        String userEmail = edUsername.getText().toString().trim();
        String userPassword = edPassword.getText().toString().trim();
        String confirmUserPassword = edConfirmPassword.getText().toString().trim();
        if (userPassword.equals(confirmUserPassword)) {

            if (userEmail.matches(emailRegex) || confirmUserPassword.matches(passwordRegex)) {

                Toast.makeText(SignUp.this, "Everything working", Toast.LENGTH_SHORT).show();

                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        intent = new Intent(SignUp.this, PreferencesPage.class);
                        startActivity(intent);
                    }
                };
                timer.schedule(task, 1000);

            }
        }
        if (userEmail.isEmpty()) {
            edUsername.setError("Please Enter Your Email");
            edUsername.requestFocus();
            return;
        }

        if (userPassword.isEmpty()) {
            edPassword.setError("Please Enter Your Password");
            edPassword.requestFocus();
            return;
        }

        if (userPassword.length() < 6) {
            edPassword.setError("Enter A Password With At Least 6 Characters");
            edPassword.requestFocus();
            return;
        } else {
            Toast.makeText(SignUp.this, "email or password is not sufficient", Toast.LENGTH_SHORT).show();
        }

        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(userEmail, userPassword);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(SignUp.this, "User Has Been Registered Successfully", Toast.LENGTH_SHORT).show();
                                                //progressBar2.setVisibility(View.GONE);
                                                startActivity(new Intent(SignUp.this, MapNav.class));
                                            }else{
                                                Toast.makeText(SignUp.this, "Failed To Register, Please Try Again", Toast.LENGTH_SHORT).show();
                                                //progressBar2.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                        }else{
                            Toast.makeText(SignUp.this, "Failed To Register, Please Try Again", Toast.LENGTH_SHORT).show();
                            //progressBar2.setVisibility(View.GONE);
                        }
                    }
                });
        //}
        //else{
        //    Toast.makeText(SignUpActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
        //}

    }

    public void txtSignIn(View view){
        Intent intent = new Intent(SignUp.this, Login.class);
        startActivity(intent);
    }







    /*public void btnExit(View view){

        Intent intent = new Intent(SignUp.this, Login.class);
        startActivity(intent);
        this.finish();
    }

    public void SignInLink(View view){

        Intent intent = new Intent(SignUp.this, Login.class);
        startActivity(intent);
        this.finish();
    }
    public void btnSignIn(View view){

        Intent intent = new Intent(SignUp.this, Login.class);
        startActivity(intent);
        this.finish();
    }*/
}