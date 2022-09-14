package com.app.dorav4.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.app.dorav4.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText etEmailAddress, etPassword;
    TextInputLayout tilEmailAddress, tilPassword;
    TextView tvTermsAndPolicies;
    ImageView ivBack;
    Button btnRegister;
    Intent intent;

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmailAddress = findViewById(R.id.etEmailAddress);
        etPassword = findViewById(R.id.etPassword);
        tilEmailAddress = findViewById(R.id.tilEmailAddress);
        tilPassword = findViewById(R.id.tilPassword);
        tvTermsAndPolicies = findViewById(R.id.tvTermsAndPolicies);
        ivBack = findViewById(R.id.ivBack);
        btnRegister = findViewById(R.id.btnRegister);

        progressDialog = new ProgressDialog(RegisterActivity.this);

        mAuth = FirebaseAuth.getInstance();

        // tvTermsAndPolicies OnClickListener
        customTextView(tvTermsAndPolicies);

        // btnRegister OnClickListener
        btnRegister.setOnClickListener(v -> registerUser());

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }

    // Register user
    private void registerUser() {
        boolean isEmailValid = false, isPasswordValid = false;
        String email = Objects.requireNonNull(etEmailAddress.getText()).toString().trim();
        String password = Objects.requireNonNull(etPassword.getText()).toString().trim();

        // Validate password requirements
        if (!isValid(password)) {
            tilPassword.setError("A minimum of 8 characters password containing at least one uppercase letter, one lowercase letter, one special character, and one number is required.");
        } else {
            isPasswordValid = true;
            tilPassword.setError(null);
        }

        // Validate email address
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmailAddress.setError("Invalid email address, please try again.");
        } else {
            isEmailValid = true;
            tilEmailAddress.setError(null);
        }

        // If all inputs are valid
        if (isEmailValid && isPasswordValid) {
            // ProgressDialog
            progressDialog.setTitle("Register");
            progressDialog.setMessage("Creating your account");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Create a new user using Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();

                    MotionToast.Companion.darkToast(
                            this,
                            "Success",
                            "Account registration complete",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );

                    intent = new Intent(RegisterActivity.this, SetupActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    progressDialog.dismiss();

                    MotionToast.Companion.darkToast(
                            this,
                            "Error",
                            "Email already in use",
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
            (?=.*[0-9])           # a number must occur at least once
            (?=.*[a-z])           # a lower case letter must occur at least once
            (?=.*[A-Z])           # an upper case letter must occur at least once
            (?=.*[!@#$%^&*()+=])  # a special character must occur at least once
            (?=\\S+$)             # no whitespace allowed in the entire string
            .{8,20}               # length must be at least 8 but less than 20
         */

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()+=])(?=\\S+$).{8,20}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    // Custom TextView with multiple onClickListeners
    private void customTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder("By signing up, you agree to DORA's  ");
        spanTxt.append("Terms and Conditions");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                intent = new Intent(RegisterActivity.this, TermsAndConditionsActivity.class);
                startActivity(intent);
            }
        }, spanTxt.length() - "Terms and Conditions".length(), spanTxt.length(), 0);
        spanTxt.append(" & ");
        spanTxt.append("Privacy Policy");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                intent = new Intent(RegisterActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        }, spanTxt.length() - "Privacy Policy".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }
}