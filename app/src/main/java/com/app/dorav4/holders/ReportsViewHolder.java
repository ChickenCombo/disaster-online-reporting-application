package com.app.dorav4.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportsViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView ivReportProfile;
    public TextView tvReportName, tvReportDate, tvReportDisasterType, tvReportAddress, tvReportDescription, tvReportCommentCount, tvReportUpvoteCount;
    public ImageView ivReportPicture, ivReportComment, ivReportUpvote, ivMore;

    public ReportsViewHolder(@NonNull View itemView) {
        super(itemView);

        ivReportProfile = itemView.findViewById(R.id.ivReportProfile);
        tvReportName = itemView.findViewById(R.id.tvReportName);
        tvReportDate = itemView.findViewById(R.id.tvReportDate);
        tvReportDisasterType = itemView.findViewById(R.id.tvReportDisasterType);
        tvReportAddress = itemView.findViewById(R.id.tvReportAddress);
        tvReportDescription = itemView.findViewById(R.id.tvReportDescription);
        tvReportCommentCount = itemView.findViewById(R.id.tvReportCommentCount);
        tvReportUpvoteCount = itemView.findViewById(R.id.tvReportUpvoteCount);
        ivReportPicture = itemView.findViewById(R.id.ivReportPicture);
        ivReportComment = itemView.findViewById(R.id.ivReportComment);
        ivReportUpvote = itemView.findViewById(R.id.ivReportUpvote);
        ivMore = itemView.findViewById(R.id.ivMore);
    }
}
