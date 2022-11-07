package com.app.dorav4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;
import com.app.dorav4.adapters.ConversationAdapter;
import com.app.dorav4.models.Chats;
import com.app.dorav4.utils.PushNotificationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ConversationActivity extends AppCompatActivity {
    TextView tvToolbarHeader;
    ImageView ivBack, ivSend, ivReceiverPicture;
    RecyclerView recyclerView;
    EditText etChat;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference usersReference, chatsReference, tokensReference;

    List<Chats> chatsList;
    ConversationAdapter conversationAdapter;

    String receiverUserId;
    String receiverFullName;
    String receiverProfilePicture;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        tvToolbarHeader = findViewById(R.id.tvToolbarHeader);
        etChat = findViewById(R.id.etChat);
        ivBack = findViewById(R.id.ivBack);
        ivSend = findViewById(R.id.ivSend);
        ivReceiverPicture = findViewById(R.id.ivReceiverPicture);
        recyclerView = findViewById(R.id.recyclerView);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // Get uid
        Intent intent = getIntent();
        receiverUserId = intent.getStringExtra("userId");
        currentUserId = mUser.getUid();

        usersReference = FirebaseDatabase.getInstance().getReference("Users");
        chatsReference = FirebaseDatabase.getInstance().getReference("Chats");
        tokensReference = FirebaseDatabase.getInstance().getReference("Tokens");

        // Setup recyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ConversationActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Fetch receiver's information
        getUserInfo(receiverUserId);

        // ivSend OnClickListener
        ivSend.setOnClickListener(v -> sendMessage());

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());

        // Get messages
        getMessages();
    }

    // Get messages
    private void getMessages() {
        chatsList = new ArrayList<>();

        Query query = chatsReference.child(currentUserId).child(receiverUserId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chats chats = ds.getValue(Chats.class);
                    chatsList.add(chats);
                    conversationAdapter = new ConversationAdapter(ConversationActivity.this, chatsList, receiverProfilePicture, receiverUserId);
                    recyclerView.setAdapter(conversationAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Send message
    private void sendMessage() {
        String message = etChat.getText().toString();

        if (message.isEmpty()) {
            MotionToast.Companion.darkToast(
                    this,
                    "Error",
                    "Message cannot be empty",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
            );
        } else {
            String chatId = String.valueOf(System.currentTimeMillis());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("chatId", chatId);
            hashMap.put("message", message);
            hashMap.put("userId", currentUserId);

            chatsReference.child(receiverUserId).child(currentUserId).child(chatId).updateChildren(hashMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    chatsReference.child(currentUserId).child(receiverUserId).child(chatId).updateChildren(hashMap).addOnCompleteListener(task1 -> {
                       etChat.getText().clear();

                       // Send push notification
                       sendNotification(message);
                    });
                }
            });
        }
    }

    // Fetch receiver's information
    private void getUserInfo(String receiverUserId) {
        usersReference.orderByKey().equalTo(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // Fetch data from the database
                    receiverFullName = Objects.requireNonNull(ds.child("fullName").getValue()).toString();
                    receiverProfilePicture = Objects.requireNonNull(ds.child("profilePicture").getValue()).toString();

                    // Set data
                    tvToolbarHeader.setText(receiverFullName);
                    Picasso.get().load(receiverProfilePicture).into(ivReceiverPicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Send push notification
    private void sendNotification(String message) {
        tokensReference.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String token = Objects.requireNonNull(snapshot.child("token").getValue()).toString();
                    PushNotificationService.unicastNotification(ConversationActivity.this, token, MainActivity.fullName, message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}