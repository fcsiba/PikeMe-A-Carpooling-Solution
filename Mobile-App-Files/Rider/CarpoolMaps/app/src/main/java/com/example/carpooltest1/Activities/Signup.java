package com.example.carpooltest1.Activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.carpooltest1.Handlers.RequestHandler;
import com.example.carpooltest1.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.carpooltest1.Constants.Error.URL_REGISTER;

public class Signup extends AppCompatActivity implements View.OnClickListener {

    public static final int PICK_IMAGE = 102;
    private Bitmap Image_user;

    private static final String TAG = "Signup";
    private EditText edittextname, edittextemail, edittextpassword, edittextage, editRoll, edittextPhone, edittextCNIC;
    private Button buttonregister;
    private  Button Signup_image;
    private ProgressDialog progressDialog;
    private TextView textviewLogin;
    private Spinner Depart,Gender,Year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Log.d(TAG, "onCreate: ");

        Signup_image = findViewById(R.id.Signup_Image);
        edittextemail = (EditText) findViewById(R.id.edittextemail);
        edittextname = (EditText) findViewById(R.id.edittextname);
        edittextpassword = (EditText) findViewById(R.id.edittextpassword);
        edittextage = (EditText) findViewById(R.id.edittextage);
        editRoll = (EditText) findViewById(R.id.editRoll);
        edittextPhone = (EditText) findViewById(R.id.edittextphone);
        edittextCNIC = (EditText) findViewById(R.id.edittextCNIC);
        textviewLogin = (TextView) findViewById(R.id.textviewLogin);
        buttonregister = (Button) findViewById(R.id.buttonregister);
        Depart = (Spinner) findViewById(R.id.spinner_depart);
        Gender = (Spinner) findViewById(R.id.spinner_gender);
        Year = (Spinner) findViewById(R.id.spinner_year);
        progressDialog = new ProgressDialog(this);
        buttonregister.setOnClickListener(this);
        textviewLogin.setOnClickListener(this);

        Signup_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), PICK_IMAGE);
            }
        });


        ArrayAdapter<String> department_adapter = new ArrayAdapter<String>(Signup.this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Department));
        department_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Depart.setAdapter(department_adapter);
        ArrayAdapter<String> year_adapter = new ArrayAdapter<String>(Signup.this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Year));
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Year.setAdapter(year_adapter);
        ArrayAdapter<String> Gender_adapter = new ArrayAdapter<String>(Signup.this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Gender));
        Gender_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Gender.setAdapter(Gender_adapter);
    }

    private void registeruser(){
        progressDialog.setMessage("Registering..");
        progressDialog.show();
        final String roll = editRoll.getText().toString().trim();
        final int rollNo = Integer.parseInt(roll);
        final String year = Year.getSelectedItem().toString().trim();
        final String gender = Gender.getSelectedItem().toString().trim();
        final String gender1 = "Male";
        final String department = Depart.getSelectedItem().toString().trim();
        final String email = edittextemail.getText().toString().trim();
        final String Phone = edittextPhone.getText().toString();
        final String CNIC = edittextCNIC.getText().toString();
        final String name = edittextname.getText().toString().trim();
        final String passsword = edittextpassword.getText().toString().trim();
        final String Age = edittextage.getText().toString().trim();
        String Dep = "";
        switch (department){
            case "Computer Systems Engineering":
                Dep = "CS";
                break;
            case "Mechanical Engineering":
                Dep = "ME";
                break;
            case "Electrical Engineering":
                Dep = "EE";
                break;
            case "Textile Engineering":
                Dep = "TE";
                break;
            case "Automotive Engineering":
                Dep = "AE";
                break;
            default:
                break;
        }

        final String username = Dep + "-" + rollNo + "/" + year;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //Load wala kaam
                            Log.d(TAG, "onResponse: " + response);
                            Toast.makeText(getApplicationContext(),jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("DeptRollBatch", username);
                params.put("Password", passsword);
                params.put("Name", name);
                params.put("Age", Age);
                params.put("Gender", (gender.equals(gender1) ? "1":"0"));
                params.put("Email" , email);
                params.put("Phone", Phone);
                params.put("CNIC", CNIC);;
                params.put("Image", "");
                params.put("task", "1");
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest );


    }

    @Override
    public void onClick(View v)
    {
        if(v == buttonregister)
            registeruser();
        if(v == textviewLogin){
            startActivity(new Intent (this, login.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            Uri SelectedImage = data.getData();
            try{
                Image_user = MediaStore.Images.Media.getBitmap(this.getContentResolver(), SelectedImage);
                Log.d(TAG, "onActivityResult: PICK_IMAGE : Image selected Successfully");
            }
            catch (FileNotFoundException err){

            }
            catch (IOException err){

            }
        }
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}