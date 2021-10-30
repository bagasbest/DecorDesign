package com.decor.design.homepage.post;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.decor.design.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final ArrayList<PostModel> postList = new ArrayList<>();
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

        ImageView dp, image, loveClick;
        TextView name, caption, like, date;
        boolean isLike = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.dp);
            image = itemView.findViewById(R.id.roundedImageView);
            name = itemView.findViewById(R.id.name);
            caption = itemView.findViewById(R.id.caption);
            like = itemView.findViewById(R.id.textView5);
            date = itemView.findViewById(R.id.date);
            loveClick = itemView.findViewById(R.id.like);
        }

        @SuppressLint("SetTextI18n")
        public void bind(PostModel model) {
            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE);
            boolean wasLike = sharedPreferences.getBoolean(myUid+model.getUserId(), false);

            Glide.with(itemView.getContext())
                    .load(model.getImage())
                    .into(image);

            if(!model.getDp().equals("null")) {
                Glide.with(itemView.getContext())
                        .load(model.getDp())
                        .into(dp);
            }

            name.setText(model.getName());
            caption.setText(model.getCaption());
            like.setText(model.getLike() + " Likes");
            date.setText(model.getDate());

            if(wasLike) {
                loveClick.setImageResource(R.drawable.ic_baseline_favorite_24);
                isLike = true;
            } else {
                loveClick.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                isLike = false;
            }

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String []options = {"Chat Designer"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("Pilihan");
                    builder.setItems(options, (dialog, which) -> {
                        if(which == 0) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            });



            loveClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    if(isLike) {
                        loveClick.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        editor.putBoolean(myUid+model.getUserId(), false);
                        isLike = false;
                    } else {
                        loveClick.setImageResource(R.drawable.ic_baseline_favorite_24);
                        editor.putBoolean(myUid+model.getUserId(), true);
                        isLike = true;

                    }
                    editor.apply();
                }
            });

        }
    }
}
