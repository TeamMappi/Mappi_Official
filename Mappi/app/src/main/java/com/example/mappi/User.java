package com.example.mappi;

public class User {

    public String userEmail, userPassword;
    private String modeType;

    public User(){

    }

    public User(String userEmail, String userPassword){
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    public String getModeType()

    {
        return modeType;
    }

    public void setModeType(String modeType)
    {
        this.modeType = modeType;
    }
}
