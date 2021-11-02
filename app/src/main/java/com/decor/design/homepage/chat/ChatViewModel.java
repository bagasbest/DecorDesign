package com.decor.design.homepage.chat;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ChatViewModel extends ViewModel {

    /// KELAS VIEW MODEL BERFUNGSI UNTUK MENGAMBIL DATA DARI FIRESTORE KEMUDIAN MENERUSKANNYA KEPADA ACTIVITY YANG DI TUJU
    /// CONTOH KELAS CHAT VIEW MODEL MENGAMBIL DATA DARI COLLECTION "chat", KEMUDIAN SETELAH DI AMBIL, DATA DIMASUKKAN KEDALAM MODEL, SETELAH ITU DITERUSKAN KEPADA ACTIVITY CHAT, SEHINGGA ACTIVITY DAPAT MENAMPILKAN DATA CHAT LIST

    private final MutableLiveData<ArrayList<ChatModel>> listChat = new MutableLiveData<>();
    final ArrayList<ChatModel> chatModelArrayList = new ArrayList<>();

    private static final String TAG = ChatViewModel.class.getSimpleName();

    public void setListChatByDesigner(String myUid) {
        chatModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("chat")
                    .whereEqualTo("designerId", myUid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                ChatModel model = new ChatModel();

                                model.setChatId("" + document.get("chatId"));
                                model.setCustomerDp("" + document.get("customerDp"));
                                model.setCustomerId("" + document.get("customerId"));
                                model.setCustomerName("" + document.get("customerName"));
                                model.setDateTime("" + document.get("dateTime"));
                                model.setDesignerDp("" + document.get("designerDp"));
                                model.setDesignerId("" + document.get("designerId"));
                                model.setDesignerName("" + document.get("designerName"));
                                model.setLastMessage("" + document.get("lastMessage"));

                                chatModelArrayList.add(model);
                            }
                            listChat.postValue(chatModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void setListChatByCustomer(String myUid) {
        chatModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("chat")
                    .whereEqualTo("customerId", myUid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                ChatModel model = new ChatModel();

                                model.setChatId("" + document.get("chatId"));
                                model.setCustomerDp("" + document.get("customerDp"));
                                model.setCustomerId("" + document.get("customerId"));
                                model.setCustomerName("" + document.get("customerName"));
                                model.setDateTime("" + document.get("dateTime"));
                                model.setDesignerDp("" + document.get("designerDp"));
                                model.setDesignerId("" + document.get("designerId"));
                                model.setDesignerName("" + document.get("designerName"));
                                model.setLastMessage("" + document.get("lastMessage"));

                                chatModelArrayList.add(model);
                            }
                            listChat.postValue(chatModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public LiveData<ArrayList<ChatModel>> getListChat() {
        return listChat;
    }

}
