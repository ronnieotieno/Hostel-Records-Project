package com.example.hostelproject.FireBaseAccountsUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.hostelproject.R;
import com.example.hostelproject.databinding.LogInBinding;
import com.example.hostelproject.ui.ListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private LogInBinding logInBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logInBinding = DataBindingUtil.setContentView(this, R.layout.log_in);
        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar));
        }
    }

    public void userLogin(View view) {
        String email = logInBinding.tvEmailLog.getText().toString().trim();
        String password = logInBinding.passwordLog.getText().toString().trim();


        if (email.isEmpty()) {
            logInBinding.tvEmailLog.setError("Email is required");
            logInBinding.tvEmailLog.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            logInBinding.passwordLog.setError("Password is required");
            logInBinding.passwordLog.requestFocus();
            return;
        }
        logInBinding.progressLog.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    logInBinding.progressLog.setVisibility(View.GONE);
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            Intent intent = new Intent(LogIn.this, ListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(intent);
                        } else {
                            verifyEmail();
                        }
                    } else {

                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                logInBinding.progressLog.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void verifyEmail() {
        final FirebaseUser user = mAuth.getCurrentUser();
        AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LogIn.this);
        builder.setMessage("Confirm Email before proceeding");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {

                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(LogIn.this, "Email Sent", Toast.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();
                                dialog.dismiss();
                            }
                        });
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }

    public void goToSignUp(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }
}
