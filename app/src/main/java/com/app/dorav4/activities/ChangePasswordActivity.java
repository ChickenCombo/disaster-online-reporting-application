package com.app.dorav4.activities;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ChangePasswordActivity extends AppCompatActivity {
    TextInputLayout tilEmailAddress, tilPassword, tilNewPassword;
    EditText etEmailAddress, etPassword, etNewPassword;
    ImageView ivBack;
    Button btnAuthenticate, btnChangePassword;
    Intent intent;
    MaterialCardView cvAuthenticate, cvPassword;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    String email, currentPassword, newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        tilEmailAddress = findViewById(R.id.tilEmailAddress);
        tilPassword = findViewById(R.id.tilPassword);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        etPassword = findViewById(R.id.etPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        ivBack = findViewById(R.id.ivBack);
        btnAuthenticate = findViewById(R.id.btnAuthenticate);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        cvAuthenticate = findViewById(R.id.cvAuthenticate);
        cvPassword = findViewById(R.id.cvPassword);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // Set email to read-only
        etEmailAddress.setEnabled(false);
        etPassword.requestFocus();

        // Set current email address
        email = mUser.getEmail();
        etEmailAddress.setText(email);

        // btnAuthenticate OnClickListener
        btnAuthenticate.setOnClickListener(v -> authenticateUser());

        // btnChangePassword OnClickListener
        btnChangePassword.setOnClickListener(v -> updatePassword());
        
        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }

    // Authenticate user
    private void authenticateUser() {
        boolean isPasswordValid = false;
        currentPassword = Objects.requireNonNull(etPassword.getText()).toString().trim();

        // Validate password
        if (currentPassword.isEmpty()) {
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

            AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);
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

                    // Hide authentication form and show update password form
                    cvAuthenticate.setVisibility(View.GONE);
                    cvPassword.setVisibility(View.VISIBLE);
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

    // Update password
    private void updatePassword() {
        boolean isPasswordValid = false;
        newPassword = Objects.requireNonNull(etNewPassword.getText()).toString().trim();

        // Validate password
        if (newPassword.equals(currentPassword)) {
            tilNewPassword.setError("New password cannot be the same as your old password!");
        } else if (!isValid(newPassword)) {
            tilNewPassword.setError("A minimum of 8 characters password containing at least one uppercase letter, one lowercase letter, one special character, and one number is required.");
        } else {
            isPasswordValid = true;
            tilNewPassword.setError(null);
        }

        // Valid password
        if (isPasswordValid) {
            // Update password
            mUser.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Logout user
                    MotionToast.Companion.darkToast(
                            this,
                            "Success",
                            "Password has been updated, please re-login to continue",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );
                    FirebaseDatabase.getInstance().getReference("Tokens").child(mUser.getUid()).removeValue();
                    FirebaseAuth.getInstance().signOut();
                    intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    MotionToast.Companion.darkToast(
                            this,
                            "Error",
                            "Password update failed, please try again",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );
                }
            });
        }
    }

    // Validate Password
    private static boolean isValid(String password) {
        /*
            (?=.*[0-9])             # a number must occur at least once
            (?=.*[a-z])             # a lower case letter must occur at least once
            (?=.*[A-Z])             # an upper case letter must occur at least once
            (?=.*[!@#$%^&*()=_+.])  # a special character must occur at least once
            (?=\\S+$)               # no whitespace allowed in the entire string
            .{8,20}                 # length must be at least 8 but less than 20
         */

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()=_+.])(?=\\S+$).{8,20}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }
}