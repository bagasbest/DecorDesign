package com.decor.design.homepage.gallery;

public class GalleryModel {

    private String caption;
    private String image;
    private String date;
    private String galleryId;
    private String like;
    private String adminId;

    public GalleryModel() {}

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
