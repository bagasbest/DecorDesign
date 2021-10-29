package com.decor.design.homepage.post;

import android.os.Parcel;
import android.os.Parcelable;

public class PostModel implements Parcelable {

    private String name;
    private String dp;
    private String userId;
    private String image;
    private String date;
    private String caption;
    private String postId;
    private String like;

    public PostModel() {}

    protected PostModel(Parcel in) {
        name = in.readString();
        dp = in.readString();
        userId = in.readString();
        image = in.readString();
        date = in.readString();
        caption = in.readString();
        postId = in.readString();
        like = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(dp);
        dest.writeString(userId);
        dest.writeString(image);
        dest.writeString(date);
        dest.writeString(caption);
        dest.writeString(postId);
        dest.writeString(like);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostModel> CREATOR = new Creator<PostModel>() {
        @Override
        public PostModel createFromParcel(Parcel in) {
            return new PostModel(in);
        }

        @Override
        public PostModel[] newArray(int size) {
            return new PostModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }
}
