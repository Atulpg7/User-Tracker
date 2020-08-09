package com.example.usertracker;

import com.example.usertracker.Models.TagDetails;

import java.util.ArrayList;
import java.util.List;

public class GlobalData {

    public static String BASE_URL = "http://";
    public  static String IP = "172.104.40.208";
    public static String SUB_URL ="/exe_api/tag_api.php";
    public static List<TagDetails> list = new ArrayList<>();

    public static String getIP() {
        return IP;
    }

    public static void setIP(String IP) {
        GlobalData.IP = IP;
    }

    public static String getURL(){
        return BASE_URL+IP+SUB_URL;
    }

}
