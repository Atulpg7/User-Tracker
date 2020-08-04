package com.example.usertracker.Models;

public class TagDetails {

    String tag_id;
    String user_name;
    String time_stamp;
    int sn_no;

    public TagDetails(String tag_id, String user_name, String time_stamp, int sn_no) {
        this.tag_id = tag_id;
        this.user_name = user_name;
        this.time_stamp = time_stamp;
        this.sn_no = sn_no;
    }

    public int getSn_no() {
        return sn_no;
    }

    public String getTag_id() {
        return tag_id;
    }
    public String getUser_name() {
        return user_name;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

}
