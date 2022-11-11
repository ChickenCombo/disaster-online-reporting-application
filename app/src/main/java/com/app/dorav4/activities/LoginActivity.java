package com.app.dorav4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.RenderMode;
import com.app.dorav4.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout tilEmailAddress, tilPassword;
    TextInputEditText etEmailAddress, etPassword;
    TextView tvForgotPassword;
    Intent intent;
    Button btnLogin, btnRegister;

    DatabaseReference tokensReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        etPassword = findViewById(R.id.etPassword);
        tilEmailAddress = findViewById(R.id.tilEmailAddress);
        tilPassword = findViewById(R.id.tilPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        mAuth = FirebaseAuth.getInstance();
        tokensReference = FirebaseDatabase.getInstance().getReference().child("Tokens");

        // btnLogin OnClickListener
        btnLogin.setOnClickListener(v -> loginUser());

        // btnRegister OnClickListener
        btnRegister.setOnClickListener(v -> {
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // tvForgotPassword OnClickListener
        tvForgotPassword.setOnClickListener(v -> {
            intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    // Login User
    public void loginUser() {
        boolean isEmailValid = false, isPasswordValid = false;
        String email = Objects.requireNonNull(etEmailAddress.getText()).toString().trim();
        String password = Objects.requireNonNull(etPassword.getText()).toString().trim();

        // Validate email address
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmailAddress.setError("Invalid email address, please try again.");
        } else {
            isEmailValid = true;
            tilEmailAddress.setError(null);
        }

        // Validate password
        if (password.isEmpty()) {
            tilPassword.setError("Password cannot be empty!");
        } else {
            isPasswordValid = true;
            tilPassword.setError(null);
        }

        // If all inputs are valid
        if (isEmailValid && isPasswordValid) {
            // Progress Dialog
            MaterialDialog pDialog = new MaterialDialog.Builder(this)
                    .setTitle("Loading")
                    .setMessage("Verifying your login credentials, please wait")
                    .setAnimation(R.raw.lottie_loading)
                    .setCancelable(false)
                    .build();

            LottieAnimationView animationView = pDialog.getAnimationView();
            animationView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            animationView.setRenderMode(RenderMode.SOFTWARE);
            animationView.setPadding(0, 64, 0, 0);

            pDialog.show();

            // Sign-in using Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    pDialog.dismiss();

                    mUser = FirebaseAuth.getInstance().getCurrentUser();
                    assert mUser != null;

                    // Get user token for push notification
                    getToken();

                    // Email verification for first time logins.
                    if (mUser.isEmailVerified()) {
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        mUser.sendEmailVerification();
                        MotionToast.Companion.darkToast(
                                this,
                                "Info",
                                "Please verify your email address",
                                MotionToastStyle.INFO,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(this, R.font.helvetica_regular)
                        );
                    }
                } else {
                    pDialog.dismiss();

                    MotionToast.Companion.darkToast(
                            this,
                            "Error",
                            "Incorrect email/password",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );
                }
                clearFields();
            });
        }
    }

    // Clear login form
    private void clearFields() {
        Objects.requireNonNull(etEmailAddress.getText()).clear();
        Objects.requireNonNull(etPassword.getText()).clear();
        etEmailAddress.clearFocus();
        etPassword.clearFocus();
    }

    // Get user token
    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("token", task.getResult());

                    // Store user's token to the database
                    tokensReference.child(mUser.getUid()).setValue(hashMap);
                }
            });
    }
}