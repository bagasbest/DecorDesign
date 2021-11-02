package com.decor.design.homepage.chat.message_list.message_db;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MessageDatabase {
    public static void sendChat(String message, String format, String myUid, String otherId, String chatId) {

        String timeInMillis = String.valueOf(System.currentTimeMillis());

        Map<String, Object> logChat   = new HashMap<>();
        logChat.put("message", message);
        logChat.put("dateTime", format);
        logChat.put("uid", myUid);

        // UPDATE LOG CHAT (SISI PENGIRIM)
        FirebaseFirestore
                .getInstance()
                .collection("chat")
                .document(chatId)
                .collection(myUid+otherId)
                .document(timeInMillis)
                .set(logChat)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d("SENDER MSG", "success");
                    }else {
                        Log.d("SENDER MSG", task.toString());
                    }
                });

        // UPDATE LOG CHAT (SISI PENERIMA)
        FirebaseFirestore
                .getInstance()
                .collection("chat")
                .document(chatId)
                .collection(otherId+myUid)
                .document(timeInMillis)
                .set(logChat)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d("RECEIVER MSG", "success");
                    }else {
                        Log.d("RECEIVER MSG", task.toString());
                    }
                });


        Map<String, Object> lastMessage = new HashMap<>();
        lastMessage.put("lastMessage", message);
        lastMessage.put("dateTime", format);

        /// update last chat
        FirebaseFirestore
                .getInstance()
                .collection("chat")
                .document(chatId)
                .update(lastMessage)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d("SENDER MSG", "success");
                    }else {
                        Log.d("SENDER MSG", task.toString());
                    }
                });
    }
}
