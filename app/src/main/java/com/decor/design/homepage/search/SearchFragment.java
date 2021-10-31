package com.decor.design.homepage.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.decor.design.databinding.FragmentSearchBinding;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SearchAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    initRecylerView();
                    initViewModel(editable.toString());
                } else {
                    binding.noData.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initRecylerView() {
        // tampilkan daftar designer
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SearchAdapter();
        binding.recyclerView.setAdapter(adapter);
    }

    // inisiasi view model untuk menampilkan list designer dari database
    private void initViewModel(String query) {
        SearchViewModel viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setListDesigner(query);
        viewModel.getDesignerList().observe(this, listData -> {
            if (listData.size() > 0) {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.GONE);
                adapter.setData(listData);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}