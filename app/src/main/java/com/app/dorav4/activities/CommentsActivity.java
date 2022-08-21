package com.app.dorav4.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dorav4.R;
import com.app.dorav4.adapters.CommentsAdapters;
import com.app.dorav4.models.Comments;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;

public class CommentsActivity extends AppCompatActivity {
    ImageView ivReportProfile, ivReportPicture, ivReportUpvote, ivCommentProfile, ivSubmitComment, ivBack;
    TextView tvToolbarHeader, tvReportName, tvReportDate, tvReportDisasterType, tvReportAddress, tvReportDescription, tvReportUpvoteCount, tvReportCommentCount, tvEmptyViewHeader, tvEmptyViewSubHeader;
    EditText etComment;
    Intent intent;
    RecyclerView recyclerView;

    List<Comments> commentsList;
    CommentsAdapters commentsAdapters;

    ProgressDialog progressDialog;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss");

    DatabaseReference upvotesReference, reportsReference;

    String date, description, disasterType, fullName, profilePicture, reportPicture, userId, reportId, upvotes, latitude, longitude, address, comments;

    boolean processComment = false;
    boolean processUpvote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        ivReportProfile = findViewById(R.id.ivReportProfile);
        ivReportPicture = findViewById(R.id.ivReportPicture);
        ivReportUpvote = findViewById(R.id.ivReportUpvote);
        ivCommentProfile = findViewById(R.id.ivCommentProfile);
        ivSubmitComment = findViewById(R.id.ivSubmitComment);
        ivBack = findViewById(R.id.ivBack);
        tvToolbarHeader = findViewById(R.id.tvToolbarHeader);
        tvReportName = findViewById(R.id.tvReportName);
        tvReportDate = findViewById(R.id.tvReportDate);
        tvReportDisasterType = findViewById(R.id.tvReportDisasterType);
        tvReportAddress = findViewById(R.id.tvReportAddress);
        tvReportDescription = findViewById(R.id.tvReportDescription);
        tvReportUpvoteCount = findViewById(R.id.tvReportUpvoteCount);
        tvReportCommentCount = findViewById(R.id.tvReportCommentCount);
        tvEmptyViewHeader = findViewById(R.id.tvEmptyViewHeader);
        tvEmptyViewSubHeader = findViewById(R.id.tvEmptyViewSubHeader);
        etComment = findViewById(R.id.etComment);
        recyclerView = findViewById(R.id.recyclerView);

        tvToolbarHeader.setText("Comments");

        progressDialog = new ProgressDialog(CommentsActivity.this);

        reportsReference = FirebaseDatabase.getInstance().getReference().child("Reports");

        intent = getIntent();
        reportId = intent.getStringExtra("reportId");

        // ivBack OnCLickListener
        ivBack.setOnClickListener(v -> finish());

        // ivSubmitComment OnClickListener
        ivSubmitComment.setOnClickListener(v -> submitComment());

        // ivReportUpvote OnClickListener
        ivReportUpvote.setOnClickListener(v -> upvoteReport());

        // ivReportPicture OnClickListener
        ivReportPicture.setOnClickListener(v -> {
            // Pass image uri to the next intent
            Intent intent = new Intent(CommentsActivity.this, ImageFullscreenActivity.class);
            intent.putExtra("reportPicture", reportPicture);
            startActivity(intent);
        });

        // tvReportUpvoteCount OnClickListener
        tvReportUpvoteCount.setOnClickListener(v -> {
            Intent intent = new Intent(CommentsActivity.this, UpvotesActivity.class);
            intent.putExtra("reportId", reportId);
            startActivity(intent);
        });

