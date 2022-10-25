package com.app.dorav4.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.app.dorav4.R;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    // List of slide image resource
    int[] images = {
            R.drawable.img_welcome,
            R.drawable.img_take_photo,
            R.drawable.img_verify,
            R.drawable.img_post_photo,
    };

    int[] titles = {
            R.string.slider_first_title,
            R.string.slider_second_title,
            R.string.slider_third_title,
            R.string.slider_fourth_title,
    };

    int[] descriptions = {
            R.string.slider_first_desc,
            R.string.slider_second_desc,
            R.string.slider_third_desc,
            R.string.slider_fourth_desc,
    };

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // Inflate view with layout
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.single_onboarding_slide, container, false);

        // Instantiate views
        ImageView ivSlideImg = view.findViewById(R.id.ivSlideImg);
        TextView tvSlideTitle = view.findViewById(R.id.tvSlideTitle);
        TextView tvSlideDesc = view.findViewById(R.id.tvSlideDesc);

        // Set resources according to slide position
        ivSlideImg.setImageResource(images[position]);
        tvSlideTitle.setText(titles[position]);
        tvSlideDesc.setText(descriptions[position]);

        // Return the view that holds the resources
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
