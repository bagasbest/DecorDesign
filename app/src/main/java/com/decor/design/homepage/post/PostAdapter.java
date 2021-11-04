package com.decor.design.homepage.post;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.decor.design.R;
import com.decor.design.homepage.post.edit_post.EditActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final ArrayList<PostModel> postList = new ArrayList<>();

    private final String role;
    public PostAdapter(String role) {
        this.role = role;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<PostModel> items) {
        postList.clear();
        postList.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(postList.get(position), role, postList);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView dp, image, loveClick;
        TextView name, caption, like, date;
        boolean isLike = false;
        int likes = 0;

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
        public void bind(PostModel model, String role, ArrayList<PostModel> postList) {
            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE);
            boolean wasLike = sharedPreferences.getBoolean(myUid + model.getPostId(), false);
            likes = sharedPreferences.getInt(model.getPostId(), 0);

            Glide.with(itemView.getContext())
                    .load(model.getImage())
                    .into(image);

            if (!model.getDp().equals("")) {
                Glide.with(itemView.getContext())
                        .load(model.getDp())
                        .into(dp);
            }

            name.setText(model.getName());
            like.setText("" + likes + " Likes");
            caption.setText(model.getCaption());
            date.setText(model.getDate());

            if(!myUid.equals(model.getUserId())) {
                /// Jika role adalah admin atau customer, maka dapat chat dengan designer
                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        /// jika role == admin, maka bisa hanya bisa delete post designer
                        if(role.equals("admin")) {
                            String[] options = {"Delete Post"};

                            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                            builder.setTitle("Choice");
                            builder.setItems(options, (dialog, which) -> {
                                if (which == 0) {
                                    dialog.dismiss();
                                    deletePost(model.getPostId(), postList);
                                }
                            });
                            builder.create().show();
                        } else if (role.equals("customer")){
                            /// jika role == user, maka hanya bisa chat designer
                            String[] options = {"Chat Designer"};

                            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                            builder.setTitle("Choice");
                            builder.setItems(options, (dialog, which) -> {
                                if (which == 0) {
                                    dialog.dismiss();
                                    createNewChatWithDesigner(model, myUid);
                                }
                            });
                            builder.create().show();
                        }

                    }
                });
            } else {
                /// Jika role adalah designer, maka dapat mengedit postingan dan hapus postingan sendiri
                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] options = {"Edit Post", "Delete Post"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                        builder.setTitle("Choice");
                        builder.setItems(options, (dialog, which) -> {
                            dialog.dismiss();
                            if (which == 0) {
                                Intent intent = new Intent(itemView.getContext(), EditActivity.class);
                                intent.putExtra(EditActivity.EXTRA_POST, model);
                                itemView.getContext().startActivity(intent);
                            } else {
                                /// show confirm dialog, mau hapus atau tidak
                                new AlertDialog.Builder(itemView.getContext())
                                        .setTitle("Confirm Delete Post")
                                        .setMessage("Are you sure want to delete this post ?")
                                        .setIcon(R.drawable.ic_baseline_warning_24)
                                        .setPositiveButton("YES", (dialogInterface, i) -> {
                                            dialogInterface.dismiss();
                                            deletePost(model.getPostId(), postList);
                                        })
                                        .setNegativeButton("NO", (dialogInterface, i) -> {
                                            dialogInterface.dismiss();
                                        })
                                        .show();
                            }
                        });
                        builder.create().show();
                    }
                });
            }


            if (wasLike) {
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

                    if (isLike) {
                        likes--;
                        loveClick.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        like.setText((likes)+ " Likes");
                        editor.putBoolean(myUid + model.getPostId(), false);
                        editor.putInt(model.getPostId(), likes);
                        isLike = false;

                    } else {
                        likes++;
                        loveClick.setImageResource(R.drawable.ic_baseline_favorite_24);
                        like.setText((likes) + " Likes");
                        editor.putBoolean(myUid + model.getPostId(), true);
                        editor.putInt(model.getPostId(), likes);
                        isLike = true;

                    }
                    editor.apply();
                }
            });

        }

        private void deletePost(String postId, ArrayList<PostModel> postList) {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Confirm Delete Post")
                    .setMessage("Are you sure want to delete this post ?")
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton("YES", (dialogInterface, i) -> {
                        /// operation delete post by postId
                        FirebaseFirestore
                                .getInstance()
                                .collection("post")
                                .document(postId)
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            postList.remove(postList.get(getLayoutPosition()));
                                            notifyDataSetChanged();
                                            Toast.makeText(itemView.getContext(), "Success to delete this post", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(itemView.getContext(), "Failure to delete this post", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    })
                    .setNegativeButton("NO", (dialog, i) -> {
                        dialog.dismiss();
                    })
                    .show();
        }

        private void createNewChatWithDesigner(PostModel model, String myUid) {
            ProgressDialog mProgressDialog = new ProgressDialog(itemView.getContext());

            mProgressDialog.setMessage("Please wait until process finish...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .document(myUid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            Map<String, Object> consultation = new HashMap<>();
                            consultation.put("chatId", myUid+model.getUserId());
                            consultation.put("designerId", model.getUserId());
                            consultation.put("customerId", myUid);
                            consultation.put("designerName", model.getName());
                            consultation.put("customerName", "" + documentSnapshot.get("name"));
                            if(model.getDp().equals("null")){
                                consultation.put("designerDp", "");
                            } else {
                                consultation.put("designerDp", model.getDp());
                            }
                            consultation.put("dateTime", "");
                            consultation.put("customerDp", "" + documentSnapshot.get("dp"));
                            consultation.put("lastMessage", "");

                            FirebaseFirestore
                                    .getInstance()
                                    .collection("chat")
                                    .document(myUid+model.getUserId())
                                    .set(consultation)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            mProgressDialog.dismiss();
                                            showSuccessDialog();
                                        } else {
                                            mProgressDialog.dismiss();
                                            Log.e("Error Transaction", task.toString());
                                            Toast.makeText(itemView.getContext(), "Failure to create new chat with designer", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    });
        }

            private void showSuccessDialog() {
                new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Success Create Chat with Designer")
                        .setMessage("You can start chat with designer in Chat Navigation\n\nThank you")
                        .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .show();
            }
    }
}
