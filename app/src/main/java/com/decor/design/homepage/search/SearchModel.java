package com.decor.design.homepage.search;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchModel implements Parcelable {
    private String name;
    private String background;
    private String uid;
    private String dp;


    public SearchModel() {}

    protected SearchModel(Parcel in) {
        name = in.readString();
        background = in.readString();
        uid = in.readString();
        dp = in.readString();
    }

    public static final Creator<SearchModel> CREATOR = new Creator<SearchModel>() {
        @Override
        public SearchModel createFromParcel(Parcel in) {
            return new SearchModel(in);
        }

        @Override
        public SearchModel[] newArray(int size) {
            return new SearchModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(background);
        parcel.writeString(uid);
        parcel.writeString(dp);
    }
}
