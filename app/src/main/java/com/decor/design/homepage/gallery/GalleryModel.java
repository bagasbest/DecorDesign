package com.decor.design.homepage.gallery;

import android.os.Parcel;
import android.os.Parcelable;

public class GalleryModel implements Parcelable {

    private String caption;
    private String image;
    private String date;
    private String galleryId;
    private String like;
    private String adminId;

    public GalleryModel() {}

    protected GalleryModel(Parcel in) {
        caption = in.readString();
        image = in.readString();
        date = in.readString();
        galleryId = in.readString();
        like = in.readString();
        adminId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(caption);
        dest.writeString(image);
        dest.writeString(date);
        dest.writeString(galleryId);
        dest.writeString(like);
        dest.writeString(adminId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GalleryModel> CREATOR = new Creator<GalleryModel>() {
        @Override
        public GalleryModel createFromParcel(Parcel in) {
            return new GalleryModel(in);
        }

        @Override
        public GalleryModel[] newArray(int size) {
            return new GalleryModel[size];
        }
    };

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public String getGalleryId() {
        return galleryId;
    }

    public void setGalleryId(String galleryId) {
        this.galleryId = galleryId;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
}
