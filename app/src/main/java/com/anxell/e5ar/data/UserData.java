package com.anxell.e5ar.data;

import java.io.Serializable;

/**
 * Created by Sean on 2017/10/29.
 */

public class UserData implements Serializable{
    private String mId;
    private String mPassword;
    private String mCard;
    private int mUserIndex;

    public UserData(String id,  String password, String card,int userIndex) {
        this.mId = id;
        this.mPassword = password;
        this.mCard = card;
        this.mUserIndex = userIndex;

    }

    public String getId() {
        return mId;
    }


    public String getPasswrod() {
        return mPassword;
    }

    public String getCard() {return mCard;}

    public int getUserIndex(){
        return mUserIndex;
    }
}
