package com.app.dorav4.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dorav4.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ChangeEmailActivity extends AppCompatActivity {
    TextInputLayout tilEmailAddress, tilPassword, tilNewEmailAddress;
    EditText etEmailAddress, etPassword, etNewEmailAddress;
    ImageView ivBack;
    Button btnAuthenticate, btnChangeEmail;
    MaterialCardView cvAuthenticate, cvEmail;
    Intent intent;

    ProgressDialog progressDialog;

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

        progressDialog = new ProgressDialog(ChangeEmailActivity.this);

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
            // ProgressDialog
            progressDialog.setTitle("Authenticating");
            progressDialog.setMessage("Verifying your account");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Re-authenticate user
            AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);
            mUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(ChangeEmailActivity.this, "Account successfully authenticated!", Toast.LENGTH_SHORT).show();

                    // Hide authentication form and show update email form
                    cvAuthenticate.setVisibility(View.GONE);
                    cvEmail.setVisibility(View.VISIBLE);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ChangeEmailActivity.this, "Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ChangeEmailActivity.this, "Email has been updated, please verify and re-login your account.", Toast.LENGTH_SHORT).show();
                    mUser.sendEmailVerification();
                    FirebaseAuth.getInstance().signOut();
                    intent = new Intent(ChangeEmailActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ChangeEmailActivity.this, "Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}