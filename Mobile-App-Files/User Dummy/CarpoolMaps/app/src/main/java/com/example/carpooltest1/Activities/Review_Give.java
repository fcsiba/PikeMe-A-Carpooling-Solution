package com.example.carpooltest1.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.carpooltest1.Handlers.RequestHandler;
import com.example.carpooltest1.Handlers.SharedPrefManager;
import com.example.carpooltest1.MainActivity;
import com.example.carpooltest1.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;
import static com.example.carpooltest1.Constants.Error.ID_KEY;
import static com.example.carpooltest1.Constants.Error.ID_STATUS;
import static com.example.carpooltest1.Constants.Error.URL_INSREVIEWS;
import static com.example.carpooltest1.Constants.Error.URL_LOGIN;
import static com.example.carpooltest1.Constants.Error.URL_PROFILEGET;

public class Review_Give extends AppCompatActivity {

    ImageView rating[] = new ImageView[5];
    EditText edt_review;
    TextView txt_name;
    TextView txt_department;
    TextView txt_Batch;
    String DriverID;
    String rating_f;
    Button send;
    String Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review__give);

        Intent intent = getIntent();
        DriverID = intent.getStringExtra(ID_KEY);
        Status = intent.getStringExtra(ID_STATUS);

        txt_name = findViewById(R.id.rev_Name);
        txt_department = findViewById(R.id.rev_Department);
        txt_Batch = findViewById(R.id.rev_Batch);
        send = findViewById(R.id.rev_submit_btn);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        URL_INSREVIEWS,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject obj = new JSONObject(response);
                                    Log.d(TAG, "onResponse Review give: " + response);
                                    }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("UserID" , SharedPrefManager.getInstance(getApplicationContext()).getUsername());
                        params.put("DriverID" , DriverID );
                        params.put("Reviews" , edt_review.getText().toString());
                        params.put("Rating" , rating_f);
                        params.put("Status" , Status);
                        params.put("Task" , "0");
                        return params;
                    }
                };

                RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
            }
        });
        Log.d(TAG, "onCreate: Status is" + Status);

        rating[0] = findViewById(R.id.star_1);
        rating[1] = findViewById(R.id.star_2);
        rating[2] = findViewById(R.id.star_3);
        rating[3] = findViewById(R.id.star_4);
        rating[4] = findViewById(R.id.star_5);

        rating[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating_f = "1";
                for(int i =0;i<5;i++){
                    rating[i].setImageResource(R.drawable.ic_star_off);

                }
                for(int i =0;i<1;i++){
                    rating[i].setImageResource(R.drawable.ic_star_on);
                }
            }
        });



        rating[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating_f = "2";
                for(int i =0;i<5;i++){
                    rating[i].setImageResource(R.drawable.ic_star_off);
                }
                for(int i =0;i<2;i++){
                    rating[i].setImageResource(R.drawable.ic_star_on);
                }
            }
        });



        rating[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating_f = "3";
                for(int i =0;i<5;i++){
                    rating[i].setImageResource(R.drawable.ic_star_off);
                }
                for(int i =0;i<3;i++){
                    rating[i].setImageResource(R.drawable.ic_star_on);
                }
            }
        });


        rating[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating_f = "4";
                for(int i =0;i<5;i++){
                    rating[i].setImageResource(R.drawable.ic_star_off);
                }
                for(int i =0;i<4;i++){
                    rating[i].setImageResource(R.drawable.ic_star_on);
                }
            }
        });


        rating[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating_f = "5";
                for(int i =0;i<5;i++){
                    rating[i].setImageResource(R.drawable.ic_star_off);
                }
                for(int i =0;i<5;i++){
                    rating[i].setImageResource(R.drawable.ic_star_on);
                }
            }
        });


        edt_review = findViewById(R.id.Review_give_edttxt_rv_usr);
        load_profile();
    }
    private void load_profile(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PROFILEGET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //Load wala kaam
                            Log.d(TAG, "onResponse PROFILE: " + response);
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
                params.put("ID", DriverID);
                params.put("Task", "1");
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest );
    }
}
