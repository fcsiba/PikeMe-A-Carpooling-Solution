package com.example.carpooltest1.Maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.SocketHandler;


import static com.example.carpooltest1.Constants.Error.COURSE_LOCATION;
import static com.example.carpooltest1.Constants.Error.DEFAULT_ZOOM;
import static com.example.carpooltest1.Constants.Error.Driver_Canceled_ride_User_driver_reviews_not_given;
import static com.example.carpooltest1.Constants.Error.Driver_Ended_ride_User_driver_reviews_not_given;
import static com.example.carpooltest1.Constants.Error.Driver_Ended_ride_driver_reviews_not_given;
import static com.example.carpooltest1.Constants.Error.FINE_LOCATION;
import static com.example.carpooltest1.Constants.Error.LOCATION_PERMISSION_REQUEST_CODE;
import static com.example.carpooltest1.Constants.Error.NED_LOCATION_MAIN_GATE;
import static com.example.carpooltest1.Constants.Error.NO_ENTRIES_FOUND;
import static com.example.carpooltest1.Constants.Error.REVIEW_INSERTION_FAILED;
import static com.example.carpooltest1.Constants.Error.Ride_Ended;
import static com.example.carpooltest1.Constants.Error.Ride_Started;
import static com.example.carpooltest1.Constants.Error.Ride_Started_bool;
import static com.example.carpooltest1.Constants.Error.STATUS_UPDATE_FAILED;
import static com.example.carpooltest1.Constants.Error.URL_BROADCAST;
import static com.example.carpooltest1.Constants.Error.URL_END_RIDE;
import static com.example.carpooltest1.Constants.Error.URL_STATUS_UPDATE;
import static com.example.carpooltest1.Constants.Error.URL_USERREQUESTGET;

