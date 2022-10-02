package com.app.dorav4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.RenderMode;
import com.app.dorav4.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ChangeEmailActivity extends AppCompatActivity {
    TextInputLayout tilEmailAddress, tilPassword, tilNewEmailAddress;
    EditText etEmailAddress, etPassword, etNewEmailAddress;
    ImageView ivBack;
    Button btnAuthenticate, btnChangeEmail;
    MaterialCardView cvAuthenticate, cvEmail;
    Intent intent;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    String currentEmail, newEmail, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        tilEmailAddress = findViewById(R.id.tilEmailAddress);
        tilPassword = findViewById(R.id.tilPassword);
        tilNewEmailAddress = findViewById(R.id.tilNewEmailAddress);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        etPassword = findViewById(R.id.etPassword);
        etNewEmailAddress = findViewById(R.id.etNewEmailAddress);
        ivBack = findViewById(R.id.ivBack);
        btnAuthenticate = findViewById(R.id.btnAuthenticate);
        btnChangeEmail = findViewById(R.id.btnChangeEmail);
        cvAuthenticate = findViewById(R.id.cvAuthenticate);
        cvEmail = findViewById(R.id.cvEmail);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // Set email to read-only
        etEmailAddress.setEnabled(false);
        etPassword.requestFocus();

        // Set current email address
        currentEmail = mUser.getEmail();
        etEmailAddress.setText(currentEmail);

        // btnAuthenticate OnClickListener
        btnAuthenticate.setOnClickListener(v -> authenticateUser());

        // btnChangeEmail OnClickListener
        btnChangeEmail.setOnClickListener(v -> updateEmail());

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }

    // Authenticate user
    private void authenticateUser() {
        boolean isPasswordValid = false;
        password = Objects.requireNonNull(etPassword.getText()).toString().trim();

        // Validate password
        if (password.isEmpty()) {
            tilPassword.setError("Password cannot be empty!");
        } else {
            isPasswordValid = true;
            tilPassword.setError(null);
        }

        if (isPasswordValid) {
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

            // Re-authenticate user
            AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);
            mUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    pDialog.dismiss();
                    MotionToast.Companion.darkToast(
                            this,
                            "Success",
                            "Account has been authenticated",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );

                    // Hide authentication form and show update email form
                    cvAuthenticate.setVisibility(View.GONE);
                    cvEmail.setVisibility(View.VISIBLE);
                } else {
                    pDialog.dismiss();
                    MotionToast.Companion.darkToast(
                            this,
                            "Error",
                            "Incorrect password, please try again",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );
                }
            });
        }
    }

    // Update email
    private void updateEmail() {
        boolean isEmailValid = false;
        newEmail = Objects.requireNonNull(etNewEmailAddress.getText()).toString().trim();

        // Validate email address
        if (currentEmail.matches(newEmail)) {
            tilNewEmailAddress.setError("New email cannot be the same as your old email!");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            tilNewEmailAddress.setError("Invalid email address, please try again.");
        } else {
            isEmailValid = true;
            tilNewEmailAddress.setError(null);
        }

        // Valid email
        if (isEmailValid) {
            // Update email address
            mUser.updateEmail(newEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Send email verification then logout
                    MotionToast.Companion.darkToast(
                            this,
                            "Success",
                            "Email has been updated, please re-login to continue",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );
                    FirebaseDatabase.getInstance().getReference("Tokens").child(mUser.getUid()).removeValue();
                    mUser.sendEmailVerification();
                    FirebaseAuth.getInstance().signOut();
                    intent = new Intent(ChangeEmailActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    MotionToast.Companion.darkToast(
                            this,
                            "Error",
                            "Email update failed, please try again",
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