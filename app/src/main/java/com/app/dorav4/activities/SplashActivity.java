package com.app.dorav4.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.app.dorav4.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    Intent intent;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(() -> {
            // Check if user has no internet connection
            if (!isConnected(SplashActivity.this)) {
                // Redirect to NoInternetActivity
                intent = new Intent(SplashActivity.this, NoInternetActivity.class);
                startActivity(intent);
                finish();
            } else {
                mAuth = FirebaseAuth.getInstance();
                mUser = mAuth.getCurrentUser();
                usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

                // Check if user is already logged in
                if (mUser != null) {
                    usersReference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Check if user has finished setting up his profile
                            if (snapshot.exists()) {
                                // Check if email is verified
                                if (mUser.isEmailVerified()) {
                                    intent = new Intent(SplashActivity.this, MainActivity.class);
                                } else {
                                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                                }
                            } else {
                                intent = new Intent(SplashActivity.this, SetupActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    // Redirect to LoginActivity if there is no user session
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1500);
    }

    // Check internet connection
    public boolean isConnected(Context context) {
        boolean status = false;
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
            status = capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }
}