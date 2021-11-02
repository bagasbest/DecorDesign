package com.decor.design.homepage.chat;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatModel implements Parcelable {

    private String chatId;
    private String designerId;
    private String customerId;
    private String designerName;
    private String customerName;
    private String designerDp;
    private String customerDp;
    private String dateTime;
    private String lastMessage;

    public ChatModel() {}


    protected ChatModel(Parcel in) {
        chatId = in.readString();
        designerId = in.readString();
        customerId = in.readString();
        designerName = in.readString();
        customerName = in.readString();
        designerDp = in.readString();
        customerDp = in.readString();
        dateTime = in.readString();
        lastMessage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatId);
        dest.writeString(designerId);
        dest.writeString(customerId);
        dest.writeString(designerName);
        dest.writeString(customerName);
        dest.writeString(designerDp);
        dest.writeString(customerDp);
        dest.writeString(dateTime);
        dest.writeString(lastMessage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatModel> CREATOR = new Creator<ChatModel>() {
        @Override
        public ChatModel createFromParcel(Parcel in) {
            return new ChatModel(in);
        }

        @Override
        public ChatModel[] newArray(int size) {
            return new ChatModel[size];
        }
    };

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getDesignerId() {
        return designerId;
    }

    public void setDesignerId(String designerId) {
        this.designerId = designerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDesignerName() {
        return designerName;
    }

    public void setDesignerName(String designerName) {
        this.designerName = designerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDesignerDp() {
        return designerDp;
    }

    public void setDesignerDp(String designerDp) {
        this.designerDp = designerDp;
    }

    public String getCustomerDp() {
        return customerDp;
    }

    public void setCustomerDp(String customerDp) {
        this.customerDp = customerDp;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
