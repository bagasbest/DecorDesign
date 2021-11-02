package com.decor.design.homepage.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.decor.design.R;
import com.decor.design.databinding.ActivityGalleryEditProfileAdminBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GalleryEditProfileAdminActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_BACKGROUND = "background";
    public static final String EXTRA_USERNAME = "username";
    private ActivityGalleryEditProfileAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryEditProfileAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.name.setText(getIntent().getStringExtra(EXTRA_NAME));
        binding.background.setText(getIntent().getStringExtra(EXTRA_BACKGROUND));
        binding.username.setText(getIntent().getStringExtra(EXTRA_USERNAME));

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation();
            }
        });

    }

    private void formValidation() {
        String background = binding.background.getText().toString().trim();
        String name = binding.name.getText().toString().trim();
        String username = binding.username.getText().toString().trim();

        if (background.isEmpty()) {
            Toast.makeText(GalleryEditProfileAdminActivity.this, "Background must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (name.isEmpty()) {
            Toast.makeText(GalleryEditProfileAdminActivity.this, "Name must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (username.isEmpty()) {
            Toast.makeText(GalleryEditProfileAdminActivity.this, "Username must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> admin = new HashMap<>();
        admin.put("background", background);
        admin.put("name", name);
        admin.put("username", username);

        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .update(admin)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);
                            showSuccessDialog();
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            showFailureDialog();
                        }
                    }
                });

    }

    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Failure to update profile")
                .setMessage("There something wrong in your connection, please try again later")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success to update profile")
                .setMessage("Operation Success")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}