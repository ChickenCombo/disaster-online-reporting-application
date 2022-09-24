package com.app.dorav4.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;
import com.app.dorav4.activities.PostReportActivity;
import com.app.dorav4.adapters.ReportsAdapter;
import com.app.dorav4.models.Reports;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReportsFragment extends Fragment {
    FloatingActionButton addReport;
    Intent intent;
    ConstraintLayout emptyView;
    RecyclerView recyclerView;
    List<Reports> reportsList;
    ReportsAdapter reportsAdapter;
    NestedScrollView nestedScrollView;
    LinearLayoutManager layoutManager;
    SearchView searchView;
    Button btnJumpToTop;
    Animation slideUp, slideDown;

    DatabaseReference reportsReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        reportsList = new ArrayList<>();

        emptyView = view.findViewById(R.id.emptyView);
        addReport = view.findViewById(R.id.addReport);
        btnJumpToTop = view.findViewById(R.id.btnJumpToTop);
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);

        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);

        // Show newest reports on top of the recycler view
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        reportsReference = FirebaseDatabase.getInstance().getReference().child("Reports");

        // addReport OnClickListener
        addReport.setOnClickListener(v -> {
            intent = new Intent(getActivity(), PostReportActivity.class);
            startActivity(intent);
        });

        // btnJumpToTop OnClickListener
        btnJumpToTop.setOnClickListener(v -> nestedScrollView.smoothScrollTo(0, 0));

        // searchView OnQueryTextListener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    searchReports(query);
                } else {
                    loadReports();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!query.isEmpty()) {
                    searchReports(query);
                } else {
                    loadReports();
                }
                return false;
            }
        });

        // Show btnJumpToTop button at the end of nested scroll view
        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (nestedScrollView.getScrollY() == 0) {
                btnJumpToTop.startAnimation(slideDown);
                btnJumpToTop.setVisibility(View.GONE);
            } else if (nestedScrollView.getChildAt(0).getBottom() <= (nestedScrollView.getHeight() + nestedScrollView.getScrollY())){
                btnJumpToTop.startAnimation(slideUp);
                btnJumpToTop.setVisibility(View.VISIBLE);
            }
        });

        loadReports();

        return view;
    }

    // Load reports into the recycler view
    private void loadReports() {
        reportsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportsList.clear();

                // Add database data inside the list
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Reports reports = ds.getValue(Reports.class);
                    reportsList.add(reports);
                    reportsAdapter = new ReportsAdapter(getActivity(), reportsList);
                    recyclerView.setAdapter(reportsAdapter);
                }

                // Show emptyView if recyclerView is empty
                if (reportsList.size() == 0) {
                    searchView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    searchView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Search for reports
    private void searchReports(String searchQuery) {
        reportsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportsList.clear();

                // Add database data inside the list
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Reports reports = ds.getValue(Reports.class);

                    if (reports != null) {
                        boolean disasterTypeQuery = reports.getDisasterType().toLowerCase().contains(searchQuery.toLowerCase());
                        boolean addressQuery = reports.getAddress().toLowerCase().contains(searchQuery.toLowerCase());
                        boolean nameQuery = reports.getFullName().toLowerCase().contains(searchQuery.toLowerCase());

                        if (disasterTypeQuery || addressQuery || nameQuery) {
                            reportsList.add(reports);
                        }
                    }

                    reportsAdapter = new ReportsAdapter(getActivity(), reportsList);
                    recyclerView.setAdapter(reportsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}