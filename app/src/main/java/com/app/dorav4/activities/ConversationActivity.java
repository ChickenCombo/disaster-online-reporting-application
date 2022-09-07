package com.app.dorav4.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dorav4.R;

public class ConversationActivity extends AppCompatActivity {
    TextView tvToolbarHeader;
    ImageView ivBack, ivSend;
    RecyclerView recyclerView;
    EditText etChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Intent intent = getIntent();
        String uid = intent.getStringExtra("userId");

        tvToolbarHeader = findViewById(R.id.tvToolbarHeader);
        etChat = findViewById(R.id.etChat);
        ivBack = findViewById(R.id.ivBack);
        ivSend = findViewById(R.id.ivSend);
        recyclerView = findViewById(R.id.recyclerView);

        // ivSend OnClickListener
        ivSend.setOnClickListener(v -> Toast.makeText(this, uid, Toast.LENGTH_SHORT).show());

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }
}