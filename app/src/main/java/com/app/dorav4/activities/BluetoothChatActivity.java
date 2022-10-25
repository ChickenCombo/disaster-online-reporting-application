package com.app.dorav4.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.app.dorav4.R;
import com.app.dorav4.fragments.BluetoothChatFragment;

public class BluetoothChatActivity extends AppCompatActivity {
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_chat);

        // Change status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(BluetoothChatActivity.this, R.color.background));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Set starting fragment to ReportsFragment
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, new BluetoothChatFragment());
        fragmentTransaction.commit();
    }
}