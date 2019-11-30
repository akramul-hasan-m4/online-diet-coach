package com.daffodil.online.dietcoach.model;

public class Conversation {

    private String userName;
    private String userType;
    private String message;
    private String imageData;
    private String date;
    private String connectFromPhone;
    private String connectToPhone;
    private String userProfileImage;
    private String drProfileImage;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getConnectFromPhone() {
        return connectFromPhone;
    }

    public void setConnectFromPhone(String connectFromPhone) {
        this.connectFromPhone = connectFromPhone;
    }

    public String getConnectToPhone() {
        return connectToPhone;
    }

    public void setConnectToPhone(String connectToPhone) {
        this.connectToPhone = connectToPhone;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getDrProfileImage() {
        return drProfileImage;
    }

    public void setDrProfileImage(String drProfileImage) {
        this.drProfileImage = drProfileImage;
    }
}
