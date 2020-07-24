package com.example.carpooltest1.Activities;

import android.app.ProgressDialog;
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
import com.example.carpooltest1.Handlers.RequestHandler;
import com.example.carpooltest1.Handlers.SharedPrefManager;
import com.example.carpooltest1.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;
import static com.example.carpooltest1.Constants.Error.INSERTION_FAILED;
import static com.example.carpooltest1.Constants.Error.INSERTION_SUCCESSFULL;
import static com.example.carpooltest1.Constants.Error.NO_VEHICLE_ENTRIES_FOUND;
import static com.example.carpooltest1.Constants.Error.URL_VEHICLE_STATUS_CHECK;
import static com.example.carpooltest1.Constants.Error.VEHICLE_NOT_VERIFIED;

public class Vehicle_Status extends AppCompatActivity {
    Spinner Spinner_VehicleType;
    private TextView txtVerif;
    private EditText edtName,edtPlate;
    private Button setInfo;
    int VehicleType;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle__status);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait!");

        Spinner_VehicleType = (Spinner) findViewById(R.id.VS_spin_type);
        edtName = findViewById(R.id.VS_EDT_Name);
        edtPlate = findViewById(R.id.VS_EDT_PlateNo);
        txtVerif = findViewById(R.id.VS_TXT_Verif);
        setInfo = findViewById(R.id.VS_BTN_Submit);
        setInfo.setEnabled(false);
        setInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertVehicle();
            }
        });


        edtName.setEnabled(false);
        edtPlate.setEnabled(false);

        ArrayAdapter<String> myadapt = new ArrayAdapter<String>(Vehicle_Status.this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Vehicle_Type));
        myadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner_VehicleType.setAdapter(myadapt);
        UpdateView();

    }

    private void insertVehicle(){
        progressDialog.show();
        switch (Spinner_VehicleType.getSelectedItem().toString()) {
            case "Air Conditioned":
                VehicleType = 1;
                break;
            case "Non Air Conditioned":
                VehicleType = 0;
                break;
            default:
                VehicleType = 2;
                break;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_VEHICLE_STATUS_CHECK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //Load wala kaam
                            Log.d(TAG, "onResponse: " + response);
                            if(jsonObject.getInt("Status") == INSERTION_FAILED){
                                Log.d(TAG, "onResponse: STATUSCHECK INSERTION_FAILED");
                            }
                            else if(jsonObject.getInt("Status") == INSERTION_SUCCESSFULL){
                                txtVerif.setText("Not Verified");
                                Log.d(TAG, "onResponse: STATUSCHECK INSERTION_SUCCESSFULL");
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
                params.put("ID", SharedPrefManager.getInstance(getApplicationContext()).getUsername());
                params.put("VehicleName",edtName.getText().toString());
                params.put("VehicleType",Integer.toString(VehicleType));
                params.put("PlateNo",edtPlate.getText().toString());
                params.put("Task" , "1");
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void UpdateView() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_VEHICLE_STATUS_CHECK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //Load wala kaam
                            Log.d(TAG, "onResponse: " + response);
                            if(jsonObject.getInt("Status") == NO_VEHICLE_ENTRIES_FOUND){
                                Log.d(TAG, "onResponse: STATUSCHECK NO_VEHICLE_ENTRIES_FOUND");
                                edtName.setEnabled(true);
                                edtPlate.setEnabled(true);
                                setInfo.setEnabled(true);
                                txtVerif.setText("Vehicle Status Verification Not Available");
                            }
                            else if(jsonObject.getInt("Status") == VEHICLE_NOT_VERIFIED){
                                txtVerif.setText("Vehicle Status Not Verified");
                                Log.d(TAG, "onResponse: STATUSCHECK VEHICLE_NOT_VERIFIED");
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
                params.put("ID", SharedPrefManager.getInstance(getApplicationContext()).getUsername());
                params.put("Task" , "0");
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest );
    }
}
