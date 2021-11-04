package com.decor.design.homepage.gallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.decor.design.databinding.FragmentGalleryBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private GalleryAdapter adapter;
    private String dp, name, background, username;
    private static final int REQUEST_FROM_GALLERY = 1001;
    /// admin uid
    private static final String adminId = "1M9MXPosKyMJANH4tH7TiDxkA6E2";

    @Override
    public void onResume() {
        super.onResume();
        /// check role if role == designer, then show icon add +
        checkRole();
        initRecylerView();
        initViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGalleryBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), GalleryAddActivity.class));
            }
        });

        /// update profile
        binding.imageHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(GalleryFragment.this)
                        .galleryOnly()
                        .compress(1024)
                        .start(REQUEST_FROM_GALLERY);
            }
        });

        /// edit profile admin
        binding.editGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GalleryEditProfileAdminActivity.class);
                intent.putExtra(GalleryEditProfileAdminActivity.EXTRA_NAME, name);
                intent.putExtra(GalleryEditProfileAdminActivity.EXTRA_BACKGROUND, background);
                intent.putExtra(GalleryEditProfileAdminActivity.EXTRA_USERNAME, username);
                startActivity(intent);
            }
        });

    }

    private void checkRole() {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(adminId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(adminId.equals(myUid)) {
                            binding.fabAdd.setVisibility(View.VISIBLE);
                            binding.imageHint.setVisibility(View.VISIBLE);
                            binding.editGallery.setVisibility(View.VISIBLE);
                        }

                        String dp = "" + documentSnapshot.get("dp");
                        name = "" + documentSnapshot.get("name");
                        background = "" + documentSnapshot.get("background");
                        username = "" + documentSnapshot.get("username");

                        Glide.with(requireContext())
                                .load(dp)
                                .into(binding.dp);

                        binding.name.setText(name);
                        binding.textView7.setText(background);
                        binding.username.setText(username);
                    }
                });
    }


    // FUNGSI UNTUK MENAMPILKAN LIST DATA gallery
    private void initRecylerView() {
        binding.rvGallery.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        adapter = new GalleryAdapter();
        binding.rvGallery.setAdapter(adapter);
    }

    /// FUNGSI UNTUK MENDAPATKAN LIST DATA gallery DARI FIREBASE
    private void initViewModel() {
        GalleryViewModel viewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        /// AMBIL DATA gallery DARI DATABASE
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setListGallery();
        viewModel.getListGallery().observe(getViewLifecycleOwner(), gallery -> {
            if (gallery.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                adapter.setData(gallery);
            } else {
                binding.noData.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }

    /// fungsi untuk memvalidasi kode upload avatar dari gallery
    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        ProgressDialog mProgressDialog = new ProgressDialog(requireContext());

        mProgressDialog.setMessage("Please wait until process finish...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        String imageFileName = "admin/avatar_" + System.currentTimeMillis() + ".png";

        mStorageRef.child(imageFileName).putFile(data)
                .addOnSuccessListener(taskSnapshot ->
                        mStorageRef.child(imageFileName).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    mProgressDialog.dismiss();
                                    dp = uri.toString();
                                    Glide
                                            .with(this)
                                            .load(dp)
                                            .into(binding.dp);
                                    updateUserDp();
                                })
                                .addOnFailureListener(e -> {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Failure to upload image", Toast.LENGTH_SHORT).show();
                                    Log.d("imageDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), "Failure to upload image", Toast.LENGTH_SHORT).show();
                    Log.d("imageDp: ", e.toString());
                });
    }

    private void updateUserDp() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(adminId)
                .update("dp", dp);
    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}