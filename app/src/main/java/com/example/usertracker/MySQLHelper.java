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

        String sql = "CREATE TABLE USERS (_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT,tagID TEXT, timeStamp TEXT )";
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public void insertData(String username,String tag,String timeStamp,SQLiteDatabase database){
        ContentValues values = new ContentValues();

        values.put("username",username);
        values.put("tagID",tag);
        values.put("timeStamp",timeStamp);

        database.insert("USERS",null,values);

    }
}
