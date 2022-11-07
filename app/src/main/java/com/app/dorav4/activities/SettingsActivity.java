package com.app.dorav4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.app.dorav4.R;

public class SettingsActivity extends AppCompatActivity {
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        ivBack = findViewById(R.id.ivBack);

        // Change status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(SettingsActivity.this, R.color.background));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference termsAndConditions = findPreference("termsAndConditions");
            Preference privacyPolicy = findPreference("privacyPolicy");
            Preference credits = findPreference("credits");

            assert termsAndConditions != null;
            // termsAndConditions OnPreferenceClickListener
            termsAndConditions.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), TermsAndConditionsActivity.class);
                startActivity(intent);
                return false;
            });

            assert privacyPolicy != null;
            // privacyPolicy OnPreferenceClickListener
            privacyPolicy.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), PrivacyPolicyActivity.class);
                startActivity(intent);
                return false;
            });

            assert credits != null;
            // credits OnPreferenceClickListener
            credits.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), CreditsActivity.class);
                startActivity(intent);
                return false;
            });
        }
    }
}