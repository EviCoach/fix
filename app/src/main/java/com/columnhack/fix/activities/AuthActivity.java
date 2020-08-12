package com.columnhack.fix.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.columnhack.fix.R;
import com.columnhack.fix.dialogs.ServiceLoadingDialog;
import com.columnhack.fix.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";
    public static final String AUTH_DIALOG = "auth_dialog";

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
    private TextView mAuthText;
    private TextView mAuthInfoText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mEmailTextView = findViewById(R.id.user_auth_email);
        mPasswordTextView = findViewById(R.id.user_auth_password);
        mAuthButton = findViewById(R.id.auth_btn);
        mSignupText = findViewById(R.id.signup_text);
        mConfirmPasswordTextView = findViewById(R.id.confirm_user_auth_password);
        mAuthText = findViewById(R.id.not_a_member_yet);
        mAuthInfoText = findViewById(R.id.auth_info);

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

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void signup() {
        if (!LOGIN) {
            final String email = mEmailTextView.getText().toString();
            String password = mPasswordTextView.getText().toString();
            String confirmPassword = mConfirmPasswordTextView.getText().toString();

            if (password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
                Toast.makeText(AuthActivity.this, "Fields must not be empty", Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                // Password did not match
                Toast.makeText(this, "Passwords did not match", Toast.LENGTH_SHORT)
                        .show();
            } else {

                hideKeyboard(this); // hide the soft keyboad
                // continue with signup
                FragmentManager manager = getSupportFragmentManager();
                final ServiceLoadingDialog dialog = new ServiceLoadingDialog(this);
                dialog.show(manager, AUTH_DIALOG); // show the dialog
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss(); // hide the dialog
                                if (!task.isSuccessful()) {
                                    showAuthText(task.getException().getMessage(), false);
                                } else {
                                    mAuth.getCurrentUser().sendEmailVerification();

                                    User user = new User();
                                    user.setName(email.substring(0, email.indexOf("@")));
                                    user.setPhone("Will implemented later");
                                    user.setProfile_image("Will be implemented later");
                                    user.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user);

                                    String text = "Check your mail inbox for a verification link\n and login";
                                    showAuthText(text, true);
                                    mAuth.signOut();
                                }
                            }
                        });
            }
        }
    }

    private void login() {
        if (LOGIN) {
            hideKeyboard(this); // hide the soft keyboard
            FragmentManager manager = getSupportFragmentManager();
            final ServiceLoadingDialog dialog = new ServiceLoadingDialog(this);
            dialog.show(manager, AUTH_DIALOG); // show the dialog
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
                            dialog.dismiss(); // hide the dialog
                            if (!task.isSuccessful()) {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                showAuthText(task.getException().getMessage(), false);
                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showAuthText(e.getMessage(), false);
                        }
                    });
        }
    }

    private void showAuthText(String errMsg, boolean successOrFail) {
        mAuthInfoText.setVisibility(View.VISIBLE);
        mAuthInfoText.setText(errMsg);
        if (successOrFail) {
            mAuthInfoText.setTextColor(Color.GREEN);
        } else mAuthInfoText.setTextColor(Color.RED);
    }

    private void configureSignUpUI() {
        mAuthButton.setText("Sign Up");
        mConfirmPasswordTextView.setVisibility(View.VISIBLE);
        mSignupText.setText("Login");
        mAuthText.setText("Login instead.");
    }

    private void configureLoginUI() {
        mConfirmPasswordTextView.setVisibility(View.GONE);
        mAuthButton.setText("Login");
        mSignupText.setText("Sign Up");
        mAuthText.setText("Not a member yet?");

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