package com.app.dorav4.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

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

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ViewUserActivity extends AppCompatActivity {
    ImageView ivUserPicture, ivBack;
    TextView tvUserName;
    Button btnSendFriendRequest, btnDeclineFriendRequest;

    DatabaseReference usersReference, friendRequestsReference, friendsReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    String currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_user);

        // Set background to transparent
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ivUserPicture = findViewById(R.id.ivUserPicture);
        ivBack = findViewById(R.id.ivBack);
        tvUserName = findViewById(R.id.tvUserName);
        btnSendFriendRequest = findViewById(R.id.btnSendFriendRequest);
        btnDeclineFriendRequest = findViewById(R.id.btnDeclineFriendRequest);

        // Fetch data from previous activity
        String userId = getIntent().getStringExtra("userId");

        friendRequestsReference = FirebaseDatabase.getInstance().getReference("Friend Requests");
        usersReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        friendsReference = FirebaseDatabase.getInstance().getReference("Friends");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        currentState = "not_friends";
        btnDeclineFriendRequest.setVisibility(View.GONE);

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());

        // btnSendFriendRequest OnClickListener
        btnSendFriendRequest.setOnClickListener(v -> friendRequest(userId));

        // btnDeclineFriendRequest OnClickListener
        btnDeclineFriendRequest.setOnClickListener(v -> declineRequest(userId));

        // Load the user details
        loadUser();

        // Check the current request state
        checkState(userId);
    }

    // Load the user's profile
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

    /*
        currentState status:
            not_friends             # User X is not friends with user Y
            request_sent            # User X sent a friend request to user Y
            request_received        # User Y received a friend request from user X
            friends                 # User X and user Y are friends
    */

    // Check current request state
    private void checkState(String userId) {
        friendRequestsReference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if current user has already received or sent a friend request
                if (snapshot.hasChild(userId)) {
                    String request = Objects.requireNonNull(snapshot.child(userId).child("request").getValue()).toString();
                    if (request.equals("received")) {
                        // If the other user has received a friend request, show accept friend request button
                        btnSendFriendRequest.setText(R.string.accept_friend_request);
                        currentState = "request_received";

                        // If the other user has received a friend request, show accept friend request button
                        btnDeclineFriendRequest.setVisibility(View.VISIBLE);
                    } else if (request.equals("sent")) {
                        // If current user has sent a friend request, show cancel request button
                        btnSendFriendRequest.setText(R.string.cancel_friend_request);
                        currentState = "request_sent";
                    }
                // If friend request doesn't exist, check if they're already friends
                } else {
                    friendsReference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(userId)) {
                                // If users are already friends, show unfriend button
                                currentState = "friends";
                                btnSendFriendRequest.setText(R.string.unfriend);
                                btnSendFriendRequest.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.primary_red));
                            } else {
                                btnSendFriendRequest.setEnabled(true);
                                btnSendFriendRequest.setText(R.string.send_friend_request);
                                btnSendFriendRequest.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.primary_green));
                                btnDeclineFriendRequest.setVisibility(View.GONE);
                                currentState = "not_friends";
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Process friend request
    private void friendRequest(String userId) {
        btnSendFriendRequest.setEnabled(false);

        // Send Friend Request
        if (currentState.equals("not_friends")) {
            friendRequestsReference.child(mUser.getUid()).child(userId).child("request").setValue("sent").addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    friendRequestsReference.child(userId).child(mUser.getUid()).child("request").setValue("received").addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            btnSendFriendRequest.setEnabled(true);
                            btnSendFriendRequest.setText(R.string.cancel_friend_request);
                            currentState = "request_sent";

                            MotionToast.Companion.darkToast(
                                    this,
                                    "Success",
                                    "Friend request sent",
                                    MotionToastStyle.SUCCESS,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
                            );
                        }
                    });
                }
            });
        }

        // Cancel Friend Request
        if (currentState.equals("request_sent")) {
            friendRequestsReference.child(mUser.getUid()).child(userId).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    friendRequestsReference.child(userId).child(mUser.getUid()).removeValue().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            btnSendFriendRequest.setEnabled(true);
                            btnSendFriendRequest.setText(R.string.send_friend_request);
                            currentState = "not_friends";

                            MotionToast.Companion.darkToast(
                                    this,
                                    "Success",
                                    "Friend request cancelled",
                                    MotionToastStyle.SUCCESS,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
                            );
                        }
                    });
                }
            });
        }

        // Accept Friend Request
        if (currentState.equals("request_received")) {
            // Hide decline friend request if user accepted
            btnDeclineFriendRequest.setVisibility(View.GONE);

            // Add to both users to friends database
            friendsReference.child(mUser.getUid()).child(userId).setValue("friends").addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   friendsReference.child(userId).child(mUser.getUid()).setValue("friends").addOnCompleteListener(task1 -> {
                       if (task1.isSuccessful()) {
                           // Remove the request after accepting the request
                           friendRequestsReference.child(mUser.getUid()).child(userId).removeValue().addOnCompleteListener(task2 -> {
                               if (task2.isSuccessful()) {
                                   friendRequestsReference.child(userId).child(mUser.getUid()).removeValue().addOnCompleteListener(task3 -> {
                                       if (task3.isSuccessful()) {
                                           btnSendFriendRequest.setEnabled(true);
                                           btnSendFriendRequest.setText(R.string.unfriend);
                                           currentState = "friends";

                                           MotionToast.Companion.darkToast(
                                                   this,
                                                   "Success",
                                                   "Friend request accepted",
                                                   MotionToastStyle.SUCCESS,
                                                   MotionToast.GRAVITY_BOTTOM,
                                                   MotionToast.LONG_DURATION,
                                                   ResourcesCompat.getFont(this, R.font.helvetica_regular)
                                           );
                                       }
                                   });
                                   btnSendFriendRequest.setEnabled(true);
                               }
                           });
                       }
                   });
               }
            });
        }

        // Unfriend
        if (currentState.equals("friends")) {
            friendsReference.child(mUser.getUid()).child(userId).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    friendsReference.child(userId).child(mUser.getUid()).removeValue().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            btnSendFriendRequest.setEnabled(true);
                            btnSendFriendRequest.setText(R.string.send_friend_request);
                            btnSendFriendRequest.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.primary_green));
                            currentState = "not_friends";

                            MotionToast.Companion.darkToast(
                                    this,
                                    "Success",
                                    "User has been unfriended",
                                    MotionToastStyle.SUCCESS,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
                            );
                        }
                    });
                }
            });
        }
    }

    // Decline friend request
    private void declineRequest(String userId) {
        friendRequestsReference.child(mUser.getUid()).child(userId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                friendRequestsReference.child(userId).child(mUser.getUid()).removeValue().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        btnSendFriendRequest.setEnabled(true);
                        btnSendFriendRequest.setText(R.string.send_friend_request);
                        currentState = "not_friends";

                        MotionToast.Companion.darkToast(
                                this,
                                "Success",
                                "Friend request declined",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(this, R.font.helvetica_regular)
                        );

                        btnDeclineFriendRequest.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}