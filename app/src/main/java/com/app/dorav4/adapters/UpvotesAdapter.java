package com.app.dorav4.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;
import com.app.dorav4.holders.UpvotesViewHolder;
import com.app.dorav4.models.Upvotes;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UpvotesAdapter extends RecyclerView.Adapter<UpvotesViewHolder>{
    Context context;
    List<Upvotes> upvotesList;

    public UpvotesAdapter(Context context, List<Upvotes> upvotesList) {
        this.context = context;
        this.upvotesList = upvotesList;
    }

    @NonNull
    @Override
    public UpvotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_view_upvotes, parent, false);
        return new UpvotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpvotesViewHolder holder, int position) {
        // Fetch user's details
        String fullName = upvotesList.get(position).getFullName();
        String profilePicture = upvotesList.get(position).getProfilePicture();

        // Set user's details
        holder.tvUserName.setText(fullName);
        Picasso.get().load(profilePicture).into(holder.ivUserProfile);
    }

    @Override
    public int getItemCount() {
        return upvotesList.size();
    }
}
