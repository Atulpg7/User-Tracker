package com.example.usertracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.usertracker.Adapters.CustomAdapter;
import com.example.usertracker.Models.TagDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //Recycler view Part variables
    SQLiteDatabase database;
    MySQLHelper helper;
    RecyclerView rv_main;
    CustomAdapter adapter;

    //If no data present in Database
    TextView txt_no_data_found;

    //Server variables
    boolean isResponse;
    TagDetails details;


    //Configuration of IP Address
    EditText ip_address;
    TextView btn_save;
    ImageView settings_icon;


    //Extra
    ProgressDialog dialog;
    Handler handler_tag,handler_ue;
    Runnable runnable_tag,runnable_ue;


    //Shared Preferences for date and ip Address
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getReferences();
        setBtnClicks();

        new fetchData().execute();
        new fetchNewTag().execute();

        setTimerForTag();
        setTimerForUE();

    }

    //Function for hitting the User Enrollment API after every 30 Seconds
    private void setTimerForUE() {
        handler_ue = new Handler();
        final int delay = 30000; //milliseconds
        runnable_ue = new Runnable() {
            @Override
            public void run() {
                new fetchNewTag().execute();
                handler_ue.postDelayed(this, delay);
            }
        };
        handler_ue.postDelayed(runnable_ue, delay);
    }


    //Function for hitting the Tag Fetching API after every 1 Minute
    private void setTimerForTag() {
        handler_tag = new Handler();
        final int delay = 60000; //milliseconds
        runnable_tag = new Runnable() {
            @Override
            public void run() {
                new fetchData().execute();
                handler_tag.postDelayed(this, delay);
            }
        };
        handler_tag.postDelayed(runnable_tag, delay);
    }


    //class for send Name with TagId
    class fetchNewTag extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {



            String ip = sharedPreferences.getString("ip_address","null");

            String URL = GlobalData.getNewURl();
            if(!ip.equals("null"))
                URL = GlobalData.BASE_URL+ip+GlobalData.SUB_URL_2;

            Log.e("UE Fetch BASE URL==>",URL);

            StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.e("UE Fetch Response:==> ", response);

                    if (!response.equals("null")) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            String tag_id = jsonObject.getString("tag_id");
                            String id= jsonObject.getString("id");

                            showUserEnrollmentPopup(tag_id,id);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(MainActivity.this, "Please Check IP Address", Toast.LENGTH_SHORT).show();
                    Log.e("UE Fetch Error:==> ", error+"");
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("reader", "0");
                    Log.e("Sending Data:==> ", params.toString());
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(request);
            return null;
        }
    }


    //Popup for User Enrollment
    private void showUserEnrollmentPopup(final String tag_id, final String id) {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_box_sending);

        TextView btn_ok = dialog.findViewById(R.id.btn_ok);
        final TextView tv_tag_ig = dialog.findViewById(R.id.txt_tag);
        final EditText et_username = dialog.findViewById(R.id.et_username);

        tv_tag_ig.setText(tag_id);


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = et_username.getText().toString();

                if(!username.equals("")) {

                    new sendResponseBackUserEnrollment(tag_id,username,id).execute();

                    dialog.dismiss();
                }
                else {
                    Toast.makeText(MainActivity.this, "Enter username !", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();


    }

    //Sending data again to the Server for User Enrollment
    private class sendResponseBackUserEnrollment extends AsyncTask<Void,Void,Void> {

        String tag_id,username,id;

        public sendResponseBackUserEnrollment(String tag_id, String username,String id) {
            this.tag_id = tag_id;
            this.username = username;
            this.id=id;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String ip = sharedPreferences.getString("ip_address","null");

            String URL = GlobalData.getNewURl();

            if(!ip.equals("null"))
                URL = GlobalData.BASE_URL+ip+GlobalData.SUB_URL_2;

            Log.e("Feedback URL UE==>",URL);

           JSONObject object = new JSONObject();
            try {

                object.put("reader", "1");
                object.put("id", id);
                object.put("user", username);


            } catch (Exception e) {
                Log.e("Exception UE Json:==>", e.toString());
            }

            Log.e("Data Format:==> ", object.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Log.e("Feedback rspnse UE:==> ", response.toString());

                    try {
                        if (response.getInt("status")==0) {

                            Toast.makeText(MainActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MainActivity.this, "Try Again !!!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error UE:==> ", error.toString());
                }
            });



          /*  String ip = sharedPreferences.getString("ip_address","null");

            String URL = GlobalData.getNewURl();

            if(!ip.equals("null"))
                URL = GlobalData.BASE_URL+ip+GlobalData.SUB_URL_2;

            Log.e("Feedback URL UE==>",URL);

            StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    Log.e("Feedback Rspnse UE:==> ", response);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("UE error feedbk ==> ",error+"");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("reader", "1");
                    params.put("tag_id", tag_id);
                    params.put("user", username);
                    Log.e("Sending Data UE:==> ", params.toString());
                    return params;
                }
            };*/

            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(request);


            return null;
        }
    }





//*************    Fetching data for Tag and Username ******************* //

    //Receiving data from  Server / API URL 1
    class fetchData extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Checking Data....");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {



            String ip = sharedPreferences.getString("ip_address","null");

            String URL = GlobalData.getURL();
            if(!ip.equals("null"))
                URL = GlobalData.BASE_URL+ip+GlobalData.SUB_URL;

            Log.e("BASE URL==>",URL);

            StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {



                    Log.e("Simple Response:==> ", response);

                    if (response.length() < 3) {

                        if (dialog.isShowing())
                            dialog.dismiss();

                        if (!checkDate()) {
                            String table_name = "USERS";
                            String clearDBQuery = " DELETE FROM " + table_name;
                            database.execSQL(clearDBQuery);
                        }

                       // Toast.makeText(MainActivity.this, "No new Tag Found....", Toast.LENGTH_SHORT).show();
                        fetchDataFromSQL();

                    } else {

                        try {
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = new JSONObject(array.get(i).toString());

                                String id = object.getString("id");
                                String name = object.getString("user");
                                String tag_id = object.getString("tag_id");
                                String flag = object.getString("flag");
                                String insert_time = object.getString("insert_time");
                                details = new TagDetails(id, name, tag_id, flag, insert_time);
                                new sendFeedback().execute();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Please Check IP Address", Toast.LENGTH_SHORT).show();
                    txt_no_data_found.setVisibility(View.VISIBLE);
                    rv_main.setVisibility(View.GONE);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("reader", "0");
                    Log.e("Sending Data:==> ", params.toString());
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(request);
            return null;
        }
    }

    //Sending feedback again to the server when received the data
    class sendFeedback extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            String ip = sharedPreferences.getString("ip_address","null");

            String URL = GlobalData.getURL();

            if(!ip.equals("null"))
                URL = GlobalData.BASE_URL+ip+GlobalData.SUB_URL;

            Log.e("Feedback URL==>",URL);

            JSONObject object = new JSONObject();
            try {

                object.put("reader", "1");
                object.put("id", details.getId());


            } catch (Exception e) {
                Log.e("Exception :==>", e.toString());
            }


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

             Log.e("Feedback Response:==> ", response.toString());

             try {
                        if (response.getString("status").equals("0")) {

                            showDialog(details.getId(), details.getName(), details.getTag_id(), details.getFlag(), details.getInsertTime());

                        } else {
                            Toast.makeText(MainActivity.this, "Try Again !!!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.e("Error:==> ", error.toString());
                    Toast.makeText(MainActivity.this, "Please Check IP Address", Toast.LENGTH_SHORT).show();
                    txt_no_data_found.setVisibility(View.VISIBLE);
                    rv_main.setVisibility(View.GONE);
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(request);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }
    }

    //Dialog box if any new Tag found
    public void showDialog(final String id, final String name, final String tag_id, final String flag, final String insert_time) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_box);

        TextView btn_ok = dialog.findViewById(R.id.btn_ok);
        final TextView tv_tag_ig = dialog.findViewById(R.id.txt_tag);
        final TextView tv_username = dialog.findViewById(R.id.txt_username);

        tv_tag_ig.setText(tag_id);
        tv_username.setText(name);


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

                if (!checkDate()) {

                    String table_name = "USERS";
                    String clearDBQuery = " DELETE FROM " + table_name;
                    database.execSQL(clearDBQuery);

                }
                helper.insertData(id, name, tag_id, flag, insert_time, database);
                fetchDataFromSQL();
            }
        });

        dialog.show();
    }

    //Fetching Stored Users
    private void fetchDataFromSQL() {

        Cursor cursor = database.rawQuery("Select * from USERS", new String[]{});
        GlobalData.list.clear();

        if (cursor != null)
            if (cursor.moveToFirst()) {
                int i=1;
                do {
                    int sno = i++;
                    String id = cursor.getString(1);
                    String name = cursor.getString(2);
                    String tag_id = cursor.getString(3);
                    String flag = cursor.getString(4);
                    String insertTime = cursor.getString(5);

                    TagDetails details = new TagDetails(id, name, tag_id, flag, insertTime);
                    details.setS_no(sno);
                    GlobalData.list.add(details);

                } while (cursor.moveToNext());
            }


        if (GlobalData.list.size() == 0) {
            // Toast.makeText(this, "No record Found!", Toast.LENGTH_SHORT).show();
            txt_no_data_found.setVisibility(View.VISIBLE);
            rv_main.setVisibility(View.GONE);
        } else {
            txt_no_data_found.setVisibility(View.GONE);
            rv_main.setVisibility(View.VISIBLE);
            setRecyclerView();
        }
    }

    //Function for setting Recycler View
    private void setRecyclerView() {

        adapter = new CustomAdapter(this, GlobalData.list);
        rv_main.setHasFixedSize(true);
        rv_main.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_main.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }




