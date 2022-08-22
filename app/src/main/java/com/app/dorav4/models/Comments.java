package com.app.dorav4.models;

public class Comments {
    private String commentId, comment, name, profilePicture, date, userId;

    public Comments() {
    }

    public Comments(String commentId, String comment, String name, String profilePicture, String date, String userId) {
        this.commentId = commentId;
        this.comment = comment;
        this.name = name;
        this.profilePicture = profilePicture;
        this.date = date;
        this.userId = userId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