public class PFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    Context context;
    GoogleMap map;

    private static final String TAG = "PFragment";

    private ProgressDialog progressDialog;

    private Boolean ToNed = false;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mfFusedLocationProviderClient;


    LatLng Source;
    LatLng Destination;
    LatLng [] waypoints_U = new LatLng[4];
    String [] UserIDs = new String[4];

    boolean [] users = new boolean[4];
    LinearLayout [] User_Liner_Layouts = new LinearLayout[4];
    Button [] User_Pickup_btn = new Button[4];
    Button [] User_cancel_btn = new Button[4];
    TextView []userlist = new TextView[4];
    boolean [] Ride = new boolean[4];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_p, container, false);


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "PFRAG onViewCreated: calling getDeviceLocation from OnViewCreated");

        //Initializing maps
        //Getting Location permissions and device location

        //TODO Ride Finished buttons and pickup buttons

        User_Liner_Layouts[0] = getView().findViewById(R.id.PFrag_Carpooler1);
        User_Liner_Layouts[1] = getView().findViewById(R.id.PFrag_Carpooler2);
        User_Liner_Layouts[2] = getView().findViewById(R.id.PFrag_Carpooler3);
        User_Liner_Layouts[3] = getView().findViewById(R.id.PFrag_Carpooler4);

        User_cancel_btn[0] = getView().findViewById(R.id.PFrag_Carpooler1_cancel_btn);
        User_cancel_btn[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusupdate(Driver_Canceled_ride_User_driver_reviews_not_given,0);
            }
        });
        User_cancel_btn[1] = getView().findViewById(R.id.PFrag_Carpooler2_cancel_btn);
        User_cancel_btn[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusupdate(Driver_Canceled_ride_User_driver_reviews_not_given,1);
            }
        });
        User_cancel_btn[2] = getView().findViewById(R.id.PFrag_Carpooler3_cancel_btn);
        User_cancel_btn[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusupdate(Driver_Canceled_ride_User_driver_reviews_not_given,2);
            }
        });
        User_cancel_btn[3] = getView().findViewById(R.id.PFrag_Carpooler4_cancel_btn);
        User_cancel_btn[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusupdate(Driver_Canceled_ride_User_driver_reviews_not_given,3);
            }
        });

        User_Pickup_btn[0] = getView().findViewById(R.id.PFrag_Carpooler1_pickup_btn);
        User_Pickup_btn[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Ride[0] == Ride_Started_bool){
                    statusupdate(Driver_Ended_ride_User_driver_reviews_not_given,0);
                }
                else{
                    statusupdate(Ride_Started,0);
                }

            }
        });
        User_Pickup_btn[1] = getView().findViewById(R.id.PFrag_Carpooler2_pickup_btn);
        User_Pickup_btn[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Ride[1] == Ride_Started_bool){
                    statusupdate(Driver_Ended_ride_User_driver_reviews_not_given,1);
                }
                else{
                    statusupdate(Ride_Started,1);
                }

            }
        });
        User_Pickup_btn[2] = getView().findViewById(R.id.PFrag_Carpooler3_pickup_btn);
        User_Pickup_btn[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Ride[2] == Ride_Started_bool){
                    statusupdate(Driver_Ended_ride_User_driver_reviews_not_given,2);
                }
                else{
                    statusupdate(Ride_Started,2);
                }

            }
        });
        User_Pickup_btn[3] = getView().findViewById(R.id.PFrag_Carpooler4_pickup_btn);
        User_Pickup_btn[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Ride[3] == Ride_Started_bool){
                    statusupdate(Driver_Ended_ride_User_driver_reviews_not_given,3);
                }
                else{
                    statusupdate(Ride_Started,3);
                }

            }
        });

        userlist[0] = getView().findViewById(R.id.PFrag_Carpooler1_txt);
        userlist[1] = getView().findViewById(R.id.PFrag_Carpooler2_txt);
        userlist[2] = getView().findViewById(R.id.PFrag_Carpooler3_txt);
        userlist[3] = getView().findViewById(R.id.PFrag_Carpooler4_txt);

        //Get All the associated users to the driver
        //TODO ADD A CHECK FOR NO USER PRESENT
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_USERREQUESTGET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        Log.d(TAG, "PFRAG onResponse: Response Recieved");

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            //Load wala kaam
                            Log.d(TAG, "onResponse: " + response);
                            for(int i= 0;i < jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                try{
                                    if(jsonObject.getInt("UserID") == NO_ENTRIES_FOUND){
                                        //progressDialog.dismiss();
                                        Log.d(TAG, "onResponse: No Entries Found");
                                        go_to_C();
                                    }
                                }
                                catch (JSONException e){
                                    Log.d(TAG, "onResponse: " + e.getMessage());
                                }
                                UserIDs[i] = jsonObject.getString("UserID");
                                if(Integer.parseInt(jsonObject.getString("Status")) == Ride_Started){
                                    Ride[i] = true;
                                    Log.d(TAG, "onResponse: Ride Started for " + Integer.toString(i));
                                }
                                else{
                                    Ride[i] = false;
                                }
                                waypoints_U[i] = new LatLng(jsonObject.getDouble("latitude_U"),jsonObject.getDouble("longitude_U"));
                                Log.d(TAG, "onResponse: "+ jsonObject.getDouble("latitude_U")+","+jsonObject.getDouble("longitude_U"));
                                if(Source == null){
                                    if(jsonObject.getInt("ToNED") == 1){
                                        Destination = NED_LOCATION_MAIN_GATE;
                                        Source = new LatLng(jsonObject.getDouble("latitude_D"), jsonObject.getDouble("longitude_D"));
                                        Log.d(TAG, "onResponse: Destination = " + Destination);
                                    }
                                    else{
                                        Source = NED_LOCATION_MAIN_GATE;
                                        Destination = new LatLng(jsonObject.getDouble("latitude_D"), jsonObject.getDouble("longitude_D"));
                                    }
                                }
                            }
                            Log.d(TAG, "PFRAG onMapReady: Calling UpdateMapViews Source is " + Destination);
                            UpdateMapsViews();
                            update_views();
                            Log.d(TAG, "PFRAG onMapReady: Called  UpdateMapViews" + Destination);
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: "+e.getMessage());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Log.d(TAG, "PFRAG onErrorResponse: "+ error.getMessage());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("DriverID", SharedPrefManager.getInstance(getContext()).getUsername());
                return params;
            }
        };
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest );


        initMap();
        getLocationPermission();
        getDeviceLocation();
        }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: Moving the camera to: lat:  " + latLng.latitude + " long: " + latLng.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        //hideSoftKeyboard();
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private void go_to_C() {
        Toast.makeText(getContext(),"No current Ride",Toast.LENGTH_LONG).show();
        MFragment mainFragment = new MFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainlayout,mainFragment).addToBackStack(null).commit();
    }

    //My Locations
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission:  Getting Location permissions");
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                Log.d(TAG, "getLocationPermission: Permission Granted");
                initMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }

        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Getting device's current location");
        mfFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (mLocationPermissionGranted) {

                Task location = mfFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: getDeviceLocation: Location found");
                            Location currentLocation = (Location) task.getResult();
                            //Toast.makeText(MapActivity.this, "Moving Camera", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: Moving camera to " + currentLocation.getLatitude() + "," + currentLocation.getLongitude());
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");

                        } else {
                            Log.d(TAG, "onComplete: Location not found set to NULL");
                            //Toast.makeText(MapActivity.this, "Current Lccation not found", Toast.LENGTH_SHORT);

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException : " + e.getMessage());
        }
    }

    private void statusupdate(final int Status, final int ind){

        final StringRequest string_request_insert_review = new StringRequest(Request.Method.POST, URL_END_RIDE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        Log.d(TAG, "PFRAG onResponse: Response REview Recieved" + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //Load wala kaam
                            Log.d(TAG, "onResponse: " + response);
                            if(jsonObject.getString("main") == String.valueOf(REVIEW_INSERTION_FAILED) || jsonObject.getString("main") == String.valueOf(NO_ENTRIES_FOUND)){
                                Log.d(TAG, "onResponse: Review Insertion Failed!" );
                            }
                            else{
                                Log.d(TAG, "onResponse: " + Integer.toString(Status));
                            }
                            update_views(Status,ind);
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: Status Update Failed! : "+e.getMessage());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Log.d(TAG, "PFRAG onErrorResponse: "+ error.getMessage());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("DriverID", SharedPrefManager.getInstance(getContext()).getUsername());
                params.put("UserID", UserIDs[ind]);
                return params;
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_STATUS_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        Log.d(TAG, "PFRAG onResponse: Response Recieved");

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //Load wala kaam
                            Log.d(TAG, "onResponse: " + response);
                            if(jsonObject.getString("Message") == String.valueOf(STATUS_UPDATE_FAILED)){
                                Log.d(TAG, "onResponse: Status Update Failed!");
                            }
                            else {
                                if (Status == Driver_Canceled_ride_User_driver_reviews_not_given || Status == Driver_Ended_ride_User_driver_reviews_not_given) {
                                    Log.d(TAG, "onResponse: Status = " + Status + " Updated And Inserting Reviews");
                                    RequestHandler.getInstance(getContext()).addToRequestQueue(string_request_insert_review);
                                }
                                else{
                                    update_views(Status,ind);
                                    Log.d(TAG, "onResponse: Status Updated");
                                }
                            }
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: Status Update Failed! : "+e.getMessage());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Log.d(TAG, "PFRAG onErrorResponse: "+ error.getMessage());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("DriverID", SharedPrefManager.getInstance(getContext()).getUsername());
                params.put("UserID", UserIDs[ind]);
                params.put("Status", String.valueOf(Status));
                return params;
            }
        };
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);


        update_views(Status,ind);
    }

    private void update_views(int Status,int ind){
        switch (Status){
            case Ride_Started:
                User_Pickup_btn[ind].setText("End");
                Ride[ind] = true;
                break;
            case Driver_Ended_ride_User_driver_reviews_not_given:
                User_Liner_Layouts[ind].setVisibility(View.INVISIBLE);
                break;
            case Driver_Canceled_ride_User_driver_reviews_not_given:
                User_Liner_Layouts[ind].setVisibility(View.INVISIBLE);
                break;
        }
    }
    private void update_views() {
        for (int i = 0; i < 4; i++) {
            if (UserIDs[i] != null) {
                if (Ride[i]) {
                    User_Pickup_btn[i].setText("End");
                    Log.d(TAG, "update_views: Setting End to index " + Integer.toString(i));
                } else {
                    User_Pickup_btn[i].setText("Pickup");
                }
            }
        }
    }

    //Maps Initialization
    private void initMap() {
        Log.d(TAG, "initMap: called");

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.PFrag_map1);
        supportMapFragment.getMapAsync(this);

    }

    private void UpdateMapsViews() {

        Log.d(TAG, "PFRAG UpdateMapsViews: clearing maps");
        map.clear();

        Log.d(TAG, "PFRAG UpdateMapsViews: maps cleared");


        for (LatLng wp:
                waypoints_U) {
            if (wp != null) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(wp)
                        .title("waypoint")
                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_dest));
                map.addMarker(markerOptions);
            }
