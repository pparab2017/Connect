package com.mad.connect.Entities;

import java.util.Date;

/**
 * Created by pushparajparab on 11/20/16.
 */
public class Photos {

    String photoUrl,key;
    Date date;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
