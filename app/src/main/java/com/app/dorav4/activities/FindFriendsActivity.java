package com.app.dorav4.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;
import com.app.dorav4.adapters.UsersAdapter;
import com.app.dorav4.models.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindFriendsActivity extends AppCompatActivity {
    ImageView ivBack;
    SearchView searchView;
    ConstraintLayout emptyView;

    RecyclerView recyclerView;

    List<Users> usersList;
    UsersAdapter usersAdapter;

    DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        ivBack = findViewById(R.id.ivBack);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        emptyView = findViewById(R.id.emptyView);

        usersList = new ArrayList<>();

        // Change status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(FindFriendsActivity.this, R.color.background));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        usersReference = FirebaseDatabase.getInstance().getReference("Users");

        // searchView OnQueryTextListener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    searchUsers(query);
                    emptyView.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!query.isEmpty()) {
                    searchUsers(query);
                    emptyView.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());

        // Show empty view
        emptyView.setVisibility(View.VISIBLE);
    }

    // Search for users
    private void searchUsers(String searchQuery) {
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for (DataSnapshot ds: snapshot.getChildren()) {
                    String uid = ds.getRef().getKey();
                    Users users = ds.getValue(Users.class);

                    if (users != null && uid != null) {
                        // Hide current user from the list
                        if (!uid.equals(MainActivity.userId)) {
                            // Search query
                            boolean nameQuery = users.getFullName().toLowerCase().contains(searchQuery.toLowerCase());
                            if (nameQuery) {
                                usersList.add(users);
                            }
                        }
                    }

                    usersAdapter = new UsersAdapter(FindFriendsActivity.this, usersList);
                    recyclerView.setAdapter(usersAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}