package com.decor.design.homepage.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.decor.design.R;
import com.decor.design.databinding.ActivityGalleryAddBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GalleryAddActivity extends AppCompatActivity {

    private ActivityGalleryAddBinding binding;
    private String dp;
    private static final int REQUEST_FROM_GALLERY = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// kembali
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        // upload new gallery
        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadNewGallery();
            }
        });

        // upload image gallery
        binding.hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(GalleryAddActivity.this)
                        .galleryOnly()
                        .compress(1024)
                        .start(REQUEST_FROM_GALLERY);
            }
        });

    }


    /// ini fungsi yang bekerja ketika tombol upload di klik, sistem akan melakukan storing data inputan ke database
    private void uploadNewGallery() {

        String caption = binding.caption.getText().toString().trim();

        /// ini merpakan validasi kolom inputan, semua kolom wajib diisi
        if (caption.isEmpty()) {
            Toast.makeText(GalleryAddActivity.this, "Caption must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (dp == null) {
            Toast.makeText(GalleryAddActivity.this, "Image Gallery must be uploaded", Toast.LENGTH_SHORT).show();
            return;
        }


        binding.progressBar.setVisibility(View.VISIBLE);
        String uid = String.valueOf(System.currentTimeMillis());
        String adminId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        /// get date now: yyyy-mm-dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = sdf.format(new Date(Long.parseLong(uid)));


        // SIMPAN DATA GALLERY KAMERA KE DATABASE
        Map<String, Object> gallery = new HashMap<>();
        gallery.put("caption", caption);
        gallery.put("image", dp);
        gallery.put("date", date);
        gallery.put("galleryId", uid);
        gallery.put("like", "0");
        gallery.put("adminId", adminId);


        FirebaseFirestore
                .getInstance()
                .collection("gallery")
                .document(uid)
                .set(gallery)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
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

    /// tampilkan dialog box ketika gagal mengupload
    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Failure to upload new gallery")
                .setMessage("There something wrong in your connection, please try again later")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    /// tampilkan dialog box ketika sukses mengupload
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success to upload new gallery")
                .setMessage("Your gallery will be arise soon, please check gallery navigation for update")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    /// fungsi untuk memvalidasi kode berdasarkan inisiasi variabel di atas tadi
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_FROM_GALLERY) {
                uploadArticleDp(data.getData());
            }
        }
    }


    /// fungsi untuk mengupload foto kedalam cloud storage
    private void uploadArticleDp(Uri data) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Please wait until process finish...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        String imageFileName = "gallery/image_" + System.currentTimeMillis() + ".png";

        mStorageRef.child(imageFileName).putFile(data)
                .addOnSuccessListener(taskSnapshot ->
                        mStorageRef.child(imageFileName).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    mProgressDialog.dismiss();
                                    dp = uri.toString();
                                    binding.hint.setVisibility(View.GONE);
                                    Glide
                                            .with(this)
                                            .load(dp)
                                            .into(binding.image);
                                })
                                .addOnFailureListener(e -> {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(GalleryAddActivity.this, "Failure to upload image", Toast.LENGTH_SHORT).show();
                                    Log.d("imageDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(GalleryAddActivity.this, "Failure to upload image", Toast.LENGTH_SHORT).show();
                    Log.d("imageDp: ", e.toString());
                });
    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}