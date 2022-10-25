package com.app.dorav4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.app.dorav4.R;
import com.app.dorav4.fragments.ChatFragment;
import com.app.dorav4.fragments.MapFragment;
import com.app.dorav4.fragments.ReportsFragment;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    // Global variables for the current user's information
    public static String fullName;
    public static String profilePicture;
    public static String userId;

    FragmentTransaction fragmentTransaction;
    BubbleNavigationConstraintView bubbleNavigationConstraintView;
    NavigationView sidebarNavigation;
    ImageView ivProfile, ivMenu, sidebarProfile;
    TextView sidebarName, sidebarEmail;
    Intent intent;
    DrawerLayout drawerLayout;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivProfile = findViewById(R.id.ivProfile);
        ivMenu = findViewById(R.id.ivMenu);
        bubbleNavigationConstraintView = findViewById(R.id.bubbleNavigation);
        sidebarNavigation = findViewById(R.id.sidebarNavigation);

        sidebarNavigation.bringToFront();
        drawerLayout = findViewById(R.id.drawerLayout);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        // Add sidebar header
        View sidebarView = sidebarNavigation.inflateHeaderView(R.layout.drawer_header);
        sidebarProfile = sidebarView.findViewById(R.id.ivProfilePicture);
        sidebarName = sidebarView.findViewById(R.id.tvFullName);
        sidebarEmail = sidebarView.findViewById(R.id.tvEmailAddress);

        // Change status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.background));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Set starting fragment to ReportsFragment
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, new ReportsFragment());
        fragmentTransaction.commit();

        // ivProfile OnClickListener
        ivProfile.setOnClickListener(v -> {
            intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // ivMenuOnClickListener
        ivMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Sidebar Navigation OnClickListener
        sidebarNavigation.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navGuides) {
                intent = new Intent(MainActivity.this, GuidesListActivity.class);
                startActivity(intent);
            } else if (id == R.id.navHotlines) {
                intent = new Intent(MainActivity.this, HotlinesActivity.class);
                startActivity(intent);
            } else if (id == R.id.navAdvisories) {
                intent = new Intent(MainActivity.this, AdvisoriesActivity.class);
                startActivity(intent);
            } else if (id == R.id.navFindFriends) {
                intent = new Intent(MainActivity.this, FindFriendsActivity.class);
                startActivity(intent);
            } else if (id == R.id.navSettings) {
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }

            return true;
        });

        // Bottom Navigation Bar OnClickListener
        bubbleNavigationConstraintView.setNavigationChangeListener((view, position) -> {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();

            if (position == 0) {
                fragmentTransaction.replace(R.id.fragmentContainer, new ReportsFragment());
            } else if (position == 1) {
                fragmentTransaction.replace(R.id.fragmentContainer, new ChatFragment());
            } else if (position == 2) {
                fragmentTransaction.replace(R.id.fragmentContainer, new MapFragment());
            }

            fragmentTransaction.commit();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Send user back to LoginActivity if session is expired
        if (mUser == null) {
            intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Get user details on the database
            usersReference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Get user details on start up
                        profilePicture = Objects.requireNonNull(snapshot.child("profilePicture").getValue()).toString();
                        fullName = Objects.requireNonNull(snapshot.child("fullName").getValue()).toString();
                        userId = mUser.getUid();

                        // Set profile picture
                        Picasso.get().load(profilePicture).into(ivProfile);

                        // Set profile details on sidebar navigation
                        Picasso.get().load(profilePicture).into(sidebarProfile);
                        sidebarName.setText(fullName);
                        sidebarEmail.setText(mUser.getEmail());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}