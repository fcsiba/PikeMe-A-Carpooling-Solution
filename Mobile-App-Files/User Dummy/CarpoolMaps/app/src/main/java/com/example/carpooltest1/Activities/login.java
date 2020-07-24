package com.example.carpooltest1.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import static com.example.carpooltest1.Constants.Error.URL_LOGIN;

public class login extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "login";
    private EditText edittextroll, edittextpassword;
    private Button buttonlogin;
    private ProgressDialog progressDialog;
    private Spinner Deptshow, Year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        edittextroll = (EditText) findViewById(R.id.edittextroll);
        edittextpassword = (EditText) findViewById(R.id.edittextpassword);
        buttonlogin = (Button) findViewById(R.id.buttonlogin);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        buttonlogin.setOnClickListener(this);
        edittextpassword = (EditText) findViewById(R.id.edittextpassword);
        Deptshow = (Spinner) findViewById(R.id.spinner1);

        Year = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> myadapt = new ArrayAdapter<String>(login.this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Departments));
        myadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Deptshow.setAdapter(myadapt);

        ArrayAdapter<String> myadapt1 = new ArrayAdapter<String>(login.this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Year));
        myadapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Year.setAdapter(myadapt1);
    }



    private void userLogin(){
        final String dept = Deptshow .getSelectedItem().toString();
        final String roll = edittextroll.getText().toString().trim();
        final int rollNo = Integer.parseInt(roll);
        final String year = Year.getSelectedItem().toString().trim();
        final String username = dept + "-" + rollNo + "/" + year;
        final String password = edittextpassword.getText().toString().trim();
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")) {
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .userLogin(obj.getString("Username"),
                                                obj.getString("email")
                                        );
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("DeptRollBatch" , username);
                params.put("Password" , password);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    @Override
    public void onClick(View v) {
        if(v == buttonlogin)
        {
            userLogin();
        }
    }
}