////                    Src.setText(place.getName());
        }

        for(int i =0;i<UserIDs.length;i++){
            if(UserIDs[i]!=null){
                User_Liner_Layouts[i].setVisibility(View.VISIBLE);
                userlist[i].setText(UserIDs[i]);
            }
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .position(Source)
                .title("Source")
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_src));
        map.addMarker(markerOptions);

        markerOptions = new MarkerOptions()
                .position(Destination)
                .title("Destination")
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_src));
        map.addMarker(markerOptions);

        Log.d(TAG, "PFRAG UpdateMapsViews: Calling Polyline");
        DrawPolyLine();
        Log.d(TAG, "PFRAG UpdateMapsViews: " + Destination);

//        moveCamera(place.getLatLng(), DEFAULT_ZOOM, "Search");
////
//            MarkerOptions markerOptions = new MarkerOptions()
//                    .position(NED_LOCATION_MAIN_GATE)
//                    .title("Source")
//                    .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_dest));
//            map.addMarker(markerOptions);
////
////                    Src.setText(place.getName());
//            Destination = NED_LOCATION_MAIN_GATE;


//            Src.setText("NED University Main Gate");
//            Src.setTextSize(1,20);
////                    moveCamera(place.getLatLng(), DEFAULT_ZOOM, "Search");
//            MarkerOptions markerOptions = new MarkerOptions()
//                    .position(NED_LOCATION_MAIN_GATE)
//                    .title("Source")
//                    .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_src));
//            map.addMarker(markerOptions);
//            Source = NED_LOCATION_MAIN_GATE;
//        }
    }

    private void DrawPolyLine(){
        String url = getUrl(Source, Destination,waypoints_U);
        Log.d(TAG, "DrawPolyLine: Called!");
        Log.d(TAG, url.toString());
        FetchUrl FetchUrl = new FetchUrl();
        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        map.moveCamera(CameraUpdateFactory.newLatLng(Source));
        map.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }


//Waypoint Drawer

    private class FetchUrl extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
// Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
// Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
// Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
// Connecting to url
            urlConnection.connect();
// Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());
// Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());
            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
// Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
// Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
// Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
// Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLACK);
                Log.d("onPostExecute","onPostExecute lineoptions decoded");
            }
// Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                map.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    private String getUrl(LatLng origin, LatLng dest, LatLng [] waypoints) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String str_waypoints = "";
        if(waypoints != null){
            Log.d(TAG, "getUrl: in if condition of waypopints");
            for (LatLng wp:
                    waypoints) {
                Log.d(TAG, "getUrl: for condition");
                if(wp != null) {
                    if (str_waypoints == "") {
                        str_waypoints = "&waypoints=" + wp.latitude + "," + wp.longitude;
                    } else {
                        str_waypoints += "%7C" + wp.latitude + "," + wp.longitude;
                    }
                }
            }
        }

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + str_waypoints + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters +"&key=" + getResources().getString(R.string.google_maps_api_key);


        return url;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
