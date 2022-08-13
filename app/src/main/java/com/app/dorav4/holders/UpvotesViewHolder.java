package com.app.dorav4.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;

public class UpvotesViewHolder extends RecyclerView.ViewHolder{
    public ImageView ivUserProfile;
    public TextView tvUserName;

    public UpvotesViewHolder(@NonNull View itemView) {
        super(itemView);

        ivUserProfile = itemView.findViewById(R.id.ivUserProfile);
        tvUserName = itemView.findViewById(R.id.tvUserName);
    }
}
