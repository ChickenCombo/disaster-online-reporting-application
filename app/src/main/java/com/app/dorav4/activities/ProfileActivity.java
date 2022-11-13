package com.app.dorav4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.app.dorav4.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ProfileActivity extends AppCompatActivity {
    TextView tvFullName, tvEmailAddress;
    ImageView ivProfilePicture, ivBack;
    Button btnChangeProfile, btnChangeEmail, btnChangePassword, btnLogout, btnChangeName;
    Intent intent;
    AlertDialog dialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference usersReference, reportsReference;

    String profilePicture;
    boolean status = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvFullName = findViewById(R.id.tvFullName);
        tvEmailAddress = findViewById(R.id.tvEmailAddress);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ivBack = findViewById(R.id.ivBack);
        btnChangeName = findViewById(R.id.btnChangeName);
        btnChangeProfile = findViewById(R.id.btnChangeProfile);
        btnChangeEmail = findViewById(R.id.btnChangeEmail);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        reportsReference = FirebaseDatabase.getInstance().getReference().child("Reports");

        // btnChangeName OnClickListener
        btnChangeName.setOnClickListener(v -> changeName());

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

    // Change user's name
    private void changeName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_change_name, null);
        EditText etFullName = view.findViewById(R.id.etFullName);
        TextInputLayout tilFullName = view.findViewById(R.id.tilFullName);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        // btnCancel OnClickListener
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // btnSubmit OnClickListener
        btnSubmit.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();

            // Input validation
            if (!fullName.isEmpty()) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("fullName", fullName);

                // Change name on users database
                usersReference.child(mUser.getUid()).updateChildren(hashMap).addOnFailureListener(e -> status = false);


                // Change name on reports database
                Query query = reportsReference.orderByChild("userId").equalTo(mUser.getUid());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String child = ds.getKey();
                            if (child != null) {
                                if (snapshot.child(child).hasChild("fullName")) {
                                    snapshot.getRef().child(child).child("fullName").setValue(fullName).addOnFailureListener(e -> status = false);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // Change name on comments database
                reportsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            String child = ds.getKey();

                            if (child != null) {
                                if (snapshot.child(child).hasChild("Comments")) {
                                    String child1 = String.valueOf(snapshot.child(child).getKey());
                                    Query query = reportsReference.child(child1).child("Comments").orderByChild("userId").equalTo(mUser.getUid());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds: snapshot.getChildren()) {
                                                String child = ds.getKey();

                                                if (child != null) {
                                                    if (snapshot.child(child).hasChild("name")) {
                                                        snapshot.getRef().child(child).child("name").setValue(fullName).addOnFailureListener(e -> status = false);
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // Show toast message
                if (status) {
                    MotionToast.Companion.darkToast(
                            this,
                            "Success",
                            "Name changed successfully",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );
                } else {
                    MotionToast.Companion.darkToast(
                            this,
                            "Error",
                            "Name change failed, please try again",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );
                }

                dialog.dismiss();
            } else {
                tilFullName.setError("Full name cannot be empty");
            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    // Remove user's token then log-out
    private void logout() {
        FirebaseDatabase.getInstance().getReference("Tokens").child(mUser.getUid()).removeValue();
        FirebaseAuth.getInstance().signOut();
        intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}