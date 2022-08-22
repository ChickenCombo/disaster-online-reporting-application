package com.app.dorav4.models;

public class Users {
    private String fullName, profilePicture;

    public Users() {
    }

    public Users(String fullName, String profilePicture) {
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
