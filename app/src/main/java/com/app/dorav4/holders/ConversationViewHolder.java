package com.app.dorav4.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;

public class ConversationViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivReceiverPicture;
    public TextView tvMessageReceiver, tvMessageSender;

    public ConversationViewHolder(@NonNull View itemView) {
        super(itemView);

        ivReceiverPicture = itemView.findViewById(R.id.ivReceiverPicture);
        tvMessageReceiver = itemView.findViewById(R.id.tvMessageReceiver);
        tvMessageSender = itemView.findViewById(R.id.tvMessageSender);
    }
}
