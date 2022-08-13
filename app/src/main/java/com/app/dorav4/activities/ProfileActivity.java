package com.app.dorav4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.dorav4.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    TextView tvFullName, tvEmailAddress;
    ImageView ivProfilePicture, ivBack;
    Button btnChangeProfile, btnChangeEmail, btnChangePassword, btnLogout;
    Intent intent;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference usersReference;

    String profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvFullName = findViewById(R.id.tvFullName);
        tvEmailAddress = findViewById(R.id.tvEmailAddress);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ivBack = findViewById(R.id.ivBack);
        btnChangeProfile = findViewById(R.id.btnChangeProfile);
        btnChangeEmail = findViewById(R.id.btnChangeEmail);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        // btnChangeProfile OnClickListener
        btnChangeProfile.setOnClickListener(v -> {
            intent = new Intent(ProfileActivity.this, ChangeProfilePictureActivity.class);
            intent.putExtra("profilePicture", profilePicture);
            startActivity(intent);
        });

        // btnChangeEmail OnClickListener
        btnChangeEmail.setOnClickListener(v -> {
            intent = new Intent(ProfileActivity.this, ChangeEmailActivity.class);
            startActivity(intent);
        });

        // btnChangePassword OnClickListener
        btnChangePassword.setOnClickListener(v -> {
            intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // ivProfilePicture OnClickListener
        ivProfilePicture.setOnClickListener(v -> {
            intent = new Intent(ProfileActivity.this, ImageFullscreenActivity.class);
            intent.putExtra("reportPicture", profilePicture);
            startActivity(intent);
        });

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());

        // btnLogout OnClickListener
        btnLogout.setOnClickListener(v -> logout());

        showProfile();
    }

    // Show user info
    private void showProfile() {
        String uid = mUser.getUid();
        String email = mUser.getEmail();

        usersReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = Objects.requireNonNull(snapshot.child("fullName").getValue()).toString();
                    profilePicture = Objects.requireNonNull(snapshot.child("profilePicture").getValue()).toString();

                    Picasso.get().load(profilePicture).into(ivProfilePicture);
                    tvFullName.setText(fullName);
                    tvEmailAddress.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProfile();
    }

    // Log out
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}