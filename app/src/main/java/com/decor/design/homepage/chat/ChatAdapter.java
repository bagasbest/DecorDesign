package com.decor.design.homepage.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.decor.design.R;
import com.decor.design.homepage.chat.message_list.MessageListActivity;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final ArrayList<ChatModel> listChat = new ArrayList<>();

    private final String role;
    public ChatAdapter(String role) {
        this.role = role;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<ChatModel> items) {
        listChat.clear();
        listChat.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listChat.get(position), role);
    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        ImageView dp;
        TextView name, lastMessage, dateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            dp = itemView.findViewById(R.id.dp);
            name = itemView.findViewById(R.id.name);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            dateTime = itemView.findViewById(R.id.dateTime);
        }

        @SuppressLint("SetTextI18n")
        public void bind(ChatModel model, String role) {
            if(role.equals("customer")) {
                /// chat dari sisi kustomer, berarti chat nya ke arah designer
                if(!model.getDesignerDp().equals("")) {
                    Glide.with(itemView.getContext())
                            .load(model.getDesignerDp())
                            .into(dp);
                }
                name.setText(model.getDesignerName());

            } else {
                /// chat dari sisi designer, berarti chat nya ke arah customer
                if(!model.getCustomerDp().equals("")) {
                    Glide.with(itemView.getContext())
                            .load(model.getCustomerDp())
                            .into(dp);
                }
                name.setText(model.getCustomerName());
            }
            lastMessage.setText("message: " + model.getLastMessage());
            dateTime.setText(model.getDateTime());


            /// klik item list chat dan menuju message activity
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), MessageListActivity.class);
                    intent.putExtra(MessageListActivity.EXTRA_CHAT, model);
                    intent.putExtra(MessageListActivity.EXTRA_ROLE, role);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
