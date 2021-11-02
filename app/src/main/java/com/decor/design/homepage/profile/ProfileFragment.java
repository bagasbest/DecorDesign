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
import com.decor.design.homepage.search.show_all_post_designer.ShowAllPostActivity;
import com.decor.design.login.LoginActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseUser user;
    private String dp;
    private static final int REQUEST_FROM_GALLERY = 1001;
    private String role;
    private ProfileModel model = new ProfileModel();

    @Override
    public void onResume() {
        super.onResume();
        //check role untuk memeriksa apakah user role = admin, customer, atau designer
        checkRole();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /// update profile
        binding.imageHint.setOnClickListener(new View.OnClickListener() {
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
                startActivity(intent);
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
                        .setPositiveButton("YES", (dialogInterface, i) -> {
                            // sign out dari firebase autentikasi
                            FirebaseAuth.getInstance().signOut();

                            // go to login activity
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            dialogInterface.dismiss();
                            startActivity(intent);
                            getActivity().finish();
                        })
                        .setNegativeButton("NO", (dialog, i) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        /// show all designer post
        binding.showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShowAllPostActivity.class);
                intent.putExtra(ShowAllPostActivity.UID, user.getUid());
                startActivity(intent);
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
                        String dp = "" + documentSnapshot.get("dp");
                        if(!dp.equals("")) {
                            Glide.with(requireContext())
                                    .load(dp)
                                    .into(binding.dp);
                        }

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
                        if (role.equals("customer") || role.equals("admin")) {
                            /// fetch user data
                            binding.fullName.setText(model.getName());
                            binding.username.setText(model.getUsername());
                            binding.gender.setText("" + documentSnapshot.get("gender"));
                            binding.dob.setText("" + documentSnapshot.get("dob"));
                            binding.phone.setText(model.getPhone());
                            binding.email.setText("" + documentSnapshot.get("email"));
                        } else {
                            binding.designerForm.setVisibility(View.VISIBLE);
                            binding.textInputLayout6.setVisibility(View.VISIBLE);
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

    /// fungsi untuk memvalidasi kode berdasarkan inisiasi variabel upload gambar dari gallery
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
                                    updateUserDp();
                                    if(role.equals("designer")) {
                                        setDpForDesignerPost();
                                        setDpForChat("designerId", "designerDp");
                                    } else {
                                        setDpForChat("customerId", "customerDp");
                                    }
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

    private void setDpForChat(String roleId, String roleDp) {
        FirebaseFirestore
                .getInstance()
                .collection("chat")
                .whereEqualTo(roleId, user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot document : task.getResult()) {
                            String chatId = "" + document.get("chatId");
                            FirebaseFirestore
                                    .getInstance()
                                    .collection("chat")
                                    .document(chatId)
                                    .update(roleDp, dp);
                        }

                    }
                });
    }

    private void updateUserDp() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .update("dp", dp);
    }

    /// fungsi untuk mengupdate avatar pada designer post
    private void setDpForDesignerPost() {
        FirebaseFirestore
                .getInstance()
                .collection("post")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot document : task.getResult()) {
                            String postId = "" + document.get("postId");
                            FirebaseFirestore
                                    .getInstance()
                                    .collection("post")
                                    .document(postId)
                                    .update("dp", dp);
                        }
                    }
                });
    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}