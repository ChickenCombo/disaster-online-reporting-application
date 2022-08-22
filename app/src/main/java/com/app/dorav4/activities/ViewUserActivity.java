package com.app.dorav4.activities;

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

public class ViewUserActivity extends AppCompatActivity {
    ImageView ivUserPicture;
    TextView tvUserName;
    Button btnSendFriendRequest, btnDeclineFriendRequest;

    DatabaseReference usersReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        ivUserPicture = findViewById(R.id.ivUserPicture);
        tvUserName = findViewById(R.id.tvUserName);
        btnSendFriendRequest = findViewById(R.id.btnSendFriendRequest);
        btnDeclineFriendRequest = findViewById(R.id.btnDeclineFriendRequest);

        String userId = getIntent().getStringExtra("userId");

        usersReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        loadUser();
    }

    private void loadUser() {
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profilePicture = Objects.requireNonNull(snapshot.child("profilePicture").getValue()).toString();
                    String fullName = Objects.requireNonNull(snapshot.child("fullName").getValue()).toString();

                    Picasso.get().load(profilePicture).into(ivUserPicture);
                    tvUserName.setText(fullName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}