package com.mad.connect.Entities;

import java.util.ArrayList;

/**
 * Created by pushparajparab on 11/17/16.
 */
public class User {
    String userName,userID,firstName, lastName, dpUrl;
    boolean gender;

public String getGenderText()
{
    if(gender)
    {
        return "Female";
    }
    else
    {
        return "Male";
    }

}


    public String getFullName()
    {
        return  firstName +" " +lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getDpUrl() {

        return dpUrl;
    }

    public String CheckAndGetDpUrl() {
        if(dpUrl.equals(""))
        {
            return null;
        }else
        {
            return dpUrl;
        }}


    public void setDpUrl(String dpUrl) {
        this.dpUrl = dpUrl;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }


}
