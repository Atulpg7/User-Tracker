package com.example.usertracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "MyDb";
    private static final int version = 1;


    public MySQLHelper(Context context){
        super(context,DB_NAME,null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sql = "CREATE TABLE USERS (_id INTEGER PRIMARY KEY AUTOINCREMENT, userId TEXT,name TEXT,tagId TEXT,flagField TEXT,insertTime TEXT )";
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    //Function for inserting data
    public void insertData(String id, String name, String tag_id, String flag, String insert_time, SQLiteDatabase database) {

        ContentValues values = new ContentValues();

        values.put("userId",id);
        values.put("name",name);
        values.put("tagId",tag_id);
        values.put("flagField",flag);
        values.put("insertTime",insert_time);

        database.insert("USERS",null,values);
    }
}
