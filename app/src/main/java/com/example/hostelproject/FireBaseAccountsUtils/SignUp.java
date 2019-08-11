package com.example.hostelproject.FireBaseAccountsUtils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.hostelproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {
    private ProgressBar progressBar;
    private EditText editTextEmail, editTextPassword, editTextConfirm;
    private Button signUp;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView goLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        editTextConfirm = findViewById(R.id.password_confirm);
        editTextEmail = findViewById(R.id.tv_email_sign);
        editTextPassword = findViewById(R.id.password_sign);
        progressBar = findViewById(R.id.progress_sign);
        signUp = findViewById(R.id.btn_sign_up);
        goLogin = findViewById(R.id.tv_go_log_in);

        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLogIn();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar));

            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RegisterUser();
                }
            });
        }
    }

    private void gotoLogIn() {
        Intent intent = new Intent(SignUp.this, LogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    private void RegisterUser() {
        final String password = editTextPassword.getText().toString().trim();
        String pass2 = editTextConfirm.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();


        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }
        if (password.equals(pass2)) {

            if (password.isEmpty()) {
                editTextPassword.setError("Password is required");
                editTextPassword.requestFocus();
                return;
            }

            if (password.length() < 6) {
                editTextPassword.setError("Minimum length of password should be 6");
                editTextPassword.requestFocus();
                return;

            }
        } else {
            editTextConfirm.setError("Password did not match");
            editTextConfirm.requestFocus();
            return;
        }
        user = mAuth.getCurrentUser();
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(
                            email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(SignUp.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                            gotoLogIn();
                        }
                    });
                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
