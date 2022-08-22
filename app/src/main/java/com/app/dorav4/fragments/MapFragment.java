package com.app.dorav4.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.dorav4.R;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

public class MapFragment extends Fragment {
    BubbleNavigationConstraintView bubbleNavigationConstraintView;
    FragmentTransaction fragmentTransaction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        bubbleNavigationConstraintView = view.findViewById(R.id.mapNavigation);

        // Set starting fragment to ReportsFragment
        fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mapContainer, new DisastersFragment());
        fragmentTransaction.commit();

        // Bottom Navigation Bar OnClickListener
        bubbleNavigationConstraintView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                fragmentTransaction = getParentFragmentManager().beginTransaction();

                if (position == 0) {
                    fragmentTransaction.replace(R.id.mapContainer, new DisastersFragment());
                } else if (position == 1) {
                    fragmentTransaction.replace(R.id.mapContainer, new EvacuateFragment());
                }

                fragmentTransaction.commit();
            }
        });

        return view;
    }
}