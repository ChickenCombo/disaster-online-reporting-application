package com.app.dorav4.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;
import com.app.dorav4.activities.StartConversationActivity;
import com.app.dorav4.adapters.MessagesAdapter;
import com.app.dorav4.models.Users;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    SearchView searchView;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ConstraintLayout emptyView;
    FloatingActionButton searchFriends;

    List<Users> usersList;
    MessagesAdapter messagesAdapter;

    DatabaseReference chatsReference, usersReference;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        searchView = view.findViewById(R.id.searchView);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        searchFriends = view.findViewById(R.id.searchFriends);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        chatsReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(mUser.getUid());
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        usersList = new ArrayList<>();

        // searchFriends OnClickListener
        searchFriends.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StartConversationActivity.class);
            startActivity(intent);
        });

        // searchView OnQueryTextListener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    searchMessages(query);
                } else {
                    loadMessages();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!query.isEmpty()) {
                    searchMessages(query);
                } else {
                    loadMessages();
                }
                return false;
            }
        });

        loadMessages();

        return view;
    }

    // Get all chat conversations
    private void loadMessages() {
        // Get chat ids
        chatsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String uid = ds.getRef().getKey();

                    // Get user's info
                    usersReference.orderByKey().equalTo(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Users users = ds.getValue(Users.class);
                                usersList.add(users);

                                // Set recycler view's data
                                chatsReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        messagesAdapter = new MessagesAdapter(getActivity(), usersList);
                                        recyclerView.setAdapter(messagesAdapter);

                                        // Show empty view if list is empty
                                        showEmptyView();
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

                // Show empty view if list is empty
                showEmptyView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Search messages
    private void searchMessages(String query) {
        chatsReference.addValueEventListener(new ValueEventListener() {
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

                                messagesAdapter = new MessagesAdapter(getActivity(), usersList);
                                recyclerView.setAdapter(messagesAdapter);
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