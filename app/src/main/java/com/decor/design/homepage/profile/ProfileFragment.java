package com.decor.design.homepage.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.decor.design.R;
import com.decor.design.databinding.FragmentProfileBinding;
import com.decor.design.login.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseUser user;

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

                        /// fetch data from database by role
                        if(("" + documentSnapshot.get("role")).equals("admin")) {

                        } else if (("" + documentSnapshot.get("role")).equals("customer")) {
                            /// fetch user data
                            binding.fullName.setText("" + documentSnapshot.get("name"));
                            binding.username.setText("" + documentSnapshot.get("username"));
                            binding.gender.setText("" + documentSnapshot.get("gender"));
                            binding.dob.setText("" + documentSnapshot.get("dob"));
                            binding.phone.setText("" + documentSnapshot.get("phone"));
                            binding.email.setText("" + documentSnapshot.get("email"));
                        } else {

                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}