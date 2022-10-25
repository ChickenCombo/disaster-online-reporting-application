package com.app.dorav4.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.app.dorav4.R;
import com.app.dorav4.activities.ConversationActivity;
import com.app.dorav4.holders.UsersViewHolder;
import com.app.dorav4.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class MessagesAdapter extends RecyclerView.Adapter<UsersViewHolder> {
    Context context;
    List<Users> usersList;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    DatabaseReference chatsReference;

    public MessagesAdapter(Context context, List<Users> usersList) {
        this.context = context;
        this.usersList = usersList;

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        chatsReference = FirebaseDatabase.getInstance().getReference("Chats").child(mUser.getUid());
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
        String userId = usersList.get(position).getUserId();
        String fullName = usersList.get(position).getFullName();
        String profilePicture = usersList.get(position).getProfilePicture();

        // Set user's details
        holder.tvUserName.setText(fullName);
        Picasso.get().load(profilePicture).into(holder.ivUserProfile);

        // Holder OnClickListener
        holder.itemView.setOnClickListener(view -> {
            // Start conversation
            Intent intent = new Intent(context, ConversationActivity.class);
            intent.putExtra("userId", userId);
            context.startActivity(intent);
        });

        // Holder OnLongClickListener
        holder.itemView.setOnLongClickListener(v -> {
            deleteConversation(userId);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    // Delete conversation
    private void deleteConversation(String userId) {
        MaterialDialog mDialog = new MaterialDialog.Builder((Activity) context)
                .setTitle("Delete?")
                .setMessage("Are you sure want to delete this conversation? This action cannot be undone.")
                .setAnimation(R.raw.lottie_delete)
                .setCancelable(false)
                .setPositiveButton("Delete", R.drawable.ic_delete, (dialogInterface, which) -> {
                    dialogInterface.dismiss();

                    // Delete conversation
                    chatsReference.child(userId).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Show success toast
                            MotionToast.Companion.darkToast(
                                    (Activity) context,
                                    "Delete",
                                    "Message has been deleted",
                                    MotionToastStyle.DELETE,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(context, R.font.helvetica_regular)
                            );
                        } else {
                            // Show failed toast
                            MotionToast.Companion.darkToast(
                                    (Activity) context,
                                    "Error",
                                    "Failed to delete message, please try again",
                                    MotionToastStyle.DELETE,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(context, R.font.helvetica_regular)
                            );
                        }
                    });
                })
                .setNegativeButton("Cancel", R.drawable.ic_cancel, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        LottieAnimationView animationView = mDialog.getAnimationView();
        animationView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        animationView.setPadding(0, 64, 0, 0);

        mDialog.show();
    }
}
