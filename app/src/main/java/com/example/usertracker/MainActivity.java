package com.example.usertracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.usertracker.Models.TagDetails;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase database;
    MySQLHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getReferences();

        showDialog("A2X58B9","Henry","2020-07-02 12:00:PM");

        //getData();


    }


    //Function for getting references of all data
    private void getReferences() {
        helper = new MySQLHelper(this);
        database = helper.getWritableDatabase();
    }


    //Checking today's date for refresh list
    private boolean checkDate() {

        Calendar c = Calendar.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPref",MODE_PRIVATE);

        String oldDate = sharedPreferences.getString("date","null");
        String todayDate = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH);

        if(!oldDate.equals(todayDate)){

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("date",todayDate);
            editor.apply();

            return false;
        }

        return true;
    }


    //Receiving data from  Server / API
    private void fetchData() {

        StringRequest request = new StringRequest(Request.Method.GET, GlobalData.default_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, "Response: "+response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error: "+error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void showDialog(final String tag_id, final String username, final String timeStamp){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_box);

        TextView btn_ok = dialog.findViewById(R.id.btn_ok);
        final TextView tv_tag_ig =  dialog.findViewById(R.id.txt_tag);
        final TextView tv_username = dialog.findViewById(R.id.txt_username);

        tv_tag_ig.setText(tag_id);
        tv_username.setText(username);



        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                if (checkDate()){
                    helper.insertData(username,tag_id,timeStamp,database);
                    fetchDataFromSQL();
                    Toast.makeText(MainActivity.this, ""+GlobalData.list, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "New Data Will Show", Toast.LENGTH_SHORT).show();
                }
            }
        });


        dialog.show();

    }

    //Fetching Stored Users
    private void fetchDataFromSQL() {

        Cursor cursor = database.rawQuery("Select * from USERS",new String[]{});
        GlobalData.list.clear();

        if (cursor!=null)
            cursor.moveToFirst();

        do {
            int sno = cursor.getInt(0);
            String username = cursor.getString(1);
            String tag_id = cursor.getString(2);
            String timeStamp = cursor.getString(3);

            TagDetails details = new TagDetails(tag_id,username,timeStamp,sno);
            GlobalData.list.add(details);

        }while (cursor.moveToNext());
    }
}