package com.app.dorav4.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.dorav4.R;
import com.app.dorav4.adapters.SliderAdapter;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager viewPager;
    SliderAdapter sliderAdapter;
    LinearLayout dotsLayout;
    TextView[] dots;
    Button btnGetStarted, btnNext;
    Animation animation;
    Intent intent;
    int currentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Remove status bar for onboarding
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Instantiate hooks
        viewPager = findViewById(R.id.vpOnboardingSlider);
        dotsLayout = findViewById(R.id.linearDots);
        btnGetStarted = findViewById(R.id.btnGetStarted);
        btnNext = findViewById(R.id.btnNext);

        // Instantiate adapter
        sliderAdapter = new SliderAdapter(OnboardingActivity.this);
        viewPager.setAdapter(sliderAdapter);

        // Call addDots() function
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
    }

    // Dots
    private void addDots(int position) {
        dots = new TextView[4];
        dotsLayout.removeAllViews();

        for(int i=0; i < dots.length; i++) {
            dots[i] = new TextView(OnboardingActivity.this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(ContextCompat.getColor(OnboardingActivity.this, R.color.safe_gray));

            // Add dots to dotsLayout
            dotsLayout.addView(dots[i]);
        }

        if(dots.length > 0) {
            dots[position].setTextColor(ContextCompat.getColor(OnboardingActivity.this, R.color.primary_green));
        }
    }

    public void next(View view) {
        viewPager.setCurrentItem(currentPos + 1);
    }

    public void getStarted(View view) {
        intent = new Intent(OnboardingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // ViewPager's change page listener
    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPos = position;
            // Only show button at the last slide
            // btnGetStarted's visibility attribute is set to INVISIBLE by default
            if(position == dots.length - 1) {
                animation = AnimationUtils.loadAnimation(OnboardingActivity.this, R.anim.fade_in_from_below);
                btnGetStarted.setAnimation(animation);
                btnGetStarted.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.INVISIBLE);
            } else {
                btnGetStarted.setVisibility(View.INVISIBLE);
                btnNext.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
}