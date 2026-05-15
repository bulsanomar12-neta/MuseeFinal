package com.example.musee.classes;

import android.os.Parcel;

import java.util.ArrayList;

public class User {
    private String firstName;
    private String lastName;
    private String userName;
    private String phoneNum;
    private String address;
    private String photo;
    private String eMail;
    private ArrayList<String> userPieces;
    private ArrayList<String> userPiecesCart;

    public User() {}

    public User(String firstName, String lastName, String userName,
                String phoneNum, String address, String eMail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.phoneNum = phoneNum;
        this.address = address;
        this.eMail = eMail;
        this.photo = ""; // بدون صورة افتراضية
        this.userPieces = new ArrayList<>();
        this.userPiecesCart = new ArrayList<>();
    }

    public User(String firstName, String lastName, String userName,
                String phoneNum, String address, String photo, String eMail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.phoneNum = phoneNum;
        this.address = address;
        this.photo = photo;
        this.eMail = eMail;
        this.userPieces = new ArrayList<>();
        this.userPiecesCart = new ArrayList<>();
    }

    public User(Parcel in) {
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + userName + '\'' +
                ", phone='" + phoneNum + '\'' +
                ", address='" + address + '\'' +
                ", photo='" + photo + '\'' +
                ", eMail='" + eMail + '\'' +
                '}';
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String geteMail() {return eMail;}

    public void seteMail(String eMail) {this.eMail = eMail;}

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public ArrayList<String> getUserPieces() {
        return userPieces;
    }

    public void setUserPieces(ArrayList<String> userPieces) {
        this.userPieces = userPieces;
    }

    public ArrayList<String> getUserPiecesCart() {
        return userPiecesCart;
    }

    public void setUserPiecesCart(ArrayList<String> userPiecesCart) {
        this.userPiecesCart = userPiecesCart;
    }

}
