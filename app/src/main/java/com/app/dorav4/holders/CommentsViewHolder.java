package com.app.dorav4.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;

public class CommentsViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivCommentProfile, ivMore;
    public TextView tvCommentName, tvCommentComment, tvCommentDate;

    public CommentsViewHolder(@NonNull View itemView) {
        super(itemView);

        ivMore = itemView.findViewById(R.id.ivMore);
        ivCommentProfile = itemView.findViewById(R.id.ivCommentProfile);
        tvCommentName = itemView.findViewById(R.id.tvCommentName);
        tvCommentComment = itemView.findViewById(R.id.tvCommentComment);
        tvCommentDate = itemView.findViewById(R.id.tvCommentDate);
    }
}
