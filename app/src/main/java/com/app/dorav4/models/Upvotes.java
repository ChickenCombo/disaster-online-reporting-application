package com.app.dorav4.models;

public class Upvotes {
    String fullName, profilePicture;

    public Upvotes() {
    }

    public Upvotes(String fullName, String profilePicture) {
        this.fullName = fullName;
        this.profilePicture = profilePicture;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
