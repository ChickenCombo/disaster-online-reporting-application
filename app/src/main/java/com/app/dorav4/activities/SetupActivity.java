package com.app.dorav4.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.RenderMode;
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
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SetupActivity extends AppCompatActivity {
    TextInputLayout tilFullName;
    TextInputEditText etFullName;
    Button btnSave;
    CircleImageView ivProfilePicture;
    Uri photoUri;

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

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures");

        // Set default profile picture
        photoUri = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.img_default_avatar)
                + '/' + getResources().getResourceTypeName(R.drawable.img_default_avatar) + '/' + getResources().getResourceEntryName(R.drawable.img_default_avatar)
        );
        ivProfilePicture.setImageURI(photoUri);

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
            MotionToast.Companion.darkToast(
                    this,
                    "Error",
                    "Please upload a profile picture",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
            );
        }

        // Save profile to Firebase Database and Storage
        if (isFullNameEmpty && isImageEmpty) {
            // Progress Dialog
            MaterialDialog pDialog = new MaterialDialog.Builder(this)
                    .setTitle("Loading")
                    .setMessage("Saving your profile")
                    .setAnimation(R.raw.lottie_loading)
                    .setCancelable(false)
                    .build();

            LottieAnimationView animationView = pDialog.getAnimationView();
            animationView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            animationView.setRenderMode(RenderMode.SOFTWARE);
            animationView.setPadding(0, 64, 0, 0);

            pDialog.show();

            // Save image to storage
            storageReference.child(mUser.getUid()).putFile(photoUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Get Image URI
                    storageReference.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(uri -> {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("userId", mUser.getUid());
                        hashMap.put("fullName", fullName);
                        hashMap.put("profilePicture", uri.toString());

                        // Save user details to database
                        usersReference.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(o -> {
                            pDialog.dismiss();

                            MotionToast.Companion.darkToast(
                                    this,
                                    "Success",
                                    "Setup complete, please verify your email address",
                                    MotionToastStyle.SUCCESS,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
                            );

                            mUser.sendEmailVerification();
                            finish();
                        }).addOnFailureListener(e -> {
                            pDialog.dismiss();

                            MotionToast.Companion.darkToast(
                                    this,
                                    "Error",
                                    "Profile setup failed",
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
                            );
                        });
                    });
                } else {
                    pDialog.dismiss();

                    MotionToast.Companion.darkToast(
                            this,
                            "Error",
                            "Profile setup failed",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );
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

                    public void invoke(@NotNull Intent it) {
                        Intrinsics.checkNotNullParameter(it, "it");
                        launcher.launch(it);
                    }
                });
    }
}