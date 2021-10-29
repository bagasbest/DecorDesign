package com.decor.design.homepage.post;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class PostViewModel extends ViewModel {

    /// KELAS VIEW MODEL BERFUNGSI UNTUK MENGAMBIL DATA DARI FIRESTORE KEMUDIAN MENERUSKANNYA KEPADA ACTIVITY YANG DI TUJU
    /// CONTOH KELAS BOOKING VIEW MODEL MENGAMBIL DATA DARI COLLECTION "post", KEMUDIAN SETELAH DI AMBIL, DATA DIMASUKKAN KEDALAM MODEL, SETELAH ITU DITERUSKAN KEPADA ACTIVITY POST, SEHINGGA ACTIVITY DAPAT MENAMPILKAN DATA POST

    private final MutableLiveData<ArrayList<PostModel>> listPost = new MutableLiveData<>();
    final ArrayList<PostModel> postModelArrayList = new ArrayList<>();

    private static final String TAG = PostViewModel.class.getSimpleName();

    public void setListPost() {
        postModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("post")
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                PostModel model = new PostModel();

                                model.setCaption("" + document.get("caption"));
                                model.setDate("" + document.get("date"));
                                model.setDp("" + document.get("dp"));
                                model.setImage("" + document.get("image"));
                                model.setLike("" + document.get("like"));
                                model.setName("" + document.get("name"));
                                model.setPostId("" + document.get("postId"));
                                model.setUserId("" + document.get("userId"));

                                postModelArrayList.add(model);
                            }
                            listPost.postValue(postModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public LiveData<ArrayList<PostModel>> getPostList() {
        return listPost;
    }

}
