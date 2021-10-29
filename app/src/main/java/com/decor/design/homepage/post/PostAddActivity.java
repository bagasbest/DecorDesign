package com.decor.design.homepage.post;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.decor.design.R;
import com.decor.design.databinding.ActivityPostAddBinding;

public class PostAddActivity extends AppCompatActivity {

    private ActivityPostAddBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}