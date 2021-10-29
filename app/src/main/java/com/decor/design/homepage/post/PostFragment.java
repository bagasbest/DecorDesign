package com.decor.design.homepage.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.decor.design.databinding.FragmentPostBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostFragment extends Fragment {

    private FragmentPostBinding binding;
    private PostAdapter adapter;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPostBinding.inflate(inflater, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();

        initRecylerView();
        initViewModel();

        /// check role if role == designer, then show icon add +
        checkRole();

        return binding.getRoot();
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
                        if(("" + documentSnapshot.get("role")).equals("designer")) {
                            binding.fabAdd.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    // FUNGSI UNTUK MENAMPILKAN LIST DATA POST
    private void initRecylerView() {
        binding.postRv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter();
        binding.postRv.setAdapter(adapter);
    }

    /// FUNGSI UNTUK MENDAPATKAN LIST DATA POST DARI FIREBASE
    private void initViewModel() {
        PostViewModel viewModel = new ViewModelProvider(this).get(PostViewModel.class);

        /// AMBIL DATA POST DARI DATABASE
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setListPost();
        viewModel.getPostList().observe(getViewLifecycleOwner(), post -> {
            if (post.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                adapter.setData(post);
            } else {
                binding.noData.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}