package com.app.dorav4.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    DatabaseReference reportsReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        emptyView = view.findViewById(R.id.emptyView);
        addReport = view.findViewById(R.id.addReport);
        recyclerView = view.findViewById(R.id.recyclerView);

        reportsReference = FirebaseDatabase.getInstance().getReference().child("Reports");

        // addReport OnClickListener
        addReport.setOnClickListener(v -> {
            intent = new Intent(getActivity(), PostReportActivity.class);
            startActivity(intent);
        });

        loadReports();

        return view;
    }

    // Load reports into the recycler view
    private void loadReports() {
        reportsList = new ArrayList<>();

        // Show newest reports on top of the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

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
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}