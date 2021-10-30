package com.decor.design.homepage.gallery;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.decor.design.databinding.FragmentGalleryBinding;
import com.decor.design.homepage.post.upload_post.PostAddActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private FirebaseUser user;
    private GalleryAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        initRecylerView();
        initViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();

        /// check role if role == designer, then show icon add +
        checkRole();

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

    }

    private void checkRole() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(("" + documentSnapshot.get("role")).equals("admin")) {
                            binding.fabAdd.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }


    // FUNGSI UNTUK MENAMPILKAN LIST DATA gallery
    private void initRecylerView() {
        binding.rvGallery.setLayoutManager(new LinearLayoutManager(getContext()));
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

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}