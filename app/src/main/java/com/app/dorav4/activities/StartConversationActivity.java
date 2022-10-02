package com.app.dorav4.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;
import com.app.dorav4.adapters.ChatAdapter;
import com.app.dorav4.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StartConversationActivity extends AppCompatActivity {
    SearchView searchView;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ConstraintLayout emptyView;
    ImageView ivBack;
    TextView tvToolbarHeader;

    DatabaseReference friendsReference, usersReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    List<Users> usersList;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_conversation);

        searchView = findViewById(R.id.searchView);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);
        ivBack = findViewById(R.id.ivBack);
        tvToolbarHeader = findViewById(R.id.tvToolbarHeader);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        friendsReference = FirebaseDatabase.getInstance().getReference("Friends").child(mUser.getUid());
        usersReference = FirebaseDatabase.getInstance().getReference("Users");

        // Set header text
        tvToolbarHeader.setText(R.string.start_conversation_header);

        // searchView OnQueryTextListener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    searchFriend(query);
                } else {
                    loadFriends();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!query.isEmpty()) {
                    searchFriend(query);
                } else {
                    loadFriends();
                }
                return false;
            }
        });

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());

        // Load all friends
        loadFriends();
    }

    // Fetch all user's friends from the database
    private void loadFriends() {
        usersList = new ArrayList<>();

        // Get friend ids
        friendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for (DataSnapshot ds: snapshot.getChildren()) {
                    String uid = ds.getRef().getKey();

                    // Get user's info
                    usersReference.orderByKey().equalTo(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Users users = ds.getValue(Users.class);
                                usersList.add(users);
                            }

                            // Set recycler view's data
                            friendsReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    chatAdapter = new ChatAdapter(StartConversationActivity.this, usersList);
                                    recyclerView.setAdapter(chatAdapter);

                                    // Show empty view if list is empty
                                    showEmptyView();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                // Show empty view if list is empty
                showEmptyView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Search a friend
    private void searchFriend(String query) {
        friendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                // Get the uid of the current user's friend
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String uid = ds.getRef().getKey();

                    // Get the user's details based on their uid
                    usersReference.orderByKey().equalTo(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Users users = ds.getValue(Users.class);

                                // Search query
                                if (users != null) {
                                    boolean nameQuery = users.getFullName().toLowerCase().contains(query.toLowerCase());
                                    if (nameQuery) {
                                        usersList.add(users);
                                    }
                                }


                                chatAdapter = new ChatAdapter(StartConversationActivity.this, usersList);
                                recyclerView.setAdapter(chatAdapter);
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

    // Show emptyView if list is empty
    private void showEmptyView() {
        if (usersList.size() == 0) {
            searchView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            searchView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}