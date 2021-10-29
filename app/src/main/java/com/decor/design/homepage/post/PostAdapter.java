package com.decor.design.homepage.post;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.decor.design.R;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private ArrayList<PostModel> postList = new ArrayList<>();
    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<PostModel> items) {
        postList.clear();
        postList.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(postList.get(position));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView dp, image;
        TextView name, caption, like, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.dp);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            caption = itemView.findViewById(R.id.caption);
            like = itemView.findViewById(R.id.like);
            date = itemView.findViewById(R.id.date);
        }

        @SuppressLint("SetTextI18n")
        public void bind(PostModel model) {
            Glide.with(itemView.getContext())
                    .load(model.getImage())
                    .into(image);

            if(!model.getDp().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(model.getDp())
                        .into(dp);
            }

            name.setText(model.getName());
            caption.setText(model.getCaption());
            like.setText(model.getLike() + " Likes");
            date.setText(model.getDate());

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }
    }
}
