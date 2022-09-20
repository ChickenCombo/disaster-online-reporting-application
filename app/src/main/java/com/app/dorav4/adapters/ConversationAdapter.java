package com.app.dorav4.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;
import com.app.dorav4.holders.ConversationViewHolder;
import com.app.dorav4.models.Chats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationViewHolder> {
    Context context;
    List<Chats> chatsList;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    String photoUri;

    public ConversationAdapter(Context context, List<Chats> chatsList, String photoUri) {
        this.context = context;
        this.chatsList = chatsList;
        this.photoUri = photoUri;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get current user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        View view = LayoutInflater.from(context).inflate(R.layout.single_view_chat, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        if (chatsList.get(position).getUserId().equals(mUser.getUid())) {
            holder.ivReceiverPicture.setVisibility(View.GONE);
            holder.tvMessageReceiver.setVisibility(View.GONE);
            holder.tvMessageSender.setVisibility(View.VISIBLE);

            holder.tvMessageSender.setText(chatsList.get(position).getMessage());
        } else {
            holder.ivReceiverPicture.setVisibility(View.VISIBLE);
            holder.tvMessageReceiver.setVisibility(View.VISIBLE);
            holder.tvMessageSender.setVisibility(View.GONE);

            Picasso.get().load(photoUri).into(holder.ivReceiverPicture);
            holder.tvMessageReceiver.setText(chatsList.get(position).getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }
}
