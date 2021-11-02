package com.decor.design.homepage.chat.message_list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.decor.design.R;
import com.decor.design.databinding.ActivityMessageListBinding;
import com.decor.design.homepage.chat.ChatAdapter;
import com.decor.design.homepage.chat.ChatModel;
import com.decor.design.homepage.chat.ChatViewModel;
import com.decor.design.homepage.chat.message_list.message_db.MessageDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageListActivity extends AppCompatActivity {

    public static final String EXTRA_CHAT = "chat";
    public static final String EXTRA_ROLE = "role";
    private ActivityMessageListBinding binding;
    private ChatModel model;
    private String myUid, otherId;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_CHAT);

        /// set name and avatar by role.
        if (getIntent().getStringExtra(EXTRA_ROLE).equals("customer")) {
            binding.name.setText(model.getDesignerName());
            if (!model.getDesignerDp().equals("null")) {
                Glide.with(MessageListActivity.this)
                        .load(model.getDesignerDp())
                        .into(binding.dp);
            }
            myUid = model.getCustomerId();
            otherId = model.getDesignerId();
        } else {
            binding.name.setText(model.getCustomerName());
            if (!model.getCustomerDp().equals("null")) {
                Glide.with(MessageListActivity.this)
                        .load(model.getCustomerDp())
                        .into(binding.dp);
            }
            myUid = model.getDesignerId();
            otherId = model.getCustomerId();
        }
        initRecyclerView();
        initViewModel();


        /// send message
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(myUid, otherId);
            }
        });


        /// kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void sendMessage(String myUid, String otherId) {

        String message = binding.messageEt.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(MessageListActivity.this, "Message must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat getDate = new SimpleDateFormat("dd MMM yyyy, HH:mm");
        String format = getDate.format(new Date());

        MessageDatabase.sendChat(message, format, myUid, otherId, model.getChatId());
        binding.messageEt.getText().clear();

        // LOAD CHAT HISTORY
        initRecyclerView();
        initViewModel();

    }

    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.chatRv.setLayoutManager(linearLayoutManager);
        adapter = new MessageAdapter(myUid);
        binding.chatRv.setAdapter(adapter);

    }

    private void initViewModel() {

        MessageViewModel viewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setListMessage(model.getChatId(), myUid, otherId);
        viewModel.getMessageList().observe(this, messageList -> {
            Log.e("tag", String.valueOf(messageList.size()));
            if (messageList != null) {
                adapter.setData(messageList);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}