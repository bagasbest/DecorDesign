package com.decor.design.homepage.gallery;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class GalleryViewModel extends ViewModel {

    /// KELAS VIEW MODEL BERFUNGSI UNTUK MENGAMBIL DATA DARI FIRESTORE KEMUDIAN MENERUSKANNYA KEPADA ACTIVITY YANG DI TUJU
    /// CONTOH KELAS Gallery VIEW MODEL MENGAMBIL DATA DARI COLLECTION "gallery", KEMUDIAN SETELAH DI AMBIL, DATA DIMASUKKAN KEDALAM MODEL, SETELAH ITU DITERUSKAN KEPADA ACTIVITY POST, SEHINGGA ACTIVITY DAPAT MENAMPILKAN DATA GALLERY

    private final MutableLiveData<ArrayList<GalleryModel>> listGallery = new MutableLiveData<>();
    final ArrayList<GalleryModel> galleryModelArrayList = new ArrayList<>();

    private static final String TAG = GalleryViewModel.class.getSimpleName();

    public void setListGallery() {
        galleryModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("gallery")
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                GalleryModel model = new GalleryModel();

                                model.setCaption("" + document.get("caption"));
                                model.setDate("" + document.get("date"));
                                model.setImage("" + document.get("image"));
                                model.setLike("" + document.get("like"));
                                model.setGalleryId("" + document.get("galleryId"));
                                model.setAdminId("" + document.get("adminId"));

                                galleryModelArrayList.add(model);
                            }
                            listGallery.postValue(galleryModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public LiveData<ArrayList<GalleryModel>> getListGallery() {
        return listGallery;
    }

}
