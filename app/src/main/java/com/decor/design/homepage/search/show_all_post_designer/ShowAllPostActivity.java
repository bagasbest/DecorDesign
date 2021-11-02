package com.decor.design.homepage.search.show_all_post_designer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.view.View;
import com.decor.design.databinding.ActivityShowAllBinding;
import com.decor.design.homepage.post.PostAdapter;
import com.decor.design.homepage.post.PostViewModel;

public class ShowAllPostActivity extends AppCompatActivity {

    public static final String UID = "uid";
    private ActivityShowAllBinding binding;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowAllBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initRecylerView();
        initViewModel();

        /// kembali ke halaman sebelumnya
        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    // FUNGSI UNTUK MENAMPILKAN LIST DATA POST
    private void initRecylerView() {
        binding.postRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter("");
        binding.postRv.setAdapter(adapter);
    }

    /// FUNGSI UNTUK MENDAPATKAN LIST DATA POST DARI FIREBASE
    private void initViewModel() {
        PostViewModel viewModel = new ViewModelProvider(this).get(PostViewModel.class);

        /// AMBIL DATA POST DARI DATABASE
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setListPostByUid(getIntent().getStringExtra(UID));
        viewModel.getPostList().observe(this, post -> {
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
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}