package com.decor.design.homepage.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.decor.design.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {


    private final ArrayList<GalleryModel> listGallery = new ArrayList<>();
    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<GalleryModel> items) {
        listGallery.clear();
        listGallery.addAll(items);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listGallery.get(position));
    }

    @Override
    public int getItemCount() {
        return listGallery.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image, loveClick;
        TextView like, date, caption;
        boolean isLike = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.roundedImageView);
            loveClick = itemView.findViewById(R.id.like);
            like = itemView.findViewById(R.id.textView5);
            date = itemView.findViewById(R.id.date);
            caption = itemView.findViewById(R.id.caption);

        }

        @SuppressLint("SetTextI18n")
        public void bind(GalleryModel model) {
            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE);
            boolean wasLike = sharedPreferences.getBoolean(myUid+model.getAdminId(), false);

            Glide.with(itemView.getContext())
                    .load(model.getImage())
                    .into(image);

            like.setText(model.getLike() + " Likes");
            date.setText(model.getDate());
            caption.setText(model.getCaption());


            if(wasLike) {
                loveClick.setImageResource(R.drawable.ic_baseline_favorite_24);
                isLike = true;
            } else {
                loveClick.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                isLike = false;
            }

            loveClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    if(isLike) {
                        loveClick.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        editor.putBoolean(myUid+model.getAdminId(), false);
                        isLike = false;
                    } else {
                        loveClick.setImageResource(R.drawable.ic_baseline_favorite_24);
                        editor.putBoolean(myUid+model.getAdminId(), true);
                        isLike = true;
                    }
                    editor.apply();
                }
            });



        }
    }
}
