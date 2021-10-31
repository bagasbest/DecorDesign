package com.decor.design.homepage.profile;

import android.os.Parcel;
import android.os.Parcelable;

public class ProfileModel implements Parcelable {
    private String name;
    private String username;
    private String phone;
    private String background;
    private String work;
    private String education;
    private String skill;
    private String softSkill;
    private String organization;
    private String hobby;


    protected ProfileModel(Parcel in) {
        name = in.readString();
        username = in.readString();
        phone = in.readString();
        background = in.readString();
        work = in.readString();
        education = in.readString();
        skill = in.readString();
        softSkill = in.readString();
        organization = in.readString();
        hobby = in.readString();
    }

    public ProfileModel() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(phone);
        dest.writeString(background);
        dest.writeString(work);
        dest.writeString(education);
        dest.writeString(skill);
        dest.writeString(softSkill);
        dest.writeString(organization);
        dest.writeString(hobby);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProfileModel> CREATOR = new Creator<ProfileModel>() {
        @Override
        public ProfileModel createFromParcel(Parcel in) {
            return new ProfileModel(in);
        }

        @Override
        public ProfileModel[] newArray(int size) {
            return new ProfileModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getSoftSkill() {
        return softSkill;
    }

    public void setSoftSkill(String softSkill) {
        this.softSkill = softSkill;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }
}
