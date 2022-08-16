package com.app.dorav4.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.app.dorav4.R;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class SetupActivity extends AppCompatActivity {
    TextInputLayout tilFullName;
    TextInputEditText etFullName;
    Button btnSave;
    CircleImageView ivProfilePicture;
    Intent intent;
    Uri photoUri;

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference usersReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        tilFullName = findViewById(R.id.tilFullName);
        etFullName = findViewById(R.id.etFullName);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        btnSave = findViewById(R.id.btnSave);

        progressDialog = new ProgressDialog(SetupActivity.this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures");

        // ivProfilePicture OnClickListener
        ivProfilePicture.setOnClickListener(v -> chooseImage());

        // btnSave OnClickListener
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        boolean isFullNameEmpty = false, isImageEmpty = false;
        String fullName = Objects.requireNonNull(etFullName.getText()).toString().trim();

        // Validate full name
        if (fullName.isEmpty()) {
            tilFullName.setError("Full name cannot be empty!");
        } else {
            isFullNameEmpty = true;
            tilFullName.setError(null);
        }

        // Validate profile picture
        if (photoUri != null && !photoUri.equals(Uri.EMPTY)) {
            isImageEmpty = true;
        } else {
            Toast.makeText(SetupActivity.this, "Please upload a profile picture", Toast.LENGTH_SHORT).show();
        }

        // Save profile to Firebase Database and Storage
        if (isFullNameEmpty && isImageEmpty) {
            // ProgressDialog
            progressDialog.setTitle("Profile Setup");
            progressDialog.setMessage("Saving your profile");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Save image to storage
            storageReference.child(mUser.getUid()).putFile(photoUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Get Image URI
                    storageReference.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(uri -> {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("fullName", fullName);
                        hashMap.put("profilePicture", uri.toString());

                        // Save user details to database
                        usersReference.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(o -> {
                            progressDialog.dismiss();
                            Toast.makeText(SetupActivity.this, "Profile setup complete", Toast.LENGTH_SHORT).show();
                            intent = new Intent(SetupActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(SetupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        });
                    });
                } else {
                    Toast.makeText(SetupActivity.this, "Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Select image from gallery
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            photoUri = result.getData().getData();
            ivProfilePicture.setImageURI(photoUri);
        } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
            ImagePicker.Companion.getError(result.getData());
        }
    });

    // Choose image from gallery or camera
    private void chooseImage() {
        ImagePicker.Companion.with(this)
                .cropSquare()
                .provider(ImageProvider.BOTH)
                .createIntentFromDialog(new Function1(){
                    public Object invoke(Object var1) {
                        this.invoke((Intent) var1);
                        return Unit.INSTANCE;
                    }

                    public final void invoke(@NotNull Intent it) {
                        Intrinsics.checkNotNullParameter(it, "it");
                        launcher.launch(it);
                    }
                });
    }
}