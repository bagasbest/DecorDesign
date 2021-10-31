package com.decor.design.homepage.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.decor.design.R;
import com.decor.design.databinding.FragmentProfileBinding;
import com.decor.design.login.LoginActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseUser user;
    private String dp;
    private static final int REQUEST_FROM_GALLERY = 1001;
    private String role;
    private ProfileModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        //check role untuk memeriksa apakah user role = admin, customer, atau designer
        checkRole();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /// update profile
        binding.dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ProfileFragment.this)
                        .galleryOnly()
                        .compress(1024)
                        .start(REQUEST_FROM_GALLERY);
            }
        });


        /// edit profile
        binding.view8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                intent.putExtra(ProfileEditActivity.ROLE, role);
                intent.putExtra(ProfileEditActivity.EXTRA_PROFILE, model);
            }
        });


        /// logout
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Logout")
                        .setMessage("Are you sure want to logout ?")
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setPositiveButton("YA", (dialogInterface, i) -> {
                            // sign out dari firebase autentikasi
                            FirebaseAuth.getInstance().signOut();

                            // go to login activity
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            dialogInterface.dismiss();
                            startActivity(intent);
                            getActivity().finish();
                        })
                        .setNegativeButton("TIDAK", (dialog, i) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });
    }

    private void checkRole() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        role = "" + documentSnapshot.get("role");

                        ProfileModel model = new ProfileModel();
                        model.setName("" + documentSnapshot.get("name"));
                        model.setUsername("" + documentSnapshot.get("username"));
                        model.setBackground("" + documentSnapshot.get("background"));
                        model.setHobby("" + documentSnapshot.get("hobby"));
                        model.setOrganization("" + documentSnapshot.get("organization"));
                        model.setPhone("" + documentSnapshot.get("phone"));
                        model.setSoftSkill("" + documentSnapshot.get("softSkill"));
                        model.setSkill("" + documentSnapshot.get("skill"));
                        model.setWork("" + documentSnapshot.get("work"));
                        model.setEducation("" + documentSnapshot.get("education"));



                        /// fetch data from database by role
                        if(role.equals("admin")) {

                        } else if (role.equals("customer")) {
                            /// fetch user data
                            binding.fullName.setText(model.getName());
                            binding.username.setText(model.getUsername());
                            binding.gender.setText("" + documentSnapshot.get("gender"));
                            binding.dob.setText("" + documentSnapshot.get("dob"));
                            binding.phone.setText(model.getPhone());
                            binding.email.setText("" + documentSnapshot.get("email"));
                        } else {
                            binding.designerForm.setVisibility(View.VISIBLE);
                            binding.fullName.setText(model.getName());
                            binding.username.setText(model.getUsername());
                            binding.background.setText(model.getBackground());
                            binding.education.setText(model.getEducation());
                            binding.hobby.setText(model.getHobby());
                            binding.organization.setText(model.getOrganization());
                            binding.phone.setText(model.getPhone());
                            binding.skill.setText(model.getSkill());
                            binding.softSkill.setText(model.getSoftSkill());
                            binding.work.setText(model.getWork());

                        }
                    }
                });
    }

    /// fungsi untuk memvalidasi kode berdasarkan inisiasi variabel di atas tadi
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
        String imageFileName = "users/avatar_" + System.currentTimeMillis() + ".png";

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

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}