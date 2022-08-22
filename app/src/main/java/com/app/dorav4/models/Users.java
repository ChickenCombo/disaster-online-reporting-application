package com.app.dorav4.models;

public class Users {
    private String userId, fullName, profilePicture;

    public Users() {
    }

    public Users(String userId, String fullName, String profilePicture) {
        this.userId = userId;
        this.fullName = fullName;
        this.profilePicture = profilePicture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
