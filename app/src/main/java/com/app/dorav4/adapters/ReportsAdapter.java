package com.app.dorav4.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;
import com.app.dorav4.activities.ImageFullscreenActivity;
import com.app.dorav4.activities.CommentsActivity;
import com.app.dorav4.activities.UpvotesActivity;
import com.app.dorav4.holders.ReportsViewHolder;
import com.app.dorav4.models.Reports;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsViewHolder> {
    Context context;
    List<Reports> reportsList;

    ProgressDialog progressDialog;

    Dialog dialog;

    DatabaseReference reportsReference;

    String currentUserId;

    boolean processUpvote = false;

    public ReportsAdapter(Context context, List<Reports> reportsList) {
        this.context = context;
        this.reportsList = reportsList;

        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        reportsReference = FirebaseDatabase.getInstance().getReference().child("Reports");
    }

    @NonNull
    @Override
    public ReportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_view_reports, parent, false) ;
        return new ReportsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsViewHolder holder, int position) {
        // Fetch value from the list
        String reportId = reportsList.get(position).getReportId();
        String date = reportsList.get(position).getDate();
        String description = reportsList.get(position).getDescription();
        String disasterType = reportsList.get(position).getDisasterType();
        String fullName = reportsList.get(position).getFullName();
        String userId = reportsList.get(position).getUserId();
        String profilePicture = reportsList.get(position).getProfilePicture();
        String reportPicture = reportsList.get(position).getReportPicture();
        String upvotes = reportsList.get(position).getUpvotes();
        String comments = reportsList.get(position).getComments();
        String address = reportsList.get(position).getAddress();

        // Set corresponding values to the layout
        holder.tvReportDate.setText(calculateTime(date));
        holder.tvReportDescription.setText(description);
        holder.tvReportDisasterType.setText(disasterType);
        holder.tvReportName.setText(fullName);
        holder.tvReportUpvoteCount.setText(String.format("%s upvotes", upvotes));
        holder.tvReportCommentCount.setText(String.format("%s comments", comments));
        holder.tvReportAddress.setText(address);
        Picasso.get().load(profilePicture).into(holder.ivReportProfile);
        Picasso.get().load(reportPicture).into(holder.ivReportPicture);

        // Hide ivMore if current user's id doesn't match the report's user id
        if (!userId.equals(currentUserId)) {
            holder.ivMore.setVisibility(View.INVISIBLE);
        }

        // Set upvote button color
        setUpvoteButton(holder, reportId);

        // ivMore OnClickListener
        holder.ivMore.setOnClickListener(v -> showReportOption(holder.ivMore, reportId, reportPicture));

        // ivReportUpvote OnClickListener
        holder.ivReportUpvote.setOnClickListener(v -> upvoteReport(reportId, position));

        // ivReportComment OnClickListener
        holder.ivReportComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra("reportId", reportId);
            context.startActivity(intent);
        });

        // tvReportCommentCount OnClickListener
        holder.tvReportCommentCount.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra("reportId", reportId);
            context.startActivity(intent);
        });

        // ivReportPicture OnClickListener
        holder.ivReportPicture.setOnClickListener(v -> {
            // Pass image uri to the next intent
            Intent intent = new Intent(context, ImageFullscreenActivity.class);
            intent.putExtra("reportPicture", reportPicture);
            context.startActivity(intent);
        });

        // tvReportUpvoteCount OnClickListener
        holder.tvReportUpvoteCount.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpvotesActivity.class);
            intent.putExtra("reportId", reportId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reportsList.size();
    }

    // Show more report options
    private void showReportOption(ImageView ivMore, String reportId, String reportPicture) {
        PopupMenu popupMenu = new PopupMenu(context, ivMore, Gravity.END);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");

        // Menu OnClickListener
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            // Delete is clicked
            if (id == 0) {
                // Initialize custom dialog
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.alert_dialog);
                dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_alert_dialog, null));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;

                // Set dialog details
                TextView alertTitle = dialog.findViewById(R.id.tvAlertTitle);
                TextView alertDescription = dialog.findViewById(R.id.tvAlertDescription);
                ImageView alertImage = dialog.findViewById(R.id.ivAlertImage);
                alertTitle.setText("Delete Report?");
                alertDescription.setText("Are you sure you want to delete your report? Deleted reports cannot be recovered.");
                alertImage.setImageResource(R.drawable.img_delete);

                // Dialog confirmation button
                Button confirm = dialog.findViewById(R.id.btnConfirm);
                confirm.setText("Delete");
                confirm.setOnClickListener(v -> {
                    dialog.dismiss();
                    deleteReport(reportId, reportPicture);
                });

                // Dialog cancel button
                Button cancel = dialog.findViewById(R.id.btnCancel);
                cancel.setOnClickListener(v -> dialog.dismiss());

                dialog.show();
            }
            return false;
        });
        popupMenu.show();
    }

    // Remove report from the database
    private void deleteReport(String reportId, String reportPicture) {
        // ProgressDialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Delete");
        progressDialog.setMessage("Deleting your disaster report");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Delete image from storage
        StorageReference reportPictureReference = FirebaseStorage.getInstance().getReferenceFromUrl(reportPicture);
        reportPictureReference.delete().addOnSuccessListener(unused -> {
            // Query that selects the matching reportId
            reportsReference.orderByChild("reportId").equalTo(reportId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Delete report from database
                    for (DataSnapshot data: snapshot.getChildren()) {
                        data.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            progressDialog.dismiss();
            Toast.makeText(context, "Report successfully deleted", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Report deletion failed
            progressDialog.dismiss();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Add and remove upvote from a report
    private void upvoteReport(String reportId, int position) {
        int reportUpvotes = Integer.parseInt(reportsList.get(position).getUpvotes());
        processUpvote = true;

        // Add or subtract from upvote count
        DatabaseReference upvotesReference = FirebaseDatabase.getInstance().getReference("Reports").child(reportId).child("Upvotes");

        upvotesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(reportId).hasChild(currentUserId)) {
                    // Remove upvote
                    reportsReference.child(reportId).child("upvotes").setValue("" + (reportUpvotes - 1));
                    upvotesReference.child(reportId).child(currentUserId).removeValue();
                } else {
                    // Upvote report
                    reportsReference.child(reportId).child("upvotes").setValue("" + (reportUpvotes + 1));
                    upvotesReference.child(reportId).child(currentUserId).setValue("Upvoted");
                }
                processUpvote = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // Set upvote button to green if user upvoted or has already upvoted
    private void setUpvoteButton(ReportsViewHolder holder, String reportId) {
        DatabaseReference upvotesReference = FirebaseDatabase.getInstance().getReference("Reports").child(reportId).child("Upvotes");

        upvotesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(reportId).hasChild(currentUserId)) {
                    // Current user upvoted the report
                    holder.ivReportUpvote.setImageResource(R.drawable.ic_upvoted);
                } else {
                    holder.ivReportUpvote.setImageResource(R.drawable.ic_upvote);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // TODO: Fix bug with timezone
    // Convert time into "time ago"
    private String calculateTime (String strDate) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        try {
            Date date = format.parse(strDate);
            assert date != null;
            return (String) DateUtils.getRelativeTimeSpanString(date.getTime(), Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
        } catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }
}
