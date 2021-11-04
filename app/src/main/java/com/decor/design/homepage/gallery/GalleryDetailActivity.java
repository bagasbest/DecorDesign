package com.decor.design.homepage.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.decor.design.R;
import com.decor.design.databinding.ActivityGalleryDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GalleryDetailActivity extends AppCompatActivity {

    public static final String EXTRA_GALLERY_ID = "galleryId";
    public static final String EXTRA_GALLERY = "gallery";
    public static final String EXTRA_ROLE = "role";
    private ActivityGalleryDetailBinding binding;
    private final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private SharedPreferences sharedPreferences;
    private boolean isLike = false;
    private int likes = 0;
    private String galleryId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GalleryModel model = getIntent().getParcelableExtra(EXTRA_GALLERY);
        galleryId = getIntent().getStringExtra(EXTRA_GALLERY_ID);

        sharedPreferences = this.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE);
        boolean wasLike = sharedPreferences.getBoolean(myUid+galleryId, false);
        likes = sharedPreferences.getInt(galleryId, 0);

        /// set role
        getRole();

        /// set gallery
        Glide.with(this)
                .load(model.getImage())
                .into(binding.roundedImageView);

        binding.textView5.setText(likes + " Likes");
        binding.date.setText(model.getDate());
        binding.caption.setText(model.getCaption());



        /// kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        /// cek apakah sebelumnya postingan ini saya sukai atau tidak
        if (wasLike) {
            binding.like.setImageResource(R.drawable.ic_baseline_favorite_24);
            isLike = true;
        } else {
            binding.like.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            isLike = false;
        }
        /// like / unlike post
        binding.like.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isLike) {
                    likes--;
                    binding.like.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    binding.textView5.setText((likes)+ " Likes");
                    editor.putBoolean(myUid + galleryId, false);
                    editor.putInt(galleryId, likes);
                    isLike = false;

                } else {
                    likes++;
                    binding.like.setImageResource(R.drawable.ic_baseline_favorite_24);
                    binding.textView5.setText((likes) + " Likes");
                    editor.putBoolean(myUid + galleryId, true);
                    editor.putInt(galleryId, likes);
                    isLike = true;

                }
                editor.apply();
            }
        });

        /// delete gallery
        binding.deleteGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(GalleryDetailActivity.this)
                        .setTitle("Confirm Delete Gallery")
                        .setMessage("Are you sure want to delete this gallery ?")
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setPositiveButton("YES", (dialogInterface, i) -> {
                            /// operation delete gallery by gallery id
                            FirebaseFirestore
                                    .getInstance()
                                    .collection("gallery")
                                    .document(galleryId)
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(GalleryDetailActivity.this, "Successfully delete this gallery", Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                            } else {
                                                Toast.makeText(GalleryDetailActivity.this, "Failure delete this gallery", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        })
                        .setNegativeButton("NO", (dialog, i) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });
    }

    private void getRole() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(("" + documentSnapshot.get("role")).equals("admin")) {
                            binding.deleteGallery.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}