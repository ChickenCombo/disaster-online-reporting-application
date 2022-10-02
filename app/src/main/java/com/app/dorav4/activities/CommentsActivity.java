package com.app.dorav4.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.RenderMode;
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
import java.util.TimeZone;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class CommentsActivity extends AppCompatActivity {
    ImageView ivReportProfile, ivReportPicture, ivReportUpvote, ivSubmitComment, ivBack;
    TextView tvToolbarHeader, tvReportName, tvReportDate, tvReportDisasterType, tvReportAddress, tvReportDescription, tvReportUpvoteCount, tvReportCommentCount, tvEmptyViewHeader, tvEmptyViewSubHeader;
    EditText etComment;
    Intent intent;
    RecyclerView recyclerView;

    List<Comments> commentsList;
    CommentsAdapters commentsAdapters;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss");

    DatabaseReference upvotesReference, reportsReference;

    String date, description, disasterType, fullName, profilePicture, reportPicture, reportId, upvotes, address, comments;

    boolean processComment = false;
    boolean processUpvote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        ivReportProfile = findViewById(R.id.ivReportProfile);
        ivReportPicture = findViewById(R.id.ivReportPicture);
        ivReportUpvote = findViewById(R.id.ivReportUpvote);
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

        tvToolbarHeader.setText(R.string.comments_header);

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
            MotionToast.Companion.darkToast(
                    this,
                    "Error",
                    "Comment cannot be empty",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
            );
        } else {
            // Progress Dialog
            MaterialDialog pDialog = new MaterialDialog.Builder(this)
                    .setTitle("Loading")
                    .setMessage("Adding your comment, please wait")
                    .setAnimation(R.raw.lottie_loading)
                    .setCancelable(false)
                    .build();

            LottieAnimationView animationView = pDialog.getAnimationView();
            animationView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            animationView.setRenderMode(RenderMode.SOFTWARE);
            animationView.setPadding(0, 64, 0, 0);

            pDialog.show();

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
                pDialog.dismiss();
                MotionToast.Companion.darkToast(
                        this,
                        "Success",
                        "Comment has been submitted",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                );
                etComment.getText().clear();
                commentCount();
            }).addOnFailureListener(e -> {
                pDialog.dismiss();
                MotionToast.Companion.darkToast(
                        this,
                        "Error",
                        "Comment submission failed, please try again",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                );
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