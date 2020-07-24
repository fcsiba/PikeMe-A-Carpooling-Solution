package com.example.carpooltest1.Models;

import java.sql.Time;

public class ReviewList {

    String UserID;
    String Time;
    int Status;

    public ReviewList(String userID, String time,int Stat) {
        UserID = userID;
        this.Time = time;
        Status = Stat;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }



    public String getUserID() {
        return UserID;
    }

    public int getStatus() {
        return Status;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getTime() {
        return Time;
    }
}
