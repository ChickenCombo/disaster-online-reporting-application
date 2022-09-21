package com.app.dorav4.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.app.dorav4.R;
import com.app.dorav4.holders.ConversationViewHolder;
import com.app.dorav4.models.Chats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationViewHolder> {
    Context context;
    List<Chats> chatsList;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    String photoUri;
    String receiverUserId;
    String currentUserId;

    public ConversationAdapter(Context context, List<Chats> chatsList, String photoUri, String receiverUserId) {
        this.context = context;
        this.chatsList = chatsList;
        this.photoUri = photoUri;
        this.receiverUserId = receiverUserId;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get current user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        currentUserId = mUser.getUid();

        View view = LayoutInflater.from(context).inflate(R.layout.single_view_chat, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        // Set chat bubble
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

        // chatLayout OnClickListener
        holder.chatLayout.setOnLongClickListener(v -> {
            deleteMessage(position);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    private void deleteMessage(int position) {
        DatabaseReference chatsReference = FirebaseDatabase.getInstance().getReference("Chats");

        // Delete message
        if (currentUserId.equals(chatsList.get(position).getUserId())) {
            // Show confirmation dialog
            MaterialDialog mDialog = new MaterialDialog.Builder((Activity) context)
                    .setTitle("Delete?")
                    .setMessage("Are you sure want to delete this comment? This action cannot be undone.")
                    .setAnimation(R.raw.lottie_delete)
                    .setCancelable(false)
                    .setPositiveButton("Delete", R.drawable.ic_delete, (dialogInterface, which) -> {
                        dialogInterface.dismiss();

                        // Delete message from the database
                        chatsReference.child(currentUserId).child(receiverUserId).child(chatsList.get(position).getChatId()).removeValue();
                        chatsReference.child(receiverUserId).child(currentUserId).child(chatsList.get(position).getChatId()).removeValue();

                        // Show success confirmation toast
                        MotionToast.Companion.darkToast(
                                (Activity) context,
                                "Delete",
                                "Message has been deleted",
                                MotionToastStyle.DELETE,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(context, R.font.helvetica_regular)
                        );
                    })
                    .setNegativeButton("Cancel", R.drawable.ic_cancel, (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();

            LottieAnimationView animationView = mDialog.getAnimationView();
            animationView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            animationView.setPadding(0, 64, 0, 0);

            mDialog.show();
        }
    }
}
