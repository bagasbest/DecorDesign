package com.decor.design.homepage.search;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class SearchViewModel extends ViewModel {


    /// KELAS VIEW MODEL BERFUNGSI UNTUK MENGAMBIL DATA DARI FIRESTORE KEMUDIAN MENERUSKANNYA KEPADA ACTIVITY YANG DI TUJU
    /// CONTOH KELAS SEARCH VIEW MODEL MENGAMBIL DATA DARI COLLECTION "users", KEMUDIAN SETELAH DI AMBIL, DATA DIMASUKKAN KEDALAM MODEL, SETELAH ITU DITERUSKAN KEPADA ACTIVITY SEARCH, SEHINGGA ACTIVITY DAPAT MENAMPILKAN DATA SEARCH

    private final MutableLiveData<ArrayList<SearchModel>> listDesigner = new MutableLiveData<>();
    final ArrayList<SearchModel> designerArrayList = new ArrayList<>();

    private static final String TAG = SearchViewModel.class.getSimpleName();

    public void setListDesigner(String query) {
        designerArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .whereEqualTo("role", "designer")
                    .whereGreaterThanOrEqualTo("nameTemp", query)
                    .whereLessThanOrEqualTo("nameTemp", query + '\uf8ff')
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                SearchModel model = new SearchModel();

                                model.setDp("" + document.get("dp"));
                                model.setName("" + document.get("name"));
                                model.setBackground("" + document.get("background"));
                                model.setUid("" + document.get("uid"));


                                designerArrayList.add(model);
                            }
                            listDesigner.postValue(designerArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public LiveData<ArrayList<SearchModel>> getDesignerList() {
        return listDesigner;
    }

}