        loadReportDetails();
        loadComments();
        setUpvoteButton();
    }

    // Load the post information
    private void loadReportDetails() {
        // Query the report
        Query query = reportsReference.orderByChild("reportId").equalTo(reportId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // Fetch value from the database
                    date = (String) ds.child("date").getValue();
                    description = (String) ds.child("description").getValue();
                    disasterType = (String) ds.child("disasterType").getValue();
                    fullName = (String) ds.child("fullName").getValue();
                    profilePicture = (String) ds.child("profilePicture").getValue();
                    reportPicture = (String) ds.child("reportPicture").getValue();
                    upvotes = (String) ds.child("upvotes").getValue();
                    address = (String) ds.child("address").getValue();
                    comments = (String) ds.child("comments").getValue();
                    reportId = (String) ds.child("reportId").getValue();

                    // Set the data
                    tvReportDate.setText(calculateTime(date));
                    tvReportDescription.setText(description);
                    tvReportDisasterType.setText(disasterType);
                    tvReportName.setText(fullName);
                    tvReportUpvoteCount.setText(String.format("%s upvotes", upvotes));
                    tvReportAddress.setText(address);
                    tvReportCommentCount.setText(String.format("%s comments", comments));
                    Picasso.get().load(profilePicture).into(ivReportProfile);
                    Picasso.get().load(reportPicture).into(ivReportPicture);
                    Picasso.get().load(MainActivity.profilePicture).into(ivCommentProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Load comments
    private void loadComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        commentsList = new ArrayList<>();

        DatabaseReference commentsReference = FirebaseDatabase.getInstance().getReference("Reports").child(reportId).child("Comments");
        commentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();

                // Add database data inside the list
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Comments comments = ds.getValue(Comments.class);
                    commentsList.add(comments);

                    commentsAdapters = new CommentsAdapters(CommentsActivity.this, commentsList, MainActivity.userId, reportId);
                    recyclerView.setAdapter(commentsAdapters);
                }

                // Show emptyView if recyclerView is empty
                if (commentsList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    tvEmptyViewHeader.setVisibility(View.VISIBLE);
                    tvEmptyViewSubHeader.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvEmptyViewHeader.setVisibility(View.GONE);
                    tvEmptyViewSubHeader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Add a comment to a report
    private void submitComment() {
        String comment = etComment.getText().toString().trim();

        if (comment.isEmpty()) {
            Toasty.error(CommentsActivity.this, "Comment cannot be empty.", Toasty.LENGTH_SHORT, true).show();
        } else {
            // ProgressDialog
            progressDialog.setTitle("Comment");
            progressDialog.setMessage("Posting your comment");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Get date and time of comment
            Date date = new Date();
            String formattedDate = format.format(date);
            long epochDate = date.getTime();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("commentId", String.valueOf(epochDate));
            hashMap.put("comment", comment);
            hashMap.put("userId", MainActivity.userId);
            hashMap.put("name", MainActivity.fullName);
            hashMap.put("profilePicture", MainActivity.profilePicture);
            hashMap.put("date", formattedDate);

            // Upload comment to the database
            DatabaseReference commentsReference = FirebaseDatabase.getInstance().getReference("Reports").child(reportId).child("Comments");
            commentsReference.child(String.valueOf(epochDate)).setValue(hashMap).addOnSuccessListener(unused -> {
                progressDialog.dismiss();
                Toasty.success(CommentsActivity.this, "Comment has been submitted.", Toasty.LENGTH_SHORT, true).show();
                etComment.getText().clear();
                commentCount();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toasty.error(CommentsActivity.this, e.toString(), Toasty.LENGTH_SHORT, true).show();
            });
        }
    }

    // Add +1 to comment count for every new comment
    public void commentCount() {
        processComment = true;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Reports").child(reportId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (processComment) {
                    String comments = String.valueOf(snapshot.child("comments").getValue());
                    int commentAdded = Integer.parseInt(comments) + 1;
                    reference.child("comments").setValue(String.valueOf(commentAdded));
                    processComment = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Add and remove upvote from a report
    private void upvoteReport() {
        processUpvote = true;

        upvotesReference = FirebaseDatabase.getInstance().getReference("Reports").child(reportId).child("Upvotes");
        upvotesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(reportId).hasChild(MainActivity.userId)) {
                    // Remove upvote
                    reportsReference.child(reportId).child("upvotes").setValue("" + (Integer.parseInt(upvotes) - 1));
                    upvotesReference.child(reportId).child(MainActivity.userId).removeValue();
                    ivReportUpvote.setImageResource(R.drawable.ic_upvote);
                } else {
                    // Upvote report
                    reportsReference.child(reportId).child("upvotes").setValue("" + (Integer.parseInt(upvotes) + 1));
                    upvotesReference.child(reportId).child(MainActivity.userId).setValue("Upvoted");
                    ivReportUpvote.setImageResource(R.drawable.ic_upvoted);
                }
                processUpvote = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // Set upvote button to green if user upvoted or has already upvoted
    private void setUpvoteButton() {
        upvotesReference = FirebaseDatabase.getInstance().getReference("Reports").child(reportId).child("Upvotes");
        upvotesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(reportId).hasChild(MainActivity.userId)) {
                    // Current user upvoted the report
                    ivReportUpvote.setImageResource(R.drawable.ic_upvoted);
                } else {
                    ivReportUpvote.setImageResource(R.drawable.ic_upvote);
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