package com.app.dorav4.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.RenderMode;
import com.app.dorav4.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextInputLayout tilEmailAddress;
    TextInputEditText etEmailAddress;
    ImageView ivBack;
    Button btnForgotPassword;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        tilEmailAddress = findViewById(R.id.tilEmailAddress);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        ivBack = findViewById(R.id.ivBack);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        mAuth = FirebaseAuth.getInstance();

        // btnForgotPassword OnClickListener
        btnForgotPassword.setOnClickListener(v -> resetPassword());

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }

    private void resetPassword() {
        boolean isEmailValid = false;
        String email = Objects.requireNonNull(etEmailAddress.getText()).toString().trim();

        // Validate email address
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmailAddress.setError("Invalid email address, please try again.");
        } else {
            isEmailValid = true;
            tilEmailAddress.setError(null);
        }

        if (isEmailValid) {
            // Progress Dialog
            MaterialDialog pDialog = new MaterialDialog.Builder(this)
                    .setTitle("Loading")
                    .setMessage("Resetting your password, please wait")
                    .setAnimation(R.raw.lottie_loading)
                    .setCancelable(false)
                    .build();

            LottieAnimationView animationView = pDialog.getAnimationView();
            animationView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            animationView.setRenderMode(RenderMode.SOFTWARE);
            animationView.setPadding(0, 64, 0, 0);

            pDialog.show();

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    pDialog.dismiss();
                    MotionToast.Companion.darkToast(
                            this,
                            "Info",
                            "Please check your email to reset your password",
                            MotionToastStyle.INFO,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );
                    finish();
                } else {
                    pDialog.dismiss();
                    MotionToast.Companion.darkToast(
                            this,
                            "Error",
                            "This account doesn't exist",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );
                }
            });
        }
    }
}