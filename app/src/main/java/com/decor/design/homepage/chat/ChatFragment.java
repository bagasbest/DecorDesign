package com.decor.design.homepage.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.decor.design.databinding.FragmentChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private ChatAdapter adapter;
    private FirebaseUser user;
    private String role;

    @Override
    public void onResume() {
        super.onResume();
        user = FirebaseAuth.getInstance().getCurrentUser();
        /// check role apakah customer atau designer
        checkRole();

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
                        role = "" + documentSnapshot.get("role");
                        initRecylerView();
                        initViewModel();
                    }
                });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // FUNGSI UNTUK MENAMPILKAN LIST DATA gallery
    private void initRecylerView() {
        binding.rvMessage.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatAdapter(role);
        binding.rvMessage.setAdapter(adapter);
    }

    /// FUNGSI UNTUK MENDAPATKAN LIST DATA gallery DARI FIREBASE
    private void initViewModel() {
        ChatViewModel viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        /// AMBIL DATA gallery DARI DATABASE
        binding.progressBar.setVisibility(View.VISIBLE);
        if(role.equals("customer")) {
            viewModel.setListChatByCustomer(user.getUid());
        } else {
            viewModel.setListChatByDesigner(user.getUid());
        }
        viewModel.getListChat().observe(getViewLifecycleOwner(), chatList -> {
            if (chatList.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                adapter.setData(chatList);
            } else {
                binding.noData.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}