package com.app.dorav4.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;
import com.app.dorav4.holders.UsersViewHolder;
import com.app.dorav4.models.Users;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersViewHolder> {
    Context context;
    List<Users> usersList;

    public UsersAdapter(Context context, List<Users> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_view_user, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        // Fetch user's details
        String fullName = usersList.get(position).getFullName();
        String profilePicture = usersList.get(position).getProfilePicture();

        // Set user's details
        holder.tvUserName.setText(fullName);
        Picasso.get().load(profilePicture).into(holder.ivUserProfile);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}
