package com.app.dorav4.models;

public class Reports {
    private String date, description, disasterType, fullName, profilePicture, reportPicture, userId, reportId, upvotes, comments, latitude, longitude, address;

    public Reports() {

    }

    public Reports(String date, String description, String disasterType, String fullName, String profilePicture, String reportPicture, String userId, String reportId, String upvotes, String comments, String latitude, String longitude, String address) {
        this.date = date;
        this.description = description;
        this.disasterType = disasterType;
        this.fullName = fullName;
        this.profilePicture = profilePicture;
        this.reportPicture = reportPicture;
        this.userId = userId;
        this.reportId = reportId;
        this.upvotes = upvotes;
        this.comments = comments;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisasterType() {
        return disasterType;
    }

    public void setDisasterType(String disasterType) {
        this.disasterType = disasterType;
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

    public String getReportPicture() {
        return reportPicture;
    }

    public void setReportPicture(String reportPicture) {
        this.reportPicture = reportPicture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(String upvotes) {
        this.upvotes = upvotes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}