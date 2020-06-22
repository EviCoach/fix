package com.columnhack.fix.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.columnhack.fix.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";

    /*var*/
    private static boolean LOGIN = true;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener firebaseAuthListener;

    /*widgets*/
    private EditText mEmailTextView;
    private EditText mPasswordTextView;
    private EditText mConfirmPasswordTextView;
    private TextView mSignupText;
    private Button mAuthButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mEmailTextView = findViewById(R.id.user_auth_email);
        mPasswordTextView = findViewById(R.id.user_auth_password);
        mAuthButton = findViewById(R.id.auth_btn);
        mSignupText = findViewById(R.id.signup_text);
        mConfirmPasswordTextView = findViewById(R.id.confirm_user_auth_password);

        mAuth = FirebaseAuth.getInstance();

        // Whenever we login or logout, this listener will be called
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
                if (user != null && user.isEmailVerified()) {
                    Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        if (LOGIN) {
            configureLoginUI();
        } else {
            configureSignUpUI();
        }

        mAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LOGIN) {
                    login();
                } else {
                    signup();
                }
            }
        });

        mSignupText = findViewById(R.id.signup_text);
        mSignupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LOGIN) {
                    configureSignUpUI();
                } else {
                    configureLoginUI();
                }
                LOGIN = !LOGIN;
            }
        });

    }

    private void signup() {
        if (!LOGIN) {
            String email = mEmailTextView.getText().toString();
            String password = mPasswordTextView.getText().toString();
            String confirmPassword = mConfirmPasswordTextView.getText().toString();

            if (password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
                Toast.makeText(AuthActivity.this, "Fields must not be empty", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            ;

            if (!password.equals(confirmPassword)) {
                // Password did not match
                Toast.makeText(this, "Passwords did not match", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // continue with signup
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(AuthActivity.this, "Sign up error", Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    mAuth.getCurrentUser().sendEmailVerification();
                                    Toast.makeText(AuthActivity.this, "Check your mail inbox for a verification link",
                                            Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                }
                            }
                        });
            }
        }
    }

    private void login() {
        if (LOGIN) {
            String email = mEmailTextView.getText().toString();
            String password = mPasswordTextView.getText().toString();
            if (password.isEmpty() || email.isEmpty()) {
                Toast.makeText(AuthActivity.this, "Fields must not be empty", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            ;
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(AuthActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void configureSignUpUI() {
        mAuthButton.setText("Sign Up");
        mConfirmPasswordTextView.setVisibility(View.VISIBLE);
        mSignupText.setText("Login");
    }

    private void configureLoginUI() {
        mConfirmPasswordTextView.setVisibility(View.GONE);
        mAuthButton.setText("Login");
        mSignupText.setText("Sign Up");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}