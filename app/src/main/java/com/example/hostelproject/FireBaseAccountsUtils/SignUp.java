package com.example.hostelproject.FireBaseAccountsUtils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.hostelproject.R;
import com.example.hostelproject.databinding.SignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private SignUpBinding signUpBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = DataBindingUtil.setContentView(this, R.layout.sign_up);

        mAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar));
        }
    }


    public void RegisterUser(View view) {
        final String password = signUpBinding.passwordSign.getText().toString().trim();
        String pass2 = signUpBinding.passwordConfirm.getText().toString().trim();
        final String email = signUpBinding.tvEmailSign.getText().toString().trim();


        if (email.isEmpty()) {
            signUpBinding.tvEmailSign.setError("Email is required");
            signUpBinding.tvEmailSign.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signUpBinding.tvEmailSign.setError("Please enter a valid email");
            signUpBinding.tvEmailSign.requestFocus();
            return;
        }
        if (!password.equals(pass2)) {
            signUpBinding.passwordConfirm.setError("Password did not match");
            signUpBinding.passwordConfirm.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            signUpBinding.passwordSign.setError("Password is required");
            signUpBinding.passwordSign.requestFocus();
            return;
        }

        if (password.length() < 6) {
            signUpBinding.passwordSign.setError("Minimum length of password should be 6");
            signUpBinding.passwordSign.requestFocus();
            return;

        }

        user = mAuth.getCurrentUser();
        signUpBinding.progressSign.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    signUpBinding.progressSign.setVisibility(View.GONE);
                    Toast.makeText(SignUp.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                    goToLogIn(signUpBinding.btnSignUp);

                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void goToLogIn(View view) {
        Intent intent = new Intent(SignUp.this, LogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}
