package com.example.adminapp.Fragments;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.adminapp.Handlers.RequestHandler;



import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;
import static com.example.adminapp.Constants.Error.URL_VEHICLE_STATUS_CHECK;


import com.example.adminapp.R;

public class VerifyPerson extends AppCompatActivity {
    Spinner Spinner_VehicleType;
    private TextView txtVerif;
    private EditText edtName,edtPlate,driverid;
    private Button verifyInfo,search_person;
    int VERIFIED = 102;
    int NOT_VERIFIED = 4044;
    int STATUS_UPDATED = 104;
    int STATUS_UPDATE_FAILED = 404;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_person);
        Spinner_VehicleType = (Spinner) findViewById(R.id.VS_spin_type);
        pd = new ProgressDialog(this);
        pd.setMessage("Verifying");
        edtName = findViewById(R.id.VS_EDT_Name);
        edtPlate = findViewById(R.id.VS_EDT_PlateNo);
        txtVerif = findViewById(R.id.VS_TXT_Verif);
        driverid = findViewById(R.id.edit_ID);
        verifyInfo = findViewById(R.id.VS_BTN_Submit);
        search_person = findViewById(R.id.search_person);
        verifyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifydriver();
            }
        });
        search_person.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getInfo();
            }
        });
        ArrayAdapter<String> myadapt = new ArrayAdapter<String>(VerifyPerson.this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.vehicle_type));
        myadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner_VehicleType.setAdapter(myadapt);
        Spinner_VehicleType.setPrompt("Vehicle Type");
        Spinner_VehicleType.setEnabled(false);

    }
    private void getInfo(){


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_VEHICLE_STATUS_CHECK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            Log.d(TAG, "onResponse: " + response);

                            JSONObject jsonObject = new JSONObject(response);
                            //Load wala kaam
                            switch (jsonObject.getInt("Type")) {
                                case 1:
                                    Spinner_VehicleType.setSelection(0);
                                    break;
                                case 2:
                                    Spinner_VehicleType.setSelection(1);
                                    break;
                                default:
                                    Spinner_VehicleType.setSelection(2);
                                    break;
                            }
                            edtName.setText(jsonObject.getString("Name"));
                            edtPlate.setText(jsonObject.getString("PlateNo"));
                            if(jsonObject.getInt("Status") == VERIFIED){
                                Log.d(TAG, "onResponse: STATUSCHECK ALREADY VERIFIED");
                                txtVerif.setText("Verified");
                                verifyInfo.setEnabled(false);
                            }
                            else if(jsonObject.getInt("Status") == NOT_VERIFIED){
                                txtVerif.setText("Not Verified");
                                Log.d(TAG, "onResponse: STATUSCHECK VERIFICATION NEEDED");
                                verifyInfo.setEnabled(true);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error);
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ID", driverid.getText().toString());
                params.put("Task" , "0");
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
    private void verifydriver() {
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_VEHICLE_STATUS_CHECK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        //progressDialog.dismiss();
                        try {
                            Log.d(TAG, "onResponse: " + response);

                            JSONObject jsonObject = new JSONObject(response);
                            //Load wala kaam
                            if(jsonObject.getInt("Status") == STATUS_UPDATED){
                                Log.d(TAG, "onResponse: VERIFIED");
                                txtVerif.setText("Verified");

                            }
                            else if(jsonObject.getInt("Status") == STATUS_UPDATE_FAILED){
                                Toast.makeText(getApplicationContext(),"Could not verify",Toast.LENGTH_LONG).show();
                                txtVerif.setText("Not Verified");
                                Log.d(TAG, "onResponse: STATUSCHECK VERIFICATION NEEDED");


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error);
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ID", driverid.getText().toString());
                params.put("Task" , "2");
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    }
