package com.decor.design.homepage.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.decor.design.R;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final ArrayList<SearchModel> listDesigner = new ArrayList<>();
    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<SearchModel> items) {
        listDesigner.clear();
        listDesigner.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listDesigner.get(position));
    }

    @Override
    public int getItemCount() {
        return listDesigner.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cv;
        ImageView dp;
        TextView name, background;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            dp = itemView.findViewById(R.id.circleImageView);
            name = itemView.findViewById(R.id.textView19);
            background = itemView.findViewById(R.id.background);
        }

        public void bind(SearchModel model) {

            if(!model.getDp().equals("")) {
                Glide.with(itemView.getContext())
                        .load(model.getDp())
                        .into(dp);
            }


            name.setText(model.getName());
            background.setText(model.getBackground());

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), SearchDetailActivity.class);
                    intent.putExtra(SearchDetailActivity.EXTRA_SEARCH, model);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
