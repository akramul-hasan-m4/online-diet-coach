package com.daffodil.online.dietcoach.model;

import com.google.firebase.database.PropertyName;

public class Users {

    private String address;
    private String age;
    private String bloodGroup;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String phone;
    private String userId;
    private String profileImage;
    private String userName;
    private String gender;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // @PropertyName("blood_group")
    public String getBloodGroup() {
        return bloodGroup;
    }

   // @PropertyName("blood_group")
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

   // @PropertyName("first_name")
    public String getFirstName() {
        return firstName;
    }

   // @PropertyName("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

  //  @PropertyName("last_name")
    public String getLastName() {
        return lastName;
    }

  //  @PropertyName("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
