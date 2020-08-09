package com.example.usertracker.Models;

public class TagDetails {

    int s_no;
    String id;
    String name;
    String tag_id;
    String flag;
    String insertTime;

    public TagDetails(String id, String name, String tag_id, String flag, String insertTime) {
        this.id = id;
        this.name = name;
        this.tag_id = tag_id;
        this.flag = flag;
        this.insertTime = insertTime;
    }

    public int getS_no() {
        return s_no;
    }

    public void setS_no(int s_no) {
        this.s_no = s_no;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }
}