//**************** Other Functions *************** //


    //Function for getting references of all data
    private void getReferences() {
        helper = new MySQLHelper(MainActivity.this);
        database = helper.getWritableDatabase();
        rv_main = findViewById(R.id.rv_main);
        ip_address = findViewById(R.id.ip_address);
        btn_save = findViewById(R.id.btn_save);
        settings_icon = findViewById(R.id.settings_icon);
        txt_no_data_found = findViewById(R.id.txt_no_data_found);
        dialog = new ProgressDialog(MainActivity.this);

        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
    }


    //Checking today's date for refresh list
    private boolean checkDate() {

        Calendar c = Calendar.getInstance();
        String oldDate = sharedPreferences.getString("date", "null");
        String todayDate = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH);

        Log.e("Old Date: ",oldDate);
        Log.e("Today's Date: ",todayDate);

        if (!oldDate.equals(todayDate)) {
            editor = sharedPreferences.edit();
            editor.putString("date", todayDate);
            editor.apply();
            return false;
        }

        return true;
    }


    //Setting Ip Address Data
    private void setIpAddress(String called_ip) {
        String ip = sharedPreferences.getString("ip_address","null");
        editor = sharedPreferences.edit();

        if(ip.equals("null")) {
            editor.putString("ip_address", GlobalData.getIP());
        }
        else{
            editor.putString("ip_address", called_ip);
        }

        editor.apply();

        Log.e("Ip==>","New "+ip);
    }


    //Function for all button clicks
    private void setBtnClicks() {

        settings_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btn_save.getVisibility() == View.VISIBLE) {
                    btn_save.setVisibility(View.GONE);
                    ip_address.setVisibility(View.GONE);
                } else {
                    btn_save.setVisibility(View.VISIBLE);
                    ip_address.setVisibility(View.VISIBLE);
                    String IP = sharedPreferences.getString("ip_address","null");

                    if(!IP.equals("null"))
                        ip_address.setText(IP);
                    else
                        ip_address.setText(GlobalData.getIP());

                    Log.e("Setting IP==>",IP);

                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIpAddress(ip_address.getText().toString());
                Toast.makeText(MainActivity.this, "IP Address Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Closing all handler on activity destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler_tag.removeCallbacks(runnable_tag);
        handler_ue.removeCallbacks(runnable_ue);
    }

}