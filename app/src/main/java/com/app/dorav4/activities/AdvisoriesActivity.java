package com.app.dorav4.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.dorav4.R;
import com.app.dorav4.fragments.NDRRMCFragment;
import com.app.dorav4.fragments.NOAHFragment;
import com.app.dorav4.fragments.PAGASAFragment;
import com.app.dorav4.fragments.PHIVOLCSFragment;
import com.app.dorav4.fragments.RedCrossFragment;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;

public class AdvisoriesActivity extends AppCompatActivity {
    ImageView ivBack;

    FragmentTransaction fragmentTransaction;
    BubbleNavigationConstraintView bubbleNavigationConstraintView;

    Fragment pagasaFragment, redcrossFragment, phivolcsFragment, ndrrmcFragment, noahFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advisories);

        bubbleNavigationConstraintView = findViewById(R.id.bubbleNavigation);
        ivBack = findViewById(R.id.ivBack);

        // Change status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(AdvisoriesActivity.this, R.color.background));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Initialize fragments
        pagasaFragment = new PAGASAFragment();
        redcrossFragment = new RedCrossFragment();
        phivolcsFragment = new PHIVOLCSFragment();
        ndrrmcFragment = new NDRRMCFragment();
        noahFragment = new NOAHFragment();

        // Set starting fragment to PAGASAFragment
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, pagasaFragment);
        fragmentTransaction.commit();

        // Bottom Navigation Bar OnClickListener
        bubbleNavigationConstraintView.setNavigationChangeListener((view, position) -> {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();

            if (position == 0) {
                fragmentTransaction.replace(R.id.fragmentContainer, pagasaFragment);
            } else if (position == 1) {
                fragmentTransaction.replace(R.id.fragmentContainer, redcrossFragment);
            } else if (position == 2) {
                fragmentTransaction.replace(R.id.fragmentContainer, phivolcsFragment);
            } else if (position == 3) {
                fragmentTransaction.replace(R.id.fragmentContainer, ndrrmcFragment);
            } else if (position == 4) {
                fragmentTransaction.replace(R.id.fragmentContainer, noahFragment);
            }

            fragmentTransaction.commit();
        });

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }
}