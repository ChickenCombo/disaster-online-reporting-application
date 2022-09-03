package com.app.dorav4.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;
import com.app.dorav4.activities.FindFriendsActivity;
import com.app.dorav4.activities.MainActivity;
import com.app.dorav4.activities.UpvotesActivity;
import com.app.dorav4.adapters.ChatAdapter;
import com.app.dorav4.adapters.UpvotesAdapter;
import com.app.dorav4.adapters.UsersAdapter;
import com.app.dorav4.models.Upvotes;
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

public class ChatFragment extends Fragment {
    SearchView searchView;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ConstraintLayout emptyView;

    DatabaseReference friendsReference, usersReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    List<Users> usersList;
    ChatAdapter chatAdapter;

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

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        friendsReference = FirebaseDatabase.getInstance().getReference("Friends").child(mUser.getUid());
        usersReference = FirebaseDatabase.getInstance().getReference("Users");

        usersList = new ArrayList<>();

        loadFriends();

        return view;
    }

    // Fetch all user's friends from the database
    private void loadFriends() {
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
                                usersList.add(users);
                                chatAdapter = new ChatAdapter(getActivity(), usersList);
                                recyclerView.setAdapter(chatAdapter);
                            }

                            // Show emptyView if recyclerView is empty
                            if (usersList.size() == 0) {
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.GONE);
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
}