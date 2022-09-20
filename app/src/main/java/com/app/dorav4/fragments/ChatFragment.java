package com.app.dorav4.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;
import com.app.dorav4.activities.StartConversationActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatFragment extends Fragment {
    SearchView searchView;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ConstraintLayout emptyView;
    FloatingActionButton searchFriends;
    
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        searchView = view.findViewById(R.id.searchView);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        searchFriends = view.findViewById(R.id.searchFriends);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // searchFriends OnClickListener
        searchFriends.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StartConversationActivity.class);
            startActivity(intent);
        });

        return view;
    }
}