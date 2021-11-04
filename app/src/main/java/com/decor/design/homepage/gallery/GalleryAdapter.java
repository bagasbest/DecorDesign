package com.decor.design.homepage.gallery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.decor.design.R;

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

        ImageView image;
        ConstraintLayout cv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.dp);
            cv = itemView.findViewById(R.id.cv);
        }

        @SuppressLint("SetTextI18n")
        public void bind(GalleryModel model) {
            Glide.with(itemView.getContext())
                    .load(model.getImage())
                    .into(image);


            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), GalleryDetailActivity.class);
                    intent.putExtra(GalleryDetailActivity.EXTRA_GALLERY_ID, model.getGalleryId());
                    intent.putExtra(GalleryDetailActivity.EXTRA_GALLERY, model);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
