package com.example.carpooltest1.Activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.carpooltest1.ArrayAdapters.ReviewsAdapter;
import com.example.carpooltest1.Handlers.RequestHandler;
import com.example.carpooltest1.Handlers.SharedPrefManager;
import com.example.carpooltest1.Models.Reviews;
import com.example.carpooltest1.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;
import static com.example.carpooltest1.Constants.Error.NED_LOCATION_MAIN_GATE;
import static com.example.carpooltest1.Constants.Error.URL_BROADCAST;
import static com.example.carpooltest1.Constants.Error.URL_GETREVIEWS;
import static com.example.carpooltest1.Constants.Error.URL_PROFILEGET;
import static com.example.carpooltest1.Constants.Error.URL_USERREQUESTGET;

public class Profile extends AppCompatActivity {
    ListView listView;
    List<Reviews> Reviewlist;
    TextView txt_name;
    TextView txt_department;
    TextView txt_Batch;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        txt_name = (TextView) findViewById(R.id.Name);
        txt_department = (TextView) findViewById(R.id.Department);
        txt_Batch = (TextView) findViewById(R.id.Batch);

        listView = (ListView) findViewById(R.id.reviews);
        Reviewlist = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PROFILEGET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //Load wala kaam
                            Log.d(TAG, "onResponse: " + response);
                            //setting up profile
                            txt_name.setText(jsonObject.getString("Name"));
                            String []ROLLBATCHDEPT = jsonObject.getString("DeptRollBatch").split("/");
                            String []ROLLDEPT = ROLLBATCHDEPT[0].split("-");
                            switch (ROLLDEPT[0]){
                                case "CS":
                                    txt_department.setText("Computer Systems Engineering");
                                    break;
                            }

                            txt_Batch.setText(ROLLBATCHDEPT[1]);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ID", SharedPrefManager.getInstance(getApplicationContext()).getUsername());
                params.put("Task", "1");
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest );

        ShowList();


    }
    private void ShowList() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETREVIEWS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        Log.d(TAG, "PFRAG onResponse: Response Recieved");
                        Reviews UserR;
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            //Load wala kaam
                            Log.d(TAG, "onResponse: " + response);
                            for(int i= 0;i < jsonArray.length();i++){

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                UserR = new Reviews(jsonObject.getString("Reviews_U"),
                                        jsonObject.getInt("Rating_U"),
                                        jsonObject.getString("UserID"));
                                Reviewlist.add(UserR);

                            }
                            ReviewsAdapter adapter = new ReviewsAdapter(Reviewlist, getApplicationContext());
                            listView.setAdapter(adapter);
                        }
                        catch (JSONException e) {
                            Log.d(TAG, "onResponse: "+e.getMessage());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "PFRAG onErrorResponse: "+ error.getMessage());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserID", SharedPrefManager.getInstance(getApplicationContext()).getUsername());
                params.put("Task", "1");
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);






    }
}
