package com.decor.design.homepage.chat.message_list;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.decor.design.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private final ArrayList<MessageModel> listMessage = new ArrayList<>();
    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<MessageModel> items) {
        listMessage.clear();
        listMessage.addAll(items);
        notifyDataSetChanged();
    }

    private final String uid;
    public MessageAdapter(String uid) {
        this.uid = uid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listMessage.get(position));
    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently signed user
        if(listMessage.get(position).getUid().equals(uid)) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView message, dateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageTv);
            dateTime = itemView.findViewById(R.id.timeTv);
        }

        public void bind(MessageModel model) {
            message.setText(model.getMessage());
            dateTime.setText(model.getDateTime());
        }
    }
}
