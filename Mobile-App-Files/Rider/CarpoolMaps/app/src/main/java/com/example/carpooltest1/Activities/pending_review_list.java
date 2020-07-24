package com.example.carpooltest1.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.carpooltest1.ArrayAdapters.ReviewListAdapter;
import com.example.carpooltest1.Handlers.RequestHandler;
import com.example.carpooltest1.Handlers.SharedPrefManager;
import com.example.carpooltest1.Models.ReviewList;
import com.example.carpooltest1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;
import static com.example.carpooltest1.Constants.Error.ID_KEY;
import static com.example.carpooltest1.Constants.Error.ID_STATUS;
import static com.example.carpooltest1.Constants.Error.URL_GETREVIEWS;
import static com.example.carpooltest1.Constants.Error.URL_GETREVIEWS_PENDING;

public class pending_review_list extends AppCompatActivity {
    private static final String TAG = "pending_review_list";
    ListView listView;
    List<ReviewList> Requestlist;
    int Status;
    boolean chk = true;

    ProgressDialog progressDialog;
    private static ReviewListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_review_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        listView = (ListView) findViewById(R.id.ReviewList);
        Requestlist = new ArrayList<>();
        ShowList();
        ActionBar a = getSupportActionBar();
        a.setTitle("Pending Reviews");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: CLICKED on " + Requestlist.get(position).getUserID());
                Intent in = new Intent(getApplicationContext(),Review_Give.class);
                in.putExtra(ID_KEY, Requestlist.get(position).getUserID());
                Status = Requestlist.get(position).getStatus();
                switch (Status){
                    case 5:
                        Status = 8;
                        break;
                    case 6:
                        Status = 9;
                        break;
                    case 7:
                        Status = 10;
                        break;
                    case 11:
                        Status = 14;
                        break;
                    case 12:
                        Status = 15;
                        break;
                    case 13:
                        Status = 16;
                        break;
                }
                Log.d(TAG, "onItemClick: Status is" + Status);
                if(!chk){
                    in.putExtra(ID_STATUS, Integer.toString(Status));
                    startActivity(in);
                }
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }


    private void ShowList() {

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETREVIEWS_PENDING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        ReviewList UserR;
                        Log.d(TAG, "PFRAG onResponse: Response Recieved");
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            //Load wala kaam
                            Log.d(TAG, "onResponse: " + response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                chk = false;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                UserR = new ReviewList(jsonObject.getString("UserID"),
                                        jsonObject.getString("Time"),
                                        jsonObject.getInt("Status"));
                                Requestlist.add(UserR);
                            }
                            if(chk){
                                UserR = new ReviewList("No Reviews pending",
                                        "",
                                        0);
                                Requestlist.add(UserR);
                            }
                            ReviewListAdapter adapter = new ReviewListAdapter(Requestlist, getApplicationContext());
                            listView.setAdapter(adapter);
                            progressDialog.dismiss();
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
                            Log.d(TAG, "getParams: " + SharedPrefManager.getInstance(getApplicationContext()).getUsername());
                            params.put("UserID", SharedPrefManager.getInstance(getApplicationContext()).getUsername());
                            params.put("Task", "1");
                            return params;
                        }
                    };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